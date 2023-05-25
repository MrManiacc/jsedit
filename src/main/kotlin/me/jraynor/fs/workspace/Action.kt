package me.jraynor.fs.workspace

data class Action private constructor(val parameters: Map<String, Any>) {
    /**
     * Creates a json rpc message from this action
     */
    val jsonRPC: String
        get() {
            val rpc = toJson(this, false)
            val length = rpc.toByteArray().size
            return "Content-Length: $length\r\n\$rpc"
        }

    fun has(name: String): Boolean = parameters.containsKey(name)

    fun string(name: String): String = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as String

    fun int(name: String): Int = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Int

    fun bool(name: String): Boolean = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Boolean

    fun double(name: String): Double = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Double

    fun float(name: String): Float = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Float

    fun long(name: String): Long = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Long

    fun short(name: String): Short = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Short

    fun child(name: String): Builder = if (!parameters.containsKey(name)) error("No parameter with the name: $name")
    else parameters[name] as Builder


    companion object {
        fun builder(): Builder = Builder()

        fun of(build: Builder.() -> Unit): Action = builder().apply(build).build()
        fun action(name: String) = Builder().string("method", name).string("jsonrpc", "2.0")

        fun fromJson(json: String): Action = fromJsonHelper(json).build()

        fun toJson(action: Action, prettyPrint: Boolean = false): String {
            val builder = StringBuilder()
            builder.append("{")
            var count = 0
            for ((key, value) in action.parameters) {
                builder.append("\"$key\": ")
                when (value) {
                    is String -> builder.append("\"$value\"")
                    is Int -> builder.append(value)
                    is Boolean -> builder.append(value)
                    is Double -> builder.append(value)
                    is Float -> builder.append(value)
                    is Long -> builder.append(value)
                    is Short -> builder.append(value)
                    is Byte -> builder.append(value)
                    is Char -> builder.append(value)
                    is Unit -> builder.append("null")
                    is Action -> builder.append(toJson(value))
                    is Builder -> builder.append(toJson(value.build()))
                    else -> error("Unknown type: ${value::class.java}")
                }
                if (count++ < action.parameters.size - 1) builder.append(",")
            }
            builder.append("}")
            return if (prettyPrint) prettyFormatJson(builder.toString()) else builder.toString()
        }

        private fun prettyFormatJson(jsonIn: String): String {
            val json = jsonIn.trim()
            val builder = StringBuilder()
            var indent = 0
            var inString = false
            for (i in json.indices) {
                val c = json[i]
                val next = if (i < json.length - 1) json[i + 1] else null
                val last = if (i > 0) json[i - 1] else null
                if (inString) {
                    if (c == '"') {
                        inString = false
                    }
                    builder.append(c)
                    continue
                }
                when (c) {
                    '{', '[' -> {
                        builder.append(c)
                        if (next != '}' && next != ']') {
                            builder.append("\n")
                            indent++
                            addIndent(builder, indent)
                        }
                    }

                    '}', ']' -> {
                        if (last != '{' && last != '[') {
                            builder.append("\n")
                            indent--
                            addIndent(builder, indent)
                        }
                        builder.append(c)
                    }

                    ',' -> {
                        builder.append(c)
                        if (next != ' ' && next != '{' && next != '[' && next != '}' && next != ']') {
                            builder.append("\n")
                            addIndent(builder, indent)
                        }
                    }

                    '"' -> {
                        inString = true
                        builder.append(c)
                    }

                    else -> builder.append(c)
                }
            }
            return builder.toString()
        }

        private fun addIndent(builder: StringBuilder, indent: Int) {
            for (i in 0 until indent) {
                builder.append("    ")
            }
        }

        /**
         * manually parses the json string recursively
         */
        private fun fromJsonHelper(json: String): Builder {
            val builder = Builder()
            val json = json.trim()
            if (json.startsWith("{") && json.endsWith("}")) {
                val json = json.substring(1, json.length - 1)
                val split = json.split(",")
                for (s in split) {
                    val split = s.split(":")
                    val key = split[0].trim().replace("\"", "")
                    val value = split[1].trim()
                    if (value.startsWith("{") && value.endsWith("}")) {
                        builder.child(key, fromJsonHelper(value))
                    } else {
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            builder.string(key, value.substring(1, value.length - 1))
                        } else if (value == "true" || value == "false") {
                            builder.bool(key, value.toBoolean())
                        } else if (value.toIntOrNull() != null) {
                            builder.int(key, value.toInt())
                        } else if (value.toDoubleOrNull() != null) {
                            builder.double(key, value.toDouble())
                        } else if (value.toFloatOrNull() != null) {
                            builder.float(key, value.toFloat())
                        } else if (value.toLongOrNull() != null) {
                            builder.long(key, value.toLong())
                        } else if (value.toShortOrNull() != null) {
                            builder.short(key, value.toShort())
                        } else if (value.toByteOrNull() != null) {
                            builder.byte(key, value.toByte())
                        } else if (value.startsWith("'") && value.endsWith("'")) {
                            builder.char(key, value[1])
                        } else {
                            builder.nil(key)
                        }
                    }
                }
            }
            return builder
        }


    }

    class Builder internal constructor() {
        private val parameters: MutableMap<String, Any> = hashMapOf()


        fun child(name: String, other: Builder): Builder {
            parameters[name] = other
            return this
        }

        fun child(name: String, other: Action): Builder {
            val builder = Builder()
            builder.parameters.putAll(other.parameters)
            parameters[name] = builder
            return this
        }

        fun int(key: String, value: Int): Builder {
            parameters[key] = value
            return this
        }

        fun bool(key: String, value: Boolean): Builder {
            parameters[key] = value
            return this
        }


        fun double(key: String, value: Double): Builder {
            parameters[key] = value
            return this
        }

        fun float(key: String, value: Float): Builder {
            parameters[key] = value
            return this
        }

        fun long(key: String, value: Long): Builder {
            parameters[key] = value
            return this
        }

        fun short(key: String, value: Short): Builder {
            parameters[key] = value
            return this
        }

        fun byte(key: String, value: Byte): Builder {
            parameters[key] = value
            return this
        }

        fun char(key: String, value: Char): Builder {
            parameters[key] = value
            return this
        }

        fun string(key: String, value: String): Builder {
            parameters[key] = value
            return this
        }

        fun nil(key: String): Builder {
            parameters[key] = Unit
            return this
        }

        fun build(): Action = Action(parameters)
    }


    override fun toString(): String = toJson(this, true)
}
