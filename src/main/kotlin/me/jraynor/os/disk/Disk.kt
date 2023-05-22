package me.jraynor.os.disk

import java.time.LocalDateTime


// VirtualDisk class to represent the filesystem.
class Disk(val root: Folder = Folder("/", User("root", Role.ADMIN))) {
    // Create the home folder for a given user.
    fun getHomeFolder(user: User): Folder {
        return getOrCreateFolder("/home/${user.username}", user)
    }

    fun files(parent: Folder = root): List<File> {
        val children = mutableListOf<File>()
        parent.content.filterIsInstance<File>()
            .forEach { children.add(it) }
        parent.content.filterIsInstance<Folder>()
            .forEach { children.addAll(files(it)) }
        return children
    }

    private fun getOrCreateFolder(path: String, owner: User? = null): Folder {
        val parts = path.split("/").filter { it.isNotEmpty() }
        var currentFolder: Folder = root

        for (i in parts.indices) {
            val part = parts[i]

            if (i == parts.size - 1) { // If this is the last part of the path, it's our new folder
                if (currentFolder.content.any { it.name == part }) {
                    return currentFolder.content.first { it.name == part } as Folder
                }
                val newFolder = Folder(part, owner ?: currentFolder.owner)
                currentFolder.content.add(newFolder)
                return newFolder
            } else { // Otherwise, we need to find the next folder
                val nextFolder = currentFolder.content.firstOrNull { it.name == part && it is Folder }
                currentFolder = if (nextFolder == null) {
                    val newFolder = Folder(part, owner ?: currentFolder.owner)
                    currentFolder.content.add(newFolder)
                    newFolder
                } else {
                    nextFolder as Folder
                }
            }
        }
        return currentFolder
    }

    // Print the structure of the filesystem starting from the root folder.
    fun printStructure() {
        fun printElement(element: DiskElement, indent: String = "") {
            println("$indent${element.name}${if (element is File) "" else "/"}")
            if (element is Folder) {
                element.content.forEach { printElement(it, "$indent  ") }
            }
        }
        printElement(root)
    }

    // Create a new folder at a given path.
    fun createFolder(user: User, path: String): Boolean {
        val parts = path.split("/").filter { it.isNotEmpty() }
        var currentFolder: Folder = root

        for (i in 0 until parts.size) {
            val part = parts[i]

            if (i == parts.size - 1) { // If this is the last part of the path, it's our new folder
                if (currentFolder.content.any { it.name == part }) {
                    return false // Folder name collision
                }

                val folder = Folder(part, user)
                currentFolder.content.add(folder)
                return true
            } else {
                val folder = currentFolder.content.find { it.name == part && it is Folder } as? Folder
                currentFolder = folder ?: return false
            }
        }
        return false
    }


    fun exists(path: String): Boolean {
        return findFolder(path) != null || findFile(path) != null
    }


    // Write to a file at a given path with given content.
    fun writeFile(user: User, path: String, content: ByteArray): Boolean {
        val file = findFile(path)
        if (file == null || (file.owner != user && user.role != Role.ADMIN)) return false

        file.content = content
        file.lastWritten = LocalDateTime.now()

        return true
    }

    // Read a file at a given path.
    fun readFile(user: User, path: String): ByteArray? {
        val file = findFile(path)
        if (file == null || (file.owner != user && user.role != Role.ADMIN)) return null

        file.lastRead = LocalDateTime.now()

        return file.content
    }

    // Find a file at a given path.
    fun findFile(path: String): File? {
        val parts = path.split("/").filter { it.isNotEmpty() }
        var currentFolder: Folder = root

        // ... continuation of the findFile method
        for (i in parts.indices) {
            val part = parts[i]
            val element = currentFolder.content.find { it.name == part }

            if (i == parts.size - 1) { // If this is the last part of the path, it should be our file
                return element as? File
            } else { // Otherwise, it should be a folder that we'll navigate into
                currentFolder = element as? Folder ?: return null
            }
        }
        // If we've gone through all parts of the path and haven't found our file, it doesn't exist
        return null
    }

    fun createFolder(parent: Folder, name: String, owner: User): Folder {
        val folder = Folder(name, owner)
        parent.content.add(folder)
        return folder
    }

    fun createFile(parent: Folder, name: String, owner: User, content: ByteArray): File {
        val file = File(name, owner, content, LocalDateTime.now(), LocalDateTime.now())
        parent.content.add(file)
        return file
    }

    // Create a new file at a given path with given content.
    fun createFile(user: User, path: String, content: ByteArray): Boolean {
        val parts = path.split("/").filter { it.isNotEmpty() }
        var currentFolder: Folder = root

        for (i in parts.indices) {
            val part = parts[i]

            if (i == parts.size - 1) { // If this is the last part of the path, it's our new file
                if (currentFolder.content.any { it.name == part }) {
                    return false // File name collision
                }

                if (currentFolder.owner != user && user.role != Role.ADMIN) return false // Check user permissions

                val file = File(part, user, content, LocalDateTime.now(), LocalDateTime.now())

                currentFolder.content.add(file)
                return true
            } else {
                val folder = currentFolder.content.find { it.name == part && it is Folder } as? Folder
                currentFolder = folder ?: return false
            }
        }
        return false
    }

    // Delete a file at a given path.
    private fun deleteFile(user: User, path: String): Boolean {
        val file = findFile(path) ?: return false // Find the file to delete
        if (file.owner != user && user.role != Role.ADMIN) return false // Check user permissions
        val folder = findParentFolder(path) ?: return false
        folder.content.remove(file) // Remove the file entry from the folder content list
        file.isRemoved = true
        return true
    }

    fun delete(diskElement: DiskElement): Boolean {
        val parentFolder = findParent(diskElement) ?: return false
        parentFolder.content.remove(diskElement)
        return true
    }

    //Deletes a file or folder at given path
    fun delete(user: User, path: String): Boolean {
        if (path == "/") return false
        val file = findFile(path)
        val folder = findFolder(path)
        return if (file != null) {
            deleteFile(user, path)
        } else if (folder != null) {
            deleteFolder(user, path)
        } else {
            false
        }
    }

    private fun deleteFolder(user: User, path: String): Boolean {
        val folder = findFolder(path) ?: return false // Find the folder to delete
        if (folder.owner != user && user.role != Role.ADMIN) return false // Check user permissions
        val parentFolder = findParentFolder(path) ?: return false
        parentFolder.content.remove(folder) // Remove the folder entry from the parent folder content list
        return true
    }


    // Find a folder at a given path.
    fun findFolder(path: String): Folder? {
        if (path == "/" || path == "") return root
        val parts = path.split("/").filter { it.isNotEmpty() }
        var currentFolder: Folder = root

        for (i in parts.indices) {
            val part = parts[i]
            val element = currentFolder.content.find { it.name == part }

            if (i == parts.size - 1) { // If this is the last part of the path, it should be our folder
                return element as? Folder
            } else { // Otherwise, it should be a folder that we'll navigate into
                currentFolder = element as? Folder ?: return null
            }
        }
        // If we've gone through all parts of the path and haven't found our folder, it doesn't exist
        return null
    }

    // Find the parent folder of a given path.
    private fun findParentFolder(path: String): Folder? {
        val parentPath = path.substringBeforeLast('/', "") // Remove the last part of the path
        return findFolder(parentPath) // Find the folder at the parent path
    }

    // Move a file from an old path to a new path.
    fun moveFile(user: User, oldPath: String, newPath: String): Boolean {
        val file = findFile(oldPath)
        if (file == null || (file.owner != user && user.role != Role.ADMIN)) return false

        val newParentFolder =
            findFolder(newPath) // Assume findParentFolder is implemented to return parent Folder of given path
        if (newParentFolder == null || newParentFolder.content.any { it.name == file.name }) return false

        val oldParentFolder = findParentFolder(oldPath)
        oldParentFolder?.content?.remove(file)
        newParentFolder.content.add(file)
        return true
    }

    fun move(file: DiskElement, folder: Folder) {
        // Prevent moving a folder into its child
        if (file is Folder && isChildOf(file, folder)) {
            return
        }
        val parent = findParent(file) ?: return
        if (parent == folder) return
        parent.content.remove(file)
        folder.content.add(file)
    }

    // This function checks if 'possibleChild' is a child of 'folder'
    private fun isChildOf(folder: Folder, possibleChild: Folder): Boolean {
        // Check all children
        for (child in folder.content) {
            if (child == possibleChild) {
                return true
            } else if (child is Folder) {
                // Recursively check this child's children
                if (isChildOf(child, possibleChild)) {
                    return true
                }
            }
        }
        // If no child matches 'possibleChild', return false
        return false
    }

    private fun findParent(file: DiskElement): Folder? {
        return findInFolder(root, file)
    }

    private fun findInFolder(folder: Folder, file: DiskElement): Folder? {
        if (folder.content.contains(file)) return folder
        folder.content.forEach {
            if (it is Folder) {
                val result = findInFolder(it, file)
                if (result != null) return result
            }
        }
        return null
    }

    fun move(user: User, oldPath: String, newPath: String): Boolean {
        val file = findFile(oldPath)
        val folder = findFolder(oldPath)
        return if (file != null) {
            moveFile(user, oldPath, newPath)
        } else if (folder != null) {
            moveFolder(user, oldPath, newPath)
        } else {
            false
        }
    }

    fun moveFolder(user: User, oldPath: String, newPath: String): Boolean {
        val folder = findFolder(oldPath)
        if (folder == null || (folder.owner != user && user.role != Role.ADMIN)) return false
        val target = findFolder(newPath)
        val olderParent = findParentFolder(oldPath)
        olderParent?.content?.remove(folder)
        target?.content?.add(folder)
        return true
    }
}