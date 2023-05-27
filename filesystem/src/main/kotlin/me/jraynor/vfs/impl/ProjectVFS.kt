package me.jraynor.vfs.impl

import me.jraynor.vfs.*

/**
 * Represents a VFS project. This is an in memory VFS meaning there's no physical representation of it within
 * a physical file disk that you could for example navigate to in Windows explorer.
 */
class ProjectVFS(workspacePath: VPath) : BaseVFS(workspacePath), FileAccessor {

    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override fun fileAccessor(): FileAccessor = this


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

    override fun read(file: VFile): ByteArray {
        val path = file.path
        TODO("Not yet implemented")
    }

    override fun write(file: VFile, data: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun readAttribs(file: VFile): FileAttributes = FileAttributes() +
            FileAttributes.READ_WRITE +
            FileAttributes.EXECUTE +
            FileAttributes.TEMPORARY

    /**
     * Provides an implementation specific way to list the children of a directory. This is used to list the children of a directory.
     * @param path the path to the directory.
     * @return a list of paths to the children of the directory.
     */
    override fun propagateChildren(path: VPath): List<VPath> {
        TODO()
    }


}