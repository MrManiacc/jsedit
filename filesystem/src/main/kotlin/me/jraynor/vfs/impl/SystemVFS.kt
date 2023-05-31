package me.jraynor.vfs.impl

import me.jraynor.vfs.*
import me.jraynor.vfs.impl.accessors.SystemAccessor
import java.io.File

class SystemVFS(path: VPath) : BaseVFS<SystemAccessor>(path) {

    constructor(basePath: String = "/") : this(VPath.Companion.of(basePath))
    constructor(file: File) : this(file.absolutePath)

    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override val accessor: SystemAccessor = SystemAccessor(::cache)

    /**
     * Checks if the file exists in the file system.
     * @return true if the file exists, false otherwise.
     */
    override fun contains(path: VPath): Boolean {
        return path.toFile().exists()
    }

}