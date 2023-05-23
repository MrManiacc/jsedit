package me.jraynor.os

import me.jraynor.gui.Dockspace
import me.jraynor.os.disk.*
import org.graalvm.polyglot.HostAccess.Export
import org.luaj.vm2.LuaError
import java.util.concurrent.ConcurrentHashMap

class OperatingSystem(val disk: Disk, val view: Dockspace) {
    //    val lua = JsePlatform.standardGlobals()
    private val output: MutableList<String> = ArrayList()
    var updated = false
    private val vm = VirtualMachine(this)
    private var running = true
    private val tasks: MutableMap<String, Runner> = ConcurrentHashMap()

    @Export
    fun clear() {
        output.clear()
    }
    @Export
    fun clearLast() {
        if (output.isNotEmpty())
            output.removeAt(output.size - 1)
    }

    fun addUpdate(name: String, runnable: Runner): Boolean {
        if (tasks.containsKey(name))
            return false
        tasks[name] = runnable
        return true
    }

    fun removeUpdate(name: String): Boolean {
        return tasks.remove(name) != null
    }


    internal fun update() {
        for (update in tasks.values) update.execute(System.currentTimeMillis() / 1000f)
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
    fun log(string: String) {

        output.add(string)
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