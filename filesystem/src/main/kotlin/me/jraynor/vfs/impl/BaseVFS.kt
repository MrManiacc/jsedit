package me.jraynor.vfs.impl

import me.jraynor.vfs.*
import java.io.PrintStream

/**
 * This is the base implementation of the VFS. This is the base class for all VFS implementations.
 * This class provides the basic functionality for all VFS implementations. This class is responsible for
 * managing the file cache, the handle cache, and the subscriber cache. This class also provides the basic
 * functionality for opening, closing, reading, and writing to a file.
 *
 * @param path the path to the root of the file system.
 * @see VFS
 */
abstract class BaseVFS(protected val path: VPath) : VFS {
    /**
     * The root of the file system. This is the root of the file system that is being abstracted.
     */
    final override val root: VFile = VFile(path, this)

    //stores all files that are open within the project. A file must be open to be able to read from it.
    private val cachedFiles: MutableMap<VPath, VFile> = hashMapOf(path to root)

    //stores all handles that are open within the project. A VFile must have an open handle to be able to read from it.
    private val cachedHandles: MutableMap<VFile, MutableSet<VHandle>> = hashMapOf()

    //stores all subscribers to a file. A file must have a subscriber to be able to receive events.
    private val subscribers: MutableSet<VFSListener> = hashSetOf()


    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    protected abstract fun fileAccessor(): FileAccessor

    /**
     * Provides an implementation specific way to delete a file. This is used to delete a file from the file system.
     * All fileHandles that are open to the file that is being deleted will be closed. And the cached file will be removed.
     */
    protected abstract fun deleteFile(path: VPath): Boolean

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
    protected abstract fun moveFile(path: VPath, newPath: VPath): Boolean

    /**
     * Add a listener to the file system. This will allow the file system to notify the listener when a file is changed.
     */
    override fun addListener(listener: VFSListener): Boolean = subscribers.add(listener)


    /**
     * Expects the user to manage the file handle. You may not call open again to the same path without closing the file first.
     * This means that you must call [close] before calling open again to the same path. You must keep track of your handle
     * instead of using this method as a means of retrieve a handle to the file "on the fly".
     *
     * @param path the path to the file that is being opened.
     * @param propagate if true, this will propagate recursively walk down each branch directory and open all files. to the file system.
     * it will not open any handles, only index the files.
     */
    override fun open(path: VPath, propagate: Boolean): VHandle {
        var currentFile =
            if (cachedFiles.containsKey(path)) cachedFiles[path]!! else buildPathAndReturnParent(path)
        //iterate up the tree until we reach the root.
        while (currentFile.path.toPath() != root.path.toPath() && currentFile.path.path.isNotEmpty())
            currentFile = buildPathAndReturnParent(currentFile.path)
        //At this point there should be a tree of files going from the root to that given path.
        val file = cachedFiles.getOrElse(path) { throw IllegalStateException("Failed to build path to file!") }
        val handlesForFile = cachedHandles.getOrPut(file) { hashSetOf() }
        val handle = VHandle(file, fileAccessor())
        handlesForFile.add(handle)
        subscribers.forEach {
            it.onEvent(createEventFor(file, VFSEvent.Type.OPEN))
        }
        return handle
    }

    /**
     * Used to locate a file within the file system. This will return null if the file does not exist.
     */
    internal fun find(path: VPath): VFile? = cachedFiles[path]

    override fun lookup(path: VPath): List<VHandle> {
        val file = cachedFiles.getOrElse(path) { return emptyList() }
        val handles =
            cachedHandles.getOrElse(file) { return emptyList() }
        return handles.toList()
    }


    /**
     * Closes a reference to the file. This will get rid of the in memory representation of the file and clear the cache.
     * This will not delete the physical file from the disk. Removes the reference to the file from the cache.
     *
     */
    override fun close(handle: VHandle) {
        if (!cachedFiles.containsKey(handle.reference.path)) throw IllegalStateException("Attempted to close uncached file!")
        if (!cachedHandles.containsKey(handle.reference)) throw IllegalStateException("Attempted to close uncached handle!")
        val handleForFile = cachedHandles[handle.reference]!!
        handleForFile.remove(handle)
        handle.dispose()
        //Dispose of the file handle set if there are no more handles for that file.
        if (handleForFile.isEmpty())
            cachedHandles.remove(handle.reference)
        subscribers.forEach {
            it.onEvent(createEventFor(handle.reference, VFSEvent.Type.CLOSE))
        }
    }

    /**
     * Reads the contents of the file as a byte array.
     *
     * @return empty byte array if the file doesn't exist. The contents of the file otherwise.
     */
    override fun read(handle: VHandle): Document {
        if (!cachedHandles.containsKey(handle.reference)) throw IllegalStateException("Attempted to read a closed file!")
        if (!cachedHandles[handle.reference]!!.contains(handle)) throw IllegalStateException("Attempted to read from a closed file handle!")
        val read = handle.read()
        subscribers.forEach { it.onEvent(createEventFor(handle.reference, VFSEvent.Type.READ)) }
        return read
    }

    /**
     * Writes the contents of the file as a byte array. If the file doesn't exist, it will be created.
     * @return false if the file is read only, true otherwise.
     */
    override fun write(handle: VHandle, data: Document) {
        if (!cachedHandles.containsKey(handle.reference)) throw IllegalStateException("Attempted to write to a closed file!")
        if (!cachedHandles[handle.reference]!!.contains(handle)) throw IllegalStateException("Attempted to read from a closed file handle!")
        handle.write(data)
        subscribers.forEach { it.onEvent(createEventFor(handle.reference, VFSEvent.Type.WRITE)) }
    }


    /**
     * Deletes a file from the file system regardless of if it exists or not.
     * This will delete the physical file from the disk.
     * This will not delete the file from the cache. you must call [close] to remove the file from the cache.
     *
     * @return true if the file was deleted, false otherwise.
     */
    override fun delete(handle: VHandle): Boolean {
        if (!cachedFiles.containsKey(handle.reference.path)) throw IllegalStateException("Attempted to delete unindexed file!")
        if (cachedHandles.containsKey(handle.reference)) throw IllegalStateException("Attempted to delete a file with open handles!")
        val deleted = deleteFile(handle.reference.path)
        subscribers.forEach { it.onEvent(createEventFor(handle.reference, VFSEvent.Type.DELETE)) }
        return deleted
    }


    /**
     * Moves a file from one path to another. This will move the file from one path to another.
     * All handles to the file will be updated to reflect the new path.
     *
     * @param handle the handle to the file to move.
     * @param newPath the new path to move the file to. Should be a directory not a new file name.
     */
    override fun move(handle: VHandle, newPath: VPath): Boolean {
        if (!cachedFiles.containsKey(handle.reference.path)) throw IllegalStateException("Attempted to move unindexed file!")
        if (!cachedHandles.containsKey(handle.reference)) throw IllegalStateException("Attempted to move a file with open handles!")
        if (!cachedHandles[handle.reference]!!.contains(handle)) throw IllegalStateException(
            "Attempted to move a file with a closed handle!"
        )
        val reference = handle.reference
        val newFile = buildPathAndReturnParent(newPath)
        val oldParent = reference.parent
        oldParent?.children?.remove(reference)
        newFile.children.add(reference)
        reference.parent = newFile
        reference.path = newPath
        subscribers.forEach { it.onEvent(createEventFor(reference, VFSEvent.Type.MOVE)) }
        return true
    }


    /**
     * Mounts a file system to the given path. This will mount the file system to the given path.
     * Returns a reference to the file system that was mounted in the form of a [VFile].
     */
    override fun mount(path: VPath, fs: VFS): VFile {
        TODO("Not yet implemented")
    }

    private fun buildPathAndReturnParent(path: VPath): VFile {
        if (path == this.path || path.path.isEmpty())
            return root
        val parent = createVFile(path.parent)
        val file = createVFile(path)
        file.parent = parent
        parent.children.add(file)
        propagateFile(file)
        return parent
    }

    /**
     * Recursively propagates the file system from the given path. This will create a file for each file in the file system.
     * This will also create a file for each directory in the file system.
     */
    private fun propagateFile(file: VFile) {
        val accessor = fileAccessor()
        val children = accessor.propagateChildren(file.path)
        for (child in children) {
            buildPathAndReturnParent(child)
        }
    }

    /**
     * Creates an event for the given path and type. This will create an event for the given path and type.
     */
    private fun createEventFor(path: VFile, type: VFSEvent.Type): VFSEvent = VFSEvent(path, this, type) {
        lookup(path.path)
    }

    /**
     * Creates or caches a file for the given path. This will create a file for the given path if it doesn't exist.
     * If the file already exists, it will be returned from the cache. Subscribers will be notified of the creation of the file.
     * @return the file for the given path.
     */
    private fun createVFile(path: VPath): VFile {
        val file = cachedFiles.getOrPut(path) { VFile(path, this) }
        val event = createEventFor(file, VFSEvent.Type.CREATE)
        subscribers.forEach {
            it.onEvent(event)
        }
        return file
    }

    /**
     * Prints out the folder structure of the file system recursively by starting from the root.
     * Also dumps all active handles in a pretty format.
     */
    internal fun dump(): String {
        val builder = StringBuilder()
        builder.appendLine("Dumping file system:")
        builder.appendLine("Root: ${root.path}")
        builder.appendLine("Files:")
        root.dump(builder, 0)
        builder.appendLine("Handles:")
        cachedHandles.forEach { (file, handles) ->
            builder.appendLine("\t${file.path}")
            handles.forEach {
                builder.appendLine("\t\t${it.accessor::class.simpleName}")
            }
        }
        return builder.toString()
    }
}