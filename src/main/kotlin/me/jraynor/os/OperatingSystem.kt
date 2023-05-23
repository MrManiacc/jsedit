package me.jraynor.os

import me.jraynor.gui.Dockspace
import me.jraynor.os.disk.*
import org.graalvm.polyglot.HostAccess.Export
import org.luaj.vm2.LuaError

class OperatingSystem(val disk: Disk, val view: Dockspace) {
    //    val lua = JsePlatform.standardGlobals()
    private val output: MutableList<String> = ArrayList()
    var updated = false
    private val vm = VirtualMachine(this)
    private var running = true
    private val updates = mutableListOf<Runner>()
    fun clear() {
        output.clear()
    }

    fun addUpdate(runnable: Runner) {
        updates.add(runnable)
    }


    internal fun update() {
        for (update in updates) update.execute(System.currentTimeMillis() / 1000f)
    }

    fun runCommand(cmd: String) {
        vm.execute(cmd)
    }

    fun load(name: String = "os.dat") {
        DiskIO.load(name, disk)
    }

    fun save(name: String = "os.dat") {
        DiskIO.save(disk, name)
    }
    @Export
    fun callback(runnable: Runnable) {
        runnable.run()
    }

    @Export
    fun log(string: String, vararg args: Any) {
        val formattedDateTime: String =
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        output.add("[$formattedDateTime] $string")
    }

    fun getOutput(): List<String> {
        return output
    }

    fun execute(file: File): Map<Int, String> {
        return vm.execute(file)
    }

    private fun buildErrorMap(luaError: LuaError): Map<Int, String> {
        val lines = luaError.message?.split("\n") ?: error("unknown lua error: ${luaError.message}")
        val last = lines.last().trim()
        if (last.startsWith(":")) {
            val lineNum = last.substringAfter(":").substringBefore(" ").trim().toIntOrNull() ?: 0
            val message = last.substringAfter(" ")
            return mapOf(lineNum to message)
        }
        return emptyMap()
    }

    fun shutdown() {
        if (running) {
            save()
            vm.shutdown()
            running = false
        } else save()
    }
}