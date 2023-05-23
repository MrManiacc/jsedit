package me.jraynor.os.disk

abstract class DiskElement(var name: String, var owner: User) {
    // Abstract method to get the size of the disk element.

    val nameWithoutExtension: String
        get() =
            if (name.contains(".")) name.substring(0, name.lastIndexOf("."))
            else name

    abstract fun size(): Int
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiskElement) return false

        if (name != other.name) return false
        return owner == other.owner
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }


}