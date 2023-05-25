package me.jraynor.os.fs

import java.io.PrintStream
import java.util.LinkedList

object ConsoleStream : PrintStream(System.out) {
    private val lineList: MutableList<String> = LinkedList()
    val lines: Iterable<String> get() = lineList
    override fun println(x: Boolean) {
        processLine(x.toString())
    }

    fun clear(lastOnly: Boolean) {
        if (lineList.isEmpty()) return
        if (lastOnly)
            lineList.removeAt(lineList.size - 1)
        else lineList.clear()
    }

    inline fun iterateLines(block: (Int, String) -> Unit) =
        lines.forEachIndexed(block)

    fun clear(range: IntRange) {
        if (lineList.isEmpty()) return
        for (i in range) {
            if (i >= 0 && i < lineList.size)
                lineList.removeAt(i)
        }
    }

    override fun println(x: Char) {
        processLine(x.toString())
    }

    override fun println(x: CharArray) {
        processLine(String(x))
    }

    override fun println(x: Double) {
        processLine(x.toString())
    }

    override fun println(x: Float) {
        processLine(x.toString())
    }

    override fun println(x: Int) {
        processLine(x.toString())
    }

    override fun println(x: Long) {
        processLine(x.toString())
    }

    override fun println(x: Any?) {
        processLine(x.toString())
    }

    override fun println(s: String?) {
        processLine(s ?: "")
    }

    override fun println() {
        processLine("")
    }

    private fun processLine(line: String) {
        lineList.add(line)
        super.println(cleanLine(line))
    }

    /**
     * Cleans the line colors which are defined as $ followed by a hex color code.
     */
    private fun cleanLine(line: String):String = line.replace(Regex("\\$[0-9a-fA-F]{6}"), "")


    fun removeLine(index: Int) {
        if (index >= 0 && index < lineList.size) {
            lineList.removeAt(index)
        } else {
            throw IndexOutOfBoundsException("Invalid line index: $index")
        }
    }

    fun renderLine(index: Int): String {
        return if (index >= 0 && index < lineList.size) {
            lineList[index]
        } else {
            throw IndexOutOfBoundsException("Invalid line index: $index")
        }
    }


}
