package me.jraynor.fs

import me.jraynor.fs.File.Companion.EMPTY
import java.io.PrintStream

/**
 * Represents a file system. This is the root of the file system tree and contains all the files and folders.
 * This is also the entry point for all file system operations. This is the only class that should be exposed to
 * the outside world.
 *
 *
 * @param root The root folder of the file system
 * @param name The name of the file system
 * @param version The version of the file system
 * @param description The description of the file system
 *
 */
data class FileStore(
    val name: String, val version: Int, val description: String, val root: Folder = Folder.GLOBAL_ROOT
) {

    /**
     * Dumps the current file system out to printstream in a tree format
     *
     */
    fun dump(stream: PrintStream = System.out) {
        stream.println("Dumping file system: $name `$description``")   // Prints the name and description
        root.dump(stream)
    }

    /**
     * Takes in a given path and adds the file at that location, creating folders where needed. If the file already exists,
     * it is overwritten.
     */
    fun addFile(path: String, file: File): Boolean {
        var parent = findFile(path)
        if (parent !is Folder && parent != EMPTY) return false
        parent = if (parent == EMPTY) createFolder(path) else parent
        if (parent !is Folder) return false
        parent.addFile(file)
        return true
    }

    /**
     * Removes a file from the file system. If the file does not exist, false is returned.
     */
    fun removeFile(path: String): Boolean {
        val parent = findFile(path).parent ?: root
        return parent.removeFile(path.substringAfterLast("/"))
    }

    /**
     * This allows for moving of files. It will move the file from the given path to the new path. If the file does not
     * exist, false is returned. It can be used to rename files as well.
     */
    fun moveFile(from: String, to: String): Boolean {
        val name = if (from.contains("/")) to.substringAfterLast("/") else to
        val toPath = if (from.contains("/")) to.substringBeforeLast("/") else "/"
        val file = findFile(from)
        val fromParent = file.parent ?: root
        fromParent.removeFile(file)
        if (file == EMPTY) return false
        val parent = createFolder(toPath)
        parent.addFile(file)
        file.name = name
        return true
    }

    /**
     * This will create or get it iteraters from the root folder and creates the folders needed to get to the file.
     */
    private fun createFolder(path: String): Folder {
        if (path == "/" || path.isEmpty()) return root
        var currentFolder = root
        val path = if (path.endsWith("/")) path.substring(0, path.length - 1) else path
        for (part in path.split("/")) {
            if (part.isEmpty()) continue
            val file = currentFolder.getFile(part)
            currentFolder = if (file == EMPTY) {
                val newFolder = Folder(part, mutableSetOf())
                currentFolder.addFile(newFolder)
                newFolder
            } else {
                if (file is Folder) file
                else return currentFolder
            }
        }
        return currentFolder
    }

    /**
     * Takes in an urn and locates the associated file. If the file is not found, null is returned.
     * Converts the urn to a file offset within the file system tree by iterating through the urn and finding the
     * associated file.
     */
    fun findFile(urn: String): File {
        var currentFolder = root
        if (urn == "/") return currentFolder
        if (!urn.contains("/")) return currentFolder.getFile(urn)
        for (part in urn.split("/")) {
            if (part.isEmpty()) continue
            val file = currentFolder.getFile(part)
            if (file == EMPTY) return EMPTY
            if (file is Folder) {
                currentFolder = file
            } else {
                return file
            }
        }
        return currentFolder
    }

}