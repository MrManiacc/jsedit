package me.jraynor.vfs.impl

import me.jraynor.vfs.Document
import me.jraynor.vfs.VHandle

class SourceDocument(handle: VHandle) : Document(handle, ByteArray(0)), Iterable<String> {

    private val sourceCodeLines = linkedMapOf<Int, String>()

    /**
     * Initializes the document with the given data provided by the handle.
     */
    init {
        read()
    }

    /**
     * Writes the given data to the file. This will overwrite the contents of the file.
     */
    override fun write() {
        val builder = StringBuilder()
        sourceCodeLines.forEach { builder.append(it).append("\n") }
        documentData = builder.toString().toByteArray()
        super.write()
    }

    /**
     * Reads the file and returns the data as a byte array.
     */
    override fun read() {
        super.read()
        //TODO: This is a hacky way to do this. We need to find a better way to do this.
        String(documentData).split("\n").forEachIndexed { index, s -> sourceCodeLines[index] = s }
    }

    fun setSource(source: String) {
        sourceCodeLines.clear()
        source.split("\n").forEachIndexed { index, s -> sourceCodeLines[index] = s }
    }

    operator fun set(line: Int, data: String) {
        sourceCodeLines[line] = data
    }

    operator fun get(line: Int): String? {
        return sourceCodeLines[line]
    }


    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<String> = sourceCodeLines.values.iterator()


    override fun equals(other: Any?): Boolean {
        //Trying something funky here. If the other is a string, we will set the source to the string and return true.
        if (other is String) {
            setSource(other)
            return true
        }
        return super.equals(other)
    }

}