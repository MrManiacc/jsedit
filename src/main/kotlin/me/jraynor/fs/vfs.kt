package me.jraynor.fs

/**
 * A virtual file that can be read and written to. This is a file that is located in memory, on the physical disk, or inside a jar.
 *
 * Never stores the contents of the file in memory. This is a virtual file that is used to read and write to the file.
 *
 */
interface VirtualFile {
    /**
     * The name of the file. This is the name of the file without the path.
     */
    val name: String get() = path.path

    /**
     * The parent of the file. This is the folder that contains this file.
     */
    val parent: VirtualFile get() = if (isRoot) this else path.fs.lookup(path.parent)

    /**
     * The path of the file. This is the path of the file relative to the project root.
     */
    val path: Path

    /**
     * The scheme of the file. This is the scheme of the file. This is used to determine how to read and write the file.
     */
    val scheme: String get() = path.scheme

    /**
     * @return true if the file is the root, false otherwise.
     * This is true if the file is the root of the file system meaning it's parent is itself.
     */
    val isRoot get() = path.path == "/" || path.path == "" || path.path == "." || path.parent == path

    /**
     * Reads the file as a byte array using the [path] to determine how to read the file.
     */
    fun read(): ByteArray

    /**
     * Returns true if the file exists, false otherwise.
     */
    fun exists(): Boolean

    /**
     * Returns true if the file is read only, false otherwise.
     */
    fun isReadOnly(): Boolean

    /**
     * Returns true if the file is a directory, false otherwise.
     */
    fun isDirectory(): Boolean
}

/**
 * A [VirtualFolder] is a [VirtualFile] that contains other files.
 */
interface VirtualFolder : VirtualFile {

    /**
     * Lists the files in the folder using the [path] to determine how to list the files.
     */
    fun listFiles(): List<VirtualFile>

    /**
     * Reads a file at the given [path] using the [path] to determine how to read the file.
     */
    fun readFile(path: Path): VirtualFile

}

/**
 * A helper function that lists the folders in the folder. This is a convenience function that filters the files to only return folders.
 */
fun VirtualFolder.listFolders(): List<VirtualFolder> = listFiles().filterIsInstance<VirtualFolder>()

/**
 * A helper function that reads a file at the given [path] using the [path] to determine how to read the file.
 */
fun VirtualFolder.readFolder(path: Path): VirtualFolder = with(readFile(path)) {
    if (this is VirtualFolder) this else throw IllegalArgumentException("File at path $path is not a folder")
}

/**
 * A [MutableVirtualFolder] is a [MutableVirtualFile] that contains other files. This is a folder that can be written to.
 * @see MutableVirtualFile for more information.
 */
interface MutableVirtualFolder : MutableVirtualFile {

    /**
     * Creates a file at the given [path] using the [path] to determine how to create the file.
     */
    fun createFile(path: Path): VirtualFile

    /**
     * Creates a folder at the given [path] using the [path] to determine how to create the folder.
     */
    fun createFolder(path: Path): VirtualFolder

    /**
     * Deletes the file at the given [path] using the [path] to determine how to delete the file.
     */
    fun delete(path: Path): Boolean

    /**
     * Moves the file at the given [path] to the given [path] using the [path] to determine how to move the file.
     */
    fun move(path: Path, newPath: Path): Boolean
}


/**
 * A [MutableVirtualFile] is a [VirtualFile] that can be written to.
 * @see VirtualFile for more information.
 */
interface MutableVirtualFile : VirtualFile {

    /**
     * Writes the given [bytes] to the file using the [path] to determine how to write the file.
     * @return true if the write was successful, false otherwise. If the file is read only or if the scheme is jar, this will always return false.
     * @see [Path]
     */
    fun write(bytes: ByteArray): Boolean


    /**
     * Moves the file to the given [path] using the [path] to determine how to move the file.
     */
    fun move(path: Path): VirtualFile


    /**
     * Deletes the file using the [path] to determine how to delete the file.
     */
    fun delete(): Boolean
}


/**
 * A [VirtualDisk] is a virtual file system that can be used to read files. This is used to abstract the file system from the editor.
 * This allows us to use the same code for reading files from the disk and from the jar.
 */
interface VirtualDisk {
    /**
     * Reads a [VirtualFile] from the given [path]. This will return a [VirtualFile] that can be used to read and write to the file.
     * Will never return null. It will return an empty file if the file does not exist. but will be created as soon as you write to it.
     *
     * The [relativePath] is the path relative to the root of the [VirtualDisk]. This is the path that is used to read the file.
     * The path can start with a `/` or not. If it does not start with a `/`, it will be relative to the root of the [VirtualDisk].
     * Example paths:
     *  -  `file.txt` - This is a file in the root of the disk.
     *  -  `folder/file.txt` - This is a file in the folder `folder` in the root of the disk.
     *  -  `folder/subfolder/file.txt` - This is a file in the folder `subfolder` in the folder `folder` in the root of the disk.
     *  -  `folder/subfolder/` - This is a folder in the folder `subfolder` in the folder `folder` in the root of the disk.
     *  -  `folder/subfolder` - This is also a folder in the folder `subfolder` in the folder `folder` in the root of the disk.
     *  -  `/` - This is the root of the disk.
     *  -  `/folder/subfolder/file.txt` - This is a file in the folder `subfolder` in the folder `folder` in the root of the disk.
     *  -  `/folder/subfolder/` - This is a folder in the folder `subfolder` in the folder `folder` in the root of the disk.
     */
    fun lookup(relativePath: String): VirtualFile

    /**
     * If there are multiple schemes we and if the disk's paths are delayed, we need to be able to look up a file using a parent [Path].
     */
    fun lookup(relativePath: Path): VirtualFile = lookup(relativePath.toString())

    /**
     * Reads the given [virtualFile] as a [ByteArray].
     */
    fun read(virtualFile: VirtualFile): ByteArray = virtualFile.read()

    /**
     * Reads the given [relativePath] as a [ByteArray].
     */
    fun read(relativePath: Path): ByteArray = read(lookup(relativePath))

    /**
     * Creates a [Path] using the given [path] and this disk.
     */
    fun path(path: String, delegate: Path? = null): Path = Path.of(path, this, delegate)

    /**
     * The absolute path of the disk. This is the real path of the disk on the physical disk.
     *
     *     -  If this virtual disk is a jar, this will return the path to the jar.
     *     -  If this virtual disk is a folder, this will return the path to the folder.
     *     -  If this virtual disk is a file (serialized in memory to .dat file), this will return the path to the file.
     *
     * This must be a provider as creation of the path is delegated,
     * to virtual disk implementations to ensure a path contains a VirtualDisk instance.
     */
    val path: () -> Path

    /**
     * Checks if the given path exists. This will return false if the path does not exist.
     */
    fun exists(path: Path): Boolean = lookup(path).exists()

    /**
     * Checks if the given path is read only. This will return false if the path does not exist.
     */
    fun isReadOnly(path: Path): Boolean = lookup(path).isReadOnly()

    /**
     * Checks if the given path is a directory. This will return false if the path does not exist.
     */
    fun isDirectory(path: Path): Boolean = lookup(path).isDirectory()
}

/**
 * A [MutableVirtualDisk] is a [VirtualDisk] that can be written to.
 */
interface MutableVirtualDisk : VirtualDisk {

    /**
     * Writes the given [bytes] to the file using the [path] to read the file.
     */
    fun write(path: Path, bytes: ByteArray): Boolean

    /**
     * Should implement deleting current file and creating new one at the given path with the same contents.
     * This will override the file at the given path if it exists. If the file does not exist, it will be created.
     * You should check if the file exists before calling this method if you do not want to override the file.
     */
    fun move(oldPath: Path, newPath: Path): Boolean

    /**
     * Should facilitate the deleting of a file at the given path.
     */
    fun delete(path: Path): Boolean

}