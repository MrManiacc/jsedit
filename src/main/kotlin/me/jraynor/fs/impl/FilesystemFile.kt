package me.jraynor.fs.impl

import me.jraynor.fs.Path
import me.jraynor.fs.VirtualFile
import me.jraynor.fs.VirtualFileException

class FilesystemFile(override val path: Path) : VirtualFile {

    /**
     * Reads the file as a byte array using the [path] to determine how to read the file.
     */
    override fun read(): ByteArray {
        FilesystemFile::class.java.getResourceAsStream(path.path)?.use {
            return it.readBytes()
        }
        throw VirtualFileException.FileNotFoundException(this)
    }

    /**
     * Returns true if the file exists, false otherwise.
     */
    override fun exists(): Boolean {
        //Checks in the current classpath for the file
        return FilesystemFile::class.java.getResourceAsStream(path.path) != null
    }

    /**
     * Returns true if the file is read only, false otherwise.
     */
    override fun isReadOnly(): Boolean = true

    /**
     * Returns true if the file is a directory, false otherwise.
     */
    override fun isDirectory(): Boolean = false

}