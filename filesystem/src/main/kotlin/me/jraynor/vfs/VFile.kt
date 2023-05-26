package me.jraynor.vfs

/**
 * An in memory representation of a file. This is a file that is located in memory, on the physical disk, or inside a jar.
 * This is a virtual file that is used as a file handle. It does not impose any restrictions on the file meaning
 * it does not read or write to the file directly, that is done by the [VFS]. The file on the system path
 * doesn't need to exist for this to be created, it can virtually represent any file.
 */
data class VFile(val path: VPath, val fs: VFS)