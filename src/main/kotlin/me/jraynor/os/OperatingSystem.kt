package me.jraynor.os

import me.jraynor.io.*
import me.jraynor.vm.VirtualMachine
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.PolyglotException
import org.graalvm.polyglot.PolyglotException.StackFrame
import org.graalvm.polyglot.Source


class OperatingSystem(val disk: Disk) {
    private val vm = VirtualMachine(this)

    /**
     * Load from disk
     */
    fun load(name: String = "os.dat") {
        DiskIO.load(name, disk)
    }

    /**
     * Save to disk
     */
    fun save(name: String = "os.dat") {
        DiskIO.save(disk, name)
    }

    /**
     * Gets the current frame within the running context. This is expected to be called from within a javascript
     * context.
     */
    fun frame(): PolyglotException.StackFrame? {
        val e: PolyglotException = Context.getCurrent().asValue(RuntimeException()).`as`(PolyglotException::class.java)
        val frames = ArrayList<StackFrame>()
        for (frame in e.polyglotStackTrace) {
            if (frame.isGuestFrame) {
                frames.add(frame)
            }
        }
        return if (frames.size > 1) frames[1] else null
    }


    /**
     * Used for executing the CLI commands
     */
    fun execute(cmd: String) =
        execute(Source.newBuilder("js", cmd, "<CLI>").mimeType("application/javascript+module").build())

    /**
     * Execute a given file and load it as the file name within the language
     */
    fun execute(source: Source): Throwable? = vm.execute(source)



    /**
     * Execute a given file and load it as the file name within the language
     */
    fun execute(file: File): Throwable? = execute(
        Source.newBuilder("js", String(file.content), file.name)
            .mimeType("application/javascript+module").build()
    )

    /**
     * Save and shutdown the os
     */
    fun shutdown() {
        save()
        //TODO: further cleanup ?
    }
}