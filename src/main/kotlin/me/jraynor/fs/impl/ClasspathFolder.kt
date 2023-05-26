package me.jraynor.fs.impl

import me.jraynor.fs.Path
import me.jraynor.fs.VirtualFile
import me.jraynor.fs.VirtualFileException
import me.jraynor.fs.VirtualFolder
import kotlin.io.path.Path

class ClasspathFolder(override val path: Path) : VirtualFolder {
    /**
     * Lists the files in the folder using the [path] to determine how to list the files.
     */
    override fun listFiles(): List<VirtualFile> {
        val files = mutableListOf<VirtualFile>()
        ClasspathFolder::class.java.getResourceAsStream(path.path)?.use {
            it.bufferedReader().use { reader ->
                reader.lines().forEach { line ->
                    val file = ClasspathFile(path.fs.path(line))
                    if (file.exists())
                        files.add(file)
                }
            }
        }
        return files
    }

    /**
     * Reads a file at the given [path] using the [path] to determine how to read the file.
     */
    override fun readFile(path: Path): VirtualFile {
        TODO("Not yet implemented")
    }

    /**
     * Reads the file as a byte array using the [path] to determine how to read the file.
     */
    override fun read(): ByteArray {
        TODO("Not yet implemented")
    }

    /**
     * Returns true if the file exists, false otherwise.
     */
    override fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns true if the file is read only, false otherwise.
     */
    override fun isReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns true if the file is a directory, false otherwise.
     */
    override fun isDirectory(): Boolean {
        TODO("Not yet implemented")
    }


}