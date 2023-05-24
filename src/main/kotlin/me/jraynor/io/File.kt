package me.jraynor.io

import java.time.LocalDateTime

// File class to represent a file in the filesystem.
class File(
    name: String,
    owner: User,
    var content: ByteArray,
    var lastRead: LocalDateTime,
    var lastWritten: LocalDateTime,
    var isRemoved: Boolean = false,
) : IOElement(name, owner) {

    constructor() : this("", User("root", Role.ADMIN), ByteArray(0), LocalDateTime.now(), LocalDateTime.now())

    // Return the size of the file content.
    override fun size() = content.size
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is File) return false
        if (!super.equals(other)) return false

        if (!content.contentEquals(other.content)) return false
        if (lastRead != other.lastRead) return false
        if (lastWritten != other.lastWritten) return false
        return isRemoved == other.isRemoved
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + lastRead.hashCode()
        result = 31 * result + lastWritten.hashCode()
        result = 31 * result + isRemoved.hashCode()
        return result
    }


}