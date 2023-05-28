package me.jraynor.vfs.impl

import me.jraynor.vfs.*
import me.jraynor.vfs.impl.accessors.BinaryAccessor

/**
 * Represents a VFS project. This is an in memory VFS meaning there's no physical representation of it within
 * a physical file disk that you could for example navigate to in Windows explorer.
 */
class BinaryVFS(workspacePath: VPath) : BaseVFS<BinaryAccessor>(workspacePath) {
    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override val accessor: BinaryAccessor = BinaryAccessor()

    /**
     * Checks if the file exists in the file system.
     * @return true if the file exists, false otherwise.
     */
    override fun contains(path: VPath): Boolean {
        TODO("Not yet implemented")
    }


}