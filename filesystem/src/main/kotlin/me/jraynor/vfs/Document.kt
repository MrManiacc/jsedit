package me.jraynor.vfs

import kotlin.jvm.Throws

/**
 * A document is an immutable representation of a file. It represents the physical data stored on the disk.
 * If data has changed, a new document will be created and the reference will be disposed of.
 */
open class Document(
    /**
     * The handle to the file. This is used to access the file.
     */
    val ref: VHandle,

    /**
     * The data that is stored on the disk. This is the raw data that is stored on the disk.
     */
    protected var documentData: ByteArray
) {
    /**
     * The data that is stored on the disk. This is the raw data that is stored on the disk.
     */
    val data: ByteArray
        get() = documentData

    /**
     * Writes the given data to the file. This will overwrite the contents of the file.
     */
    @Throws(IllegalStateException::class)
    open fun write() {
        if (ref.isClosed) throw IllegalStateException("Cannot write to a disposed file handle!")
        else ref.accessor.write(ref, documentData)
    }

    /**
     * Reads the file and returns the data as a byte array.
     */
    @Throws(IllegalStateException::class)
    open fun read() {
        if (ref.isClosed) throw IllegalStateException("Cannot write to a disposed file handle!")
        else documentData = ref.accessor.read(ref)
    }


    /**
     * Only checks equality upon the handle. This is because the handle is the only thing that is guaranteed to be unique.
     * The data may be the same, but the handle will always be unique.
     *
     * This ensures multiple handles can modify the same file without having to worry about the data being different.
     * This allow for different document types to be created and used such as a text document or a binary document or source code document.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false
        return ref == other.ref
    }

    override fun hashCode(): Int {
        return ref.hashCode()
    }
}