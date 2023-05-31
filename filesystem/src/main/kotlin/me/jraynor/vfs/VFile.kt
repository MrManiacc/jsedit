package me.jraynor.vfs

/**
 * An in memory representation of a file. This is a file that is located in memory, on the physical disk, or inside a jar.
 * This is a virtual file that is used as a file handle. It does not impose any restrictions on the file meaning
 * it does not read or write to the file directly, that is done by the [VFS]. The file on the system path
 * doesn't need to exist for this to be created, it can virtually represent any file.
 */
class VFile {
    /**
     * The path to the file. This is the path that is used to locate the file on the physical disk.
     */
    lateinit var path: VPath

    /**
     * The file system that this file is located in. This is the file system that is used to read and write to the file.
     */
    lateinit var fs: VFS


    constructor(path: VPath, fs: VFS) {
        this.path = path
        this.fs = fs
    }



    /**
     * The parent of this file. This is the file that contains this file. This is null if this is the root file.
     */
    var parent: VFile? = null
        internal set

    /**
     * The children of this file. This is a set of files that are contained within this file.
     * This is empty if this is a file and not a directory.
     */
    val children: MutableSet<VFile> = hashSetOf()

    /**
     * The handle to the file. This is used to read and write to the file.
     */
    val name: String
        get() = path.name

    /**
     * The extension of the file. This is the extension of the file without the dot.
     */
    val extension: String
        get() = path.path.substringAfterLast(".")


    /**
     * Converts a canonical path to a normalized path using the current VFile as a reference.
     * The function will lookup a file in a sibling's children.
     */
    fun canonicalLookup(canonicalPath: String): VFile? {
        val parts = canonicalPath.split("/")
        var referenceFile: VFile = this
        for (part in parts)
            when (part) {
                "." -> {
                    // stay at the same level
                }

                ".." -> {
                    // go up one level
                    referenceFile = referenceFile.parent ?: return null // null if trying to go above root
                }

                else -> {
                    // find a child by name
                    referenceFile = referenceFile.children.find { it.name == part }
                        ?: return null // null if no child with such name found
                }
            }
        return referenceFile
    }

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

    internal fun dump() {
        val builder = StringBuilder()
        dump(builder)
        println(builder.toString())
    }

    internal fun dump(builder: StringBuilder, prefix: String = "", isTail: Boolean = true) {
        builder.append(prefix).append(if (isTail) "└── " else "├── ").append(name).append("\n")

        val nextPrefix = prefix + if (isTail) "    " else "│   "
        val it = children.iterator()
        while (it.hasNext()) {
            val child = it.next()
            child.dump(builder, nextPrefix, !it.hasNext())
        }
    }

}
