package me.jraynor.os

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.oracle.truffle.api.Truffle
import me.jraynor.gui.Dockspace
import me.jraynor.os.disk.*
import me.jraynor.os.event.Events
import me.jraynor.os.vm.VirtualMachine
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.PolyglotException
import org.graalvm.polyglot.PolyglotException.StackFrame
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap


class OperatingSystem(val disk: Disk, val view: Dockspace) {
    //    val lua = JsePlatform.standardGlobals()
    private val output: MutableList<String> = ArrayList()
    private var updated = false
    private val vm = VirtualMachine(this)
    private var running = true
    private val tasks: MutableMap<String, Runner> = ConcurrentHashMap()
    private val bus: EventBus = EventBus().apply { register(this@OperatingSystem) }
    fun subscribe(any: Any) =
        bus.register(any)

    fun post(any: Any) = bus.post(any)


    internal fun update() {
        for (update in tasks.values) update.execute(System.currentTimeMillis() / 1000f)
    }


    fun load(name: String = "os.dat") {
        DiskIO.load(name, disk)
    }

    fun save(name: String = "os.dat") {
        DiskIO.save(disk, name)
    }

    @Subscribe
    private fun clear(event: Events.Console.Clear) {
        if (event.lastOnly)
            if (output.isNotEmpty()) output.removeAt(output.size - 1) else return
        else output.clear()
    }

    fun getCurrentLocation(): PolyglotException.StackFrame? {
        val e: PolyglotException = Context.getCurrent().asValue(RuntimeException()).`as`(PolyglotException::class.java)
        val frames = ArrayList<StackFrame>()
        for (frame in e.polyglotStackTrace) {
            if (frame.isGuestFrame) {
                frames.add(frame)
            }
        }
        return if (frames.size > 1) frames[1] else null
    }

    @Subscribe
    private fun print(event: Events.Console.Log) {
        val caller =
            if (event.frame != null) {
                val pos = event.frame
                val caller = pos.sourceLocation

                "\$ffffff(\$d4d660${caller.source.name}:${caller.startLine}\$ffffff): "
            } else " "
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val now = time.format(formatter)
        val prefix = if (event.time) "\$54d676$now\$ffffff - " else " "
        val level =
            if (event.level != Events.Console.Level.NONE) "\$ffffff[${event.level.color}${event.level.name}\$ffffff] " else " "
        val message = "\$a1aeb5${event.message}"
        output.add("$level$prefix$caller$message")
        updated = true
    }

    /**
     * Returns true if the output has been updated since the last call to this function
     */
    internal fun isDirty(): Boolean {
        if (updated) {
            updated = false
            return true
        }
        return false
    }

    fun getOutput(): List<String> {
        return output
    }

    /**
     * Used for executing the CLI commands
     */
    fun execute(cmd: String) {
        org.graalvm.polyglot.Source.newBuilder("js", cmd, "CLI.mjs").mimeType("application/javascript+module").build()
    }

    /**
     * Execute a given file and load it as the file name within the language
     * Execute a given file and load it as the file name within the language
     */
    fun execute(file: File): Throwable? = vm.execute(
        org.graalvm.polyglot.Source.newBuilder("js", String(file.content), file.name)
            .mimeType("application/javascript+module").build()
    )


    fun shutdown() {
        if (running) {
            save()
//            vm.()
            running = false
        } else save()
    }
}