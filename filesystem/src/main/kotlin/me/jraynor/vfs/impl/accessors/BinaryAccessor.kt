package me.jraynor.vfs.impl.accessors

import me.jraynor.vfs.*

class BinaryAccessor: FileAccessor {

    /**
     * Reads the data from the file. This is used to read data from a file.
     * @param file the file to read the data from.
     * @return the data that was read from the file.
     */
    override fun read(file: VHandle): ByteArray {
        TODO("Not yet implemented")
    }

    /**
     * Writes the data to the file. This is used to write data to a file.
     * @param file the file to write the data to.
     * @param data the data to write to the file.
     */
    override fun write(file: VHandle, data: ByteArray) {
        TODO("Not yet implemented")
    }

    /**
     * Reads the attributes of a file. This is used to get the attributes of a file.
     * @param file the file to read the attributes of.
     */
    override fun attribs(file: VFile): FileAttributes {
        TODO("Not yet implemented")
    }

    /**
     * Index's an entire directory. It recursively indexes all the children of the directory. Caching their internally.
     * This is used to index a directory
     * @param path the path to the directory.
     * @return a list of paths to the children of the directory.
     */
    override fun index(path: VPath): List<VFile> {
        TODO("Not yet implemented")
    }

    /**
     * Provides an implementation specific way to delete a file. This is used to delete a file from the file system.
     * All fileHandles that are open to the file that is being deleted will be closed. And the cached file will be removed.
     * You must reopen the file handle if you wish by listening to the [VFSEvent.Type.DELETE] event.
     *
     * @param path the path to the file that is being deleted.
     * @return true if the file was deleted successfully, false otherwise.
     *         (If the file does not exist or if the file has a read only attribute, it will return false )
     */
    override fun delete(path: VPath): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Provides an implementation specific way to move a file. This is used to move a file from one location to another.
     *
     * All fileHandles that are open to the file that is being moved will be closed. You must reopen the file handle if you wish
     * by listening to the [VFSEvent.Type.MOVE] event.
     *
     * Will override the file if it already exists in the index by updating its cached document and write to file system.
     *
     * @param path the path to the file that is being moved.
     * @param newPath the path to the new location of the file.
     * @return true if the file was moved successfully, false otherwise.
     */
    override fun move(path: VPath, newPath: VPath): Boolean {
        TODO("Not yet implemented")
    }

}