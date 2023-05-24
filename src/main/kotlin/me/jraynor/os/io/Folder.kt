package me.jraynor.os.io

// Folder class to represent a folder in the filesystem.
class Folder(name: String, owner: User, var content: MutableList<IOElement> = mutableListOf()) :
    IOElement(name, owner) {

    constructor() : this("", User("root", Role.ADMIN))

    // Return the total size of the folder's content.
    override fun size() = content.sumOf { it.size() }
    fun listContents(): String {
        return content.joinToString("\n") { if (it is Folder) "${it.name} | owner=${it.owner.username}" else if (it is File) "${it.name} | owner=${it.owner.username}\" | ${it.lastRead} | ${it.lastWritten}" else "" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        return content == other.content
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

}