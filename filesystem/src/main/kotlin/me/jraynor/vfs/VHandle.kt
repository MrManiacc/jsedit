package me.jraynor.vfs

/**
 * This is a handle to a file. This is used to read and write data to a file. This is used to abstract the underlying
 * file system.
 */
class VHandle(
    val reference: VFile,
    internal val accessor: FileAccessor,
) {
    //Internally track the disposal state of this handle. No R/W calls will be valid after this is disposed.
    var isClosed = false
        private set

    /**
     * Reads the file and returns the data as a byte array.
     */
    fun read(): Document = if (!isClosed) Document(
        reference,
        accessor.read(reference)
    ) else throw IllegalStateException("Cannot read from a disposed file handle!")


    /**
     * Writes the given data to the file. This will overwrite the contents of the file.
     */
    fun write(document: Document) =
        if (document.reference != reference)
            throw IllegalArgumentException("Document file does not match the file handle!")
        else if (isClosed)
            throw IllegalStateException("Cannot write to a disposed file handle!")
        else accessor.write(document.reference, document.data)


    /**
     * Closes the file handle. This will remove the file from the cache and clear the in memory representation of the file.
     */
    internal fun dispose() {
        isClosed = true
    }

}