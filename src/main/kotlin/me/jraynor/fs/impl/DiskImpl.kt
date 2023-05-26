package me.jraynor.fs.impl

import me.jraynor.fs.Path
import me.jraynor.fs.VirtualDisk
import me.jraynor.fs.VirtualFile
import me.jraynor.fs.VirtualFileException
import java.io.File

/**
 * This is a [VirtualDisk] that is backed by the filesystem. This is the only [VirtualDisk] that can be written to.
 * This is the only [VirtualDisk] that can be written to.
 * @param rootPath The root path of the disk. This is the path that will be used to read and write files.
 * @see VirtualDisk  * This is the only [VirtualDisk] that can be written to.
 */
class FilesystemDisk(rootPath: File) : VirtualDisk {
    private val absolutePath = Path.of(Path.normalizePath(rootPath), this, null)

    private fun path(relativePath: String): Path {
        if (relativePath.startsWith("./"))
            return Path.of(
                "${absolutePath.scheme}${relativePath.substring(2)}",
                this,
                null
            )
        else if (relativePath.startsWith("/"))
            return Path.of(
                "$absolutePath${relativePath.substring(1)}",
                this,
                null
            )
        else
            return Path.of(
                "${absolutePath.scheme}${relativePath}",
                this,
                null
            )

    }


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
    override fun lookup(relativePath: String): VirtualFile {
        val path = path(relativePath)
        return FilesystemFile(path)
    }

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
    override val path: () -> Path
        get() = { absolutePath }
}

/**
 * A [VirtualDisk] that is located on the classpath. This is a virtual disk that is located inside a jar.
 * This is a read only disk.
 */
class ClasspathDisk(absolutePath: Path) : VirtualDisk {

    /**
     * The path of the disk. This is the path of the disk relative to the project root.
     */
    private val rootPath: Path = path("/", absolutePath)

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
    override fun lookup(relativePath: String): VirtualFile {
        val path = path(relativePath)
        val file = if (path.extension.isNotEmpty())
            ClasspathFile(path)
        else ClasspathFolder(path)
        if (!file.exists())
            throw VirtualFileException.FileNotFoundException(file)
        else return file
    }


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
    override val path: () -> Path = { rootPath }
}