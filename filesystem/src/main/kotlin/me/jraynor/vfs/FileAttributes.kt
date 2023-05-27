package me.jraynor.vfs


data class FileAttributes(private val attributes: MutableSet<FileAttribute> = hashSetOf()) {
    operator fun plus(attribute: FileAttribute): FileAttributes {
        val attributes = this.attributes.toMutableSet()
        attributes.add(attribute)
        return FileAttributes(attributes)
    }

    operator fun plus(attributes: FileAttributes): FileAttributes {
        val attrs = this.attributes.toMutableSet()
        attrs.addAll(attributes.attributes)
        return FileAttributes(attrs)
    }

    operator fun plusAssign(attribute: FileAttribute) {
        attributes.add(attribute)
    }

    operator fun plusAssign(attributes: FileAttributes) {
        this.attributes.addAll(attributes.attributes)
    }


    operator fun minus(attribute: FileAttribute): FileAttributes {
        attributes.remove(attribute)
        return this
    }

    operator fun minus(attributes: FileAttributes): FileAttributes {
        this.attributes.removeAll(attributes.attributes)
        return this
    }

    operator fun contains(attribute: FileAttribute): Boolean {
        return attributes.contains(attribute)
    }

    operator fun contains(attributes: FileAttributes): Boolean {
        return this.attributes.containsAll(attributes.attributes)
    }

    fun isEmpty(): Boolean {
        return attributes.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return attributes.isNotEmpty()
    }

    fun toIteratble(): Iterable<FileAttribute> = attributes.toList()


    data class FileAttribute(val name: String, val identifier: Char)
    companion object {
        val EXECUTE = FileAttribute("EXECUTE", 'x')
        val READ_ONLY = FileAttribute("READ_ONLY", 'r')
        val WRITE_ONLY = FileAttribute("WRITE_ONLY", 'w')
        val READ_WRITE = FileAttribute("READ_WRITE", 'b')
        val HIDDEN = FileAttribute("HIDDEN", 'h')
        val SYSTEM = FileAttribute("SYSTEM", 's')
        val DIRECTORY = FileAttribute("DIRECTORY", 'd')
        val ARCHIVE = FileAttribute("ARCHIVE", 'a')
        val TEMPORARY = FileAttribute("TEMPORARY", 't')
        val COMPRESSED = FileAttribute("COMPRESSED", 'c')
        val ENCRYPTED = FileAttribute("ENCRYPTED", 'e')
        val NOT_CONTENT_INDEXED = FileAttribute("NOT_CONTENT_INDEXED", 'i')
        val OFFLINE = FileAttribute("OFFLINE", 'o')
    }

}