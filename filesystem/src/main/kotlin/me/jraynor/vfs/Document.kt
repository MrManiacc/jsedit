package me.jraynor.vfs

/**
 * A document is an immutable representation of a file. It represents the physical data stored on the disk.
 * If data has changed, a new document will be created and the reference will be disposed of.
 */
data class Document(val reference: VFile, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (reference != other.reference) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = reference.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}