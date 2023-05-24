package me.jraynor.os

import com.google.common.eventbus.EventBus
import me.jraynor.io.Folder
import org.graalvm.polyglot.PolyglotException

object Events {
    private val globalBus = EventBus()


    fun register(any: Any) = globalBus.register(any)

    fun unregister(any: Any) = globalBus.unregister(any)

    fun post(event: Any) = globalBus.post(event)

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

    object File {
        data class Renamed(val file: me.jraynor.io.File, val oldName: String)

        data class Moved(val from: Folder, val to: Folder, val file: me.jraynor.io.File)
    }

}
