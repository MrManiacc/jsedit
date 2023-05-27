package me.jraynor.vfs.impl

import me.jraynor.vfs.FileAccessor
import me.jraynor.vfs.VPath
import java.io.File

class JarVFS(jarPath: File) : BaseVFS(VPath.of(jarPath)) {

    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override fun fileAccessor(): FileAccessor {
        TODO("Not yet implemented")
    }

    /**
     * Provides an implementation specific way to delete a file. This is used to delete a file from the file system.
     */
    override fun deleteFile(path: VPath): Boolean {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

}