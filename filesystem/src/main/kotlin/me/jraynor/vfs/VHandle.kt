package me.jraynor.vfs

/**
 * This is a handle to a file. This is used to read and write data to a file. This is used to abstract the underlying
 * file system.
 */
class VHandle(
    val ref: VFile,
    internal val accessor: FileAccessor,
) {
    /**
     * Each [VHandle] will store its own metadata. This is used to store any extra information about the file.
     * This is like runtime information such as an input stream needed to read the file.
     */
    val meta = MetaData()

    //Internally track the disposal state of this handle. No R/W calls will be valid after this is disposed.
    var isClosed = false
        private set

    /**
     * Reads the file and returns the data as a byte array.
     */
    fun read(): Document = if (!isClosed) Document(
        this,
        accessor.read(this)
    ) else throw IllegalStateException("Cannot read from a disposed file handle!")



    fun document(data: String) = Document(this, data.toByteArray())

    fun document(data: ByteArray) = Document(this, data)


    /**
     * Closes the file handle. This will remove the file from the cache and clear the in memory representation of the file.
     */
    internal fun dispose() {
        isClosed = true
    }


    /**
     * MetaData is used to store any extra information about the file. This is used to store any extra information about the file.
     *
     */
    class MetaData(private val properties: MutableMap<String, Any> = hashMapOf()) {
        fun remove(key: String) = properties.remove(key)

        operator fun contains(key: String) = properties.containsKey(key)
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


}