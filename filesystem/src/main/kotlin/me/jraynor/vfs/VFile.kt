package me.jraynor.vfs

/**
 * An in memory representation of a file. This is a file that is located in memory, on the physical disk, or inside a jar.
 * This is a virtual file that is used as a file handle. It does not impose any restrictions on the file meaning
 * it does not read or write to the file directly, that is done by the [VFS]. The file on the system path
 * doesn't need to exist for this to be created, it can virtually represent any file.
 */
class VFile(path: VPath, val fs: VFS) {
    /**
     * Each [VFile] will store its own metadata. This is used to store any extra information about the file.
     */
    val meta = MetaData()

    /**
     * The path to the file. This is the path that is used to locate the file on the physical disk.
     */
    var path: VPath = path
        internal set

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
        get() = path.path.substringAfterLast("/")

    /**
     * The extension of the file. This is the extension of the file without the dot.
     */
    val extension: String
        get() = path.path.substringAfterLast(".")

    /**
     * MetaData is used to store any extra information about the file. This is used to store any extra information about the file.
     *
     */
    class MetaData(private val properties: MutableMap<String, Any> = hashMapOf()) {

        operator fun set(key: String, value: Any): Boolean {
            if (properties.containsKey(key))
                return false
            properties[key] = value
            return true
        }

        fun add(any: Any): Boolean {
            if (properties.containsKey(any::class.qualifiedName))
                return false
            properties[any::class.qualifiedName!!] = any
            return true
        }

        fun get(key: String): Any? = properties[key]

        fun getString(key: String): String? = get(key) as? String

        fun getBoolean(key: String): Boolean? = get(key) as? Boolean

        fun getFloat(key: String): Float? = get(key) as? Float

        fun getDouble(key: String): Double? = get(key) as? Double

        fun getLong(key: String): Long? = get(key) as? Long

        fun getInt(key: String): Int? = get(key) as? Int

        fun getShort(key: String): Short? = get(key) as? Short

        fun getByte(key: String): Byte? = get(key) as? Byte

        fun getChar(key: String): Char? = get(key) as? Char

        fun getArray(key: String): Array<*>? = get(key) as? Array<*>

        inline fun <reified T> getAs(key: String): T? = get(key) as? T

        inline fun <reified T> getAs(): T? = get(T::class.qualifiedName!!) as? T
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

    internal fun dump(builder: StringBuilder, i: Int) {
        builder.append("\t".repeat(i)).append(path).append("\n")
        children.forEach { it.dump(builder, i + 1) }
    }

}
