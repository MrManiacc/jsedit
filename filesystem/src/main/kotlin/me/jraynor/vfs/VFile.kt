package me.jraynor.vfs

/**
 * An in memory representation of a file. This is a file that is located in memory, on the physical disk, or inside a jar.
 * This is a virtual file that is used as a file handle. It does not impose any restrictions on the file meaning
 * it does not read or write to the file directly, that is done by the [VFS]. The file on the system path
 * doesn't need to exist for this to be created, it can virtually represent any file.
 */
class VFile(path: VPath, val fs: VFS) {
    var path: VPath = path
        internal set
    var parent: VFile? = null
        internal set
    val children: MutableSet<VFile> = hashSetOf()
    val name: String
        get() = path.path.substringAfterLast("/")
    val extension: String
        get() = path.path.substringAfterLast(".")
    val isDirectory: Boolean
        get() = children.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VFile
        if (fs != other.fs) return false
        return path == other.path
    }

    override fun hashCode(): Int {
        var result = fs.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }

    internal fun dump(builder: StringBuilder, i: Int) {
        builder.append("\t".repeat(i)).append(path).append("\n")
        children.forEach { it.dump(builder, i + 1) }
    }

}
