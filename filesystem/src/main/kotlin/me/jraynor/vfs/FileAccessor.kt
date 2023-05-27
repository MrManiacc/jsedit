package me.jraynor.vfs

interface FileAccessor {
    /**
     * Reads the data from the file. This is used to read data from a file.
     * @param file the file to read the data from.
     * @return the data that was read from the file.
     */
    fun read(file: VFile): ByteArray

    /**
     * Writes the data to the file. This is used to write data to a file.
     * @param file the file to write the data to.
     * @param data the data to write to the file.
     */
    fun write(file: VFile, data: ByteArray)

    /**
     * Reads the attributes of a file. This is used to get the attributes of a file.
     * @param file the file to read the attributes of.
     */
    fun readAttribs(file: VFile): FileAttributes

    /**
     * Provides an implementation specific way to list the children of a directory. This is used to list the children of a directory.
     * @param path the path to the directory.
     * @return a list of paths to the children of the directory.
     */
    fun propagateChildren(path: VPath): List<VPath>

}