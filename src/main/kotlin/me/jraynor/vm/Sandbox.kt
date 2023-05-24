package me.jraynor.vm

import org.graalvm.polyglot.Context
import java.util.function.Predicate

class Sandbox private constructor() {
    private val allowed: MutableSet<String> = hashSetOf()
    private val disallowed: MutableSet<String> = hashSetOf()
    private val options: MutableMap<String, Any> = hashMapOf()

    class Builder {
        private val sandbox = Sandbox()

        fun option(key: String, value: Any): Builder {
            sandbox.options[key] = value
            return this
        }

        fun allow(pattern: String): Builder {
            sandbox.allowed.add(pattern)
            return this
        }

        fun disallow(pattern: String): Builder {
            sandbox.disallowed.add(pattern)
            return this
        }

        fun build(contextBuilder: Context.Builder): Context.Builder {
            val filter: Predicate<String> = Predicate { className ->
                sandbox.allowed.any { pattern ->
                    if (pattern.endsWith(".*")) className.startsWith(pattern.dropLast(2))
                    else className == pattern
                } && sandbox.disallowed.none { pattern ->
                    if (pattern.endsWith(".*")) className.startsWith(pattern.dropLast(2))
                    else className == pattern
                }
            }
            for ((key, value) in sandbox.options)
                contextBuilder.option(key, value.toString())
//            return contextBuilder.hostClassFilter(filter)
            return contextBuilder
        }
    }
}
