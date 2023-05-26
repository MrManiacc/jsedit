package me.jraynor.vfs

/**
 * VFS sits between the userspace application and the physical file system. This is used to abstract the physical file system.
 * The implementation of this is used to determine how to read and write files. Also referred to as a "shim layer".
 */
abstract class VFS(val root: VFile) {

    /**
     *  This will open a reference to the file. This will load the file into memory and cache it.
     * Allow the file system to keep track of open handles to the file.
     */
    abstract fun open(path: VPath): VFile

    /**
     * Closes a reference to the file. This will get rid of the in memory representation of the file and clear the cache.
     * This will not delete the physical file from the disk. Removes the reference to the file from the cache.
     */
    abstract fun close(file: VFile)

    /**
     * Reads the contents of the file as a byte array.
     *
     * @return empty byte array if the file doesn't exist. The contents of the file otherwise.
     */
    abstract fun read(path: VFile): Doc

    /**
     * Writes the contents of the file as a byte array. If the file doesn't exist, it will be created.
     * @return false if the file is read only, true otherwise.
     */

    abstract fun write(file: VFile, data: Doc)

    /**
     * Deletes a file from the file system regardless of if it exists or not.
     * This will delete the physical file from the disk.
     * This will not delete the file from the cache. you must call [close] to remove the file from the cache.
     *
     * @return true if the file was deleted, false otherwise.
     */
    abstract fun delete(file: VFile): Boolean

    /**
     * Mounts a file system to the given path. This will mount the file system to the given path.
     * Returns a reference to the file system that was mounted in the form of a [VFile].
     */
    abstract fun mount(path: VPath, fs: VFS): VFile

}