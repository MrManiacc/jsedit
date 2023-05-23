package me.jraynor.os.event

import org.graalvm.polyglot.PolyglotException

object Events {

    object Console {
        enum class Level(val color: String) {
            INFO("\$bfbfbf"),
            WARN("\$ded707"),
            ERROR("\$ed0e37"),
            DEBUG("\$6293e3"),
            NONE("\$ffffff")
        }

        data class Log(
            val message: String,
            val level: Level = Level.INFO,
            val time: Boolean = false,
            val frame: PolyglotException.StackFrame?
        )

        data class Clear(val lastOnly: Boolean = false)

    }

}
