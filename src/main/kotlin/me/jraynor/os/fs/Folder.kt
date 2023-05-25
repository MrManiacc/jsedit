package me.jraynor.os.fs

class Folder(location: String, val children: MutableSet<File>) : File(location, "folder", "", 0) {

    /**
     * Adds a file to the folder
     */
    fun addFile(file: File): Boolean {
        if (children.add(file)) {
            file.parent = this
            return true
        }
        return false
    }

    /**
     * Removes a file from the folder
     */
    fun removeFile(file: File): Boolean {
        if (children.remove(file)) {
            file.parent = null
            return true
        }
        return false
    }

    /**
     * Removes a file from the folder
     */
    fun removeFile(name: String): Boolean = removeFile(getFile(name))

    /**
     * Gets a child file by name
     */
    fun getFile(name: String, includeExtension: Boolean = true): File {
        if (name.startsWith("/")) return getFile(name.substring(1), includeExtension)
        return children.find { if (includeExtension) it.name == name else it.nameWithoutExtension == name } ?: EMPTY
    }

    fun dump(level: Int) {
        if (level == 0) println("$name/")
        println("|${"_".repeat(level)}$name/")
        for (child in children) {
            if (child !is Folder) {
                println("|${"_".repeat(level + 3)}${child.name}")
            }
        }
        for (child in children) {
            if (child is Folder) {
                child.dump(level + 3)
            }
        }
    }


    companion object {
        val GLOBAL_ROOT: Folder = Folder("/", mutableSetOf())
    }
}