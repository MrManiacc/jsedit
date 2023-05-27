package me.jraynor.vfs.impl

import me.jraynor.vfs.FileAccessor
import me.jraynor.vfs.FileAttributes
import me.jraynor.vfs.VFile
import me.jraynor.vfs.VPath
import java.io.File

class SysVFS(basePath: String = "/") : BaseVFS(VPath.of(basePath)), FileAccessor {

    constructor(file: File) : this(file.absolutePath)

    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override fun fileAccessor(): FileAccessor = this

    /**
     * Provides an implementation specific way to delete a file. This is used to delete a file from the file system.
     */
    override fun deleteFile(path: VPath): Boolean {
        return path.toFile().delete()
    }

    /**
     * Provides an implementation specific way to move a file. This is used to move a file from one location to another.
     *
     * All fileHandles that are open to the file that is being moved will be closed. You must reopen the file handle if you wish
     * by listening to the [VFSEvent.Type.MOVE] event.
     *
     *
     * @param path the path to the file that is being moved.
     * @param newPath the path to the new location of the file.
     * @return true if the file was moved successfully, false otherwise.
     */
    override fun moveFile(path: VPath, newPath: VPath): Boolean {
        return path.toFile().renameTo(newPath.toFile())
    }

    override fun read(file: VFile): ByteArray {
        return file.path.toFile().readBytes()
    }

    override fun write(file: VFile, data: ByteArray) {
        file.path.toFile().writeBytes(data)
    }

    /**
     * Reads the attributes of a file. This is used to get the attributes of a file.
     * @param file the file to read the attributes of.
     */
    override fun readAttribs(file: VFile): FileAttributes {
        return FileAttributes() + FileAttributes.READ_WRITE + FileAttributes.SYSTEM
    }

    /**
     * Provides an implementation specific way to list the children of a directory. This is used to list the children of a directory.
     * @param path the path to the directory.
     * @return a list of paths to the children of the directory.
     */
    override fun propagateChildren(path: VPath): List<VPath> {
        val file = path.toFile()
        if (!file.exists() || !file.isDirectory) return emptyList()
        return file.listFiles()?.map { VPath.of(it) } ?: emptyList()
    }

    private fun VPath.toFile(): File = if (System.getProperty("os.name")
            .contains("win", true)
    ) File("${this.path.substring(1, 2).uppercase()}:\\${this.path.substring(3).replace("/", "\\")}")
    else File(this.path)

}