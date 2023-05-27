package me.jraynor.vfs

/**
 * VFS sits between the userspace application and the physical file system. This is used to abstract the physical file system.
 * The implementation of this is used to determine how to read and write files. Also referred to as a "shim layer".
 */
interface VFS {
    /**
     * The root of the file system. This is the root of the file system that is being abstracted.
     */
    val root: VFile

    /**
     * Indexes the file system. This will walk down each branch directory and open all files. to the file system.
     * it will not open any handles, only index the files.
     *
     * @param path the path to the file that is being indexed.
     */
    fun index(path: VPath)

    /**
     * Expects the user to manage the file handle. You may not call open again to the same path without closing the file first.
     * This means that you must call [close] before calling open again to the same path. You must keep track of your handle
     * instead of using this method as a means of retrieve a handle to the file "on the fly".
     *
     * @param path the path to the file that is being opened.
     * @param propagate if true, this will propagate recursively walk down each branch directory and open all files. to the file system.
     * it will not open any handles, only index the files.
     */
    fun open(path: VPath, propagate: Boolean = true): VHandle

    /**
     * Closes a reference to the file. This will get rid of the in memory representation of the file and clear the cache.
     * This will not delete the physical file from the disk. Removes the reference to the file from the cache.
     */
    fun close(handle: VHandle)

    /**
     * Looks up open handles to the file. This will return a list of open handles to the file.
     * If the file is not open, this will return an empty list.
     */
    fun lookup(path: VPath): List<VHandle>

    /**
     * Reads the contents of the file as a byte array.
     *
     * @return empty byte array if the file doesn't exist. The contents of the file otherwise.
     */
    fun read(handle: VHandle): Document

    /**
     * Writes the contents of the file as a byte array. If the file doesn't exist, it will be created.
     * @return false if the file is read only, true otherwise.
     */

    fun write(handle: VHandle, data: Document)

    /**
     * Deletes a file from the file system regardless of if it exists or not.
     * This will delete the physical file from the disk.
     * This will not delete the file from the cache. you must call [close] to remove the file from the cache.
     *
     * @return true if the file was deleted, false otherwise.
     */
    fun delete(handle: VHandle): Boolean

    /**
     * Moves a file from one path to another. This will move the file from one path to another.
     * All handles to the file will be updated to reflect the new path.
     */
    fun move(handle: VHandle, newPath: VPath): Boolean

    /**
     * Mounts a file system to the given path. This will mount the file system to the given path.
     * Returns a reference to the file system that was mounted in the form of a [VFile].
     */
    fun mount(path: VPath, fs: VFS): VFile

    /**
     * Add a listener to the file system. This will allow the file system to notify the listener when a file is changed.
     */
    fun addListener(listener: VFSListener): Boolean


}