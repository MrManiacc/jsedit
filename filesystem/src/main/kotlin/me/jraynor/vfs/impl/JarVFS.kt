package me.jraynor.vfs.impl

import me.jraynor.vfs.*
import me.jraynor.vfs.impl.accessors.JarAccessor
import java.io.InputStream

class JarVFS(jarPath: VPath) : BaseVFS<JarAccessor>(VPath.of("/")) {
    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override val accessor: JarAccessor = JarAccessor(jarPath.toFile(), ::cache)

    /**
     * Expects the user to manage the file handle. You may not call open again to the same path without closing the file first.
     * This means that you must call [close] before calling open again to the same path. You must keep track of your handle
     * instead of using this method as a means of retrieve a handle to the file "on the fly".
     *
     * @param pathIn the path to the file that is being opened.
     * @param propagate if true, this will propagate recursively walk down each branch directory and open all files. to the file system.
     * it will not open any handles, only index the files.
     */
    override fun open(pathIn: VPath, propagate: Boolean): VHandle = with(super.open(pathIn, propagate)) {
        //Accesses the jar file from the accessor
        val jarFile = this@JarVFS.accessor.jarFile
        // Remove the leading slash if it exists
        if (!contains(pathIn))
            throw IllegalArgumentException("The path $path does not exist in the jar file ${jarFile.name}")
        if (!meta.contains("stream")) {
            val path = if (pathIn.path.startsWith("/")) pathIn.path.substring(1) else pathIn.path
            meta["stream"] = jarFile.getInputStream(jarFile.getEntry(path))
        }
        this
    }

    override fun contains(path: VPath): Boolean {
        val p = if (path.path.startsWith("/")) path.path.substring(1) else path.path
        return this@JarVFS.accessor.jarFile.entries().toList().any { it.name == p }
    }

    /**
     * Closes a reference to the file. This will get rid of the in memory representation of the file and clear the cache.
     * This will not delete the physical file from the disk. Removes the reference to the file from the cache.
     *
     */
    override fun close(handle: VHandle) {
        // Close the stream if it exists and remove it from the metadata
        with(handle.meta) {
            getAs<InputStream>("stream")?.close()
            remove("stream")
        }
        super.close(handle)
    }


}