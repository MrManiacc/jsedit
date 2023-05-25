package me.jraynor.fs

/**
 * Represents a file in the workspace
 */
open class File(
    /**
     * The location of the file in the workspace
     */
    name: String,

    /**
     * The type of file this is (ex: kotlin, javascript, etc)
     */
    fileType: String,
    /**
     * The contents of the file
     */
    contents: CharSequence,
    /**
     * The version of the file
     */
    val version: Int = 0,
) {

    /**
     * The contents of the file
     */
    var contents: CharSequence = contents
        private set

    /**
     * The location of the file in the workspace
     */
    var name: String = name
        internal set
    /**
     * The type of file this is (ex: kotlin, javascript, etc)
     */
    var fileType: String = fileType
        private set


    /**
     * The location of the file in the workspace, internally used and updated by the file system, do not modify directly
     */
    internal var parent: Folder? = null


    /**
     * The name of the file without the extension
     */
    val nameWithoutExtension: String
        get() = if (name.contains(".")) name.substringBeforeLast(".") else name


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as File
        if (name != other.name) return false
        if (version != other.version) return false
        if (contents != other.contents) return false
        return fileType == other.fileType
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + version
        result = 31 * result + contents.hashCode()
        result = 31 * result + fileType.hashCode()
        return result
    }

    companion object {
        val EMPTY = File(
            "",
            "",
            "",
            -1
        )
    }


}