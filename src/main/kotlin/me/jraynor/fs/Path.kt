package me.jraynor.fs

import java.io.File
import java.net.URI

sealed class Path {
    abstract val path: String
    abstract val scheme: String
    abstract val isReadOnly: Boolean
    abstract val fs: VirtualDisk

    /**
     * returns the name of the file or directory (i.e. the last part of the path)
     */
    val name get() = path.substringAfterLast("/")

    /**
     * returns the extension of the file or an empty string if there is no extension (i.e. the path is a directory)
     */
    val extension: String get() = if (name.contains(".")) name.substringAfterLast(".") else ""

    /**
     * returns the parent path of this path or this path if there is no parent path (i.e. the path is a file name in the root directory)
     */
    open val parent: Path get() = if (!path.contains("/")) this else of(path.substringBeforeLast("/"), fs, null)

    /**
     * Creates a URI from our path
     */
    val uri: URI get() = URI.create("$scheme$path")

    /**
     * returns a new path with the given [other] appended to the end of the path
     */
    operator fun div(other: String): Path = of("$scheme$path/$other", fs, null)

    /**
     * returns a new path with the given [other] appended to the end of the path
     */
    infix fun append(other: String): Path = of("$scheme$path/$other", fs, null)

    /**
     * Returns the path as a string
     */
    override fun toString(): String = "$scheme$path"

    /**
     * Represents a memory path. This is a file that is located in memory as serialized to a .dat file.
     */
    private data class MemoryPath(override val path: String, override val fs: VirtualDisk) : Path() {
        override val scheme: String = "project://"
        override val isReadOnly: Boolean = false
    }

    /**
     * Represents a memory path. This is a file that is located in memory as serialized to a .dat file.
     */
    private data class DelegatedMemoryPath(
        override val parent: Path,
        override val path: String,
        override val fs: VirtualDisk
    ) : Path() {
        override val scheme: String = "project://"
        override val isReadOnly: Boolean = false
    }

    /**
     * Represents a resource path. This is a file that is located inside a jar.
     */
    private data class ResourcePath(override val path: String, override val fs: VirtualDisk) : Path() {
        override val scheme: String = "jar://"
        override val isReadOnly: Boolean = true
    }

    /**
     * Represents a resource path. This is a file that is located inside a jar. This is a delegated path.
     */
    private data class DelegateResourcePath(
        override val parent: Path,
        override val path: String,
        override val fs: VirtualDisk
    ) : Path() {
        override val scheme: String = "jar://"
        override val isReadOnly: Boolean = true
    }


    /**
     * Represents a filesystem path. This is a file that is located on the physical disk.
     */
    private data class FileSystemPath(override val path: String, override val fs: VirtualDisk) : Path() {
        override val scheme: String = "file://"
        override val isReadOnly: Boolean = false
    }

    /**
     * Represents a filesystem path. This is a file that is located on the physical disk.
     */
    private data class DelegateFileSystemPath(
        override val parent: Path,
        override val path: String,
        override val fs: VirtualDisk
    ) : Path() {
        override val scheme: String = "file://"
        override val isReadOnly: Boolean = false
    }

    companion object {
        fun normalizePath(file: File): String {
            val isWindows = System.getProperty("os.name").startsWith("Windows")
            var normalizedPath = if (isWindows) {
                val drive = file.absolutePath.substring(0, 1).toLowerCase()
                val path = file.absolutePath.substring(3).replace('\\', '/')
                "$drive/$path"
            } else {
                // Unix-based file systems
                // Remove leading / if present to align with Windows-based format
                file.absolutePath.trimStart('/')
            }

            // If there's no extension (i.e., this is not a file), ensure the path ends with a '/'
            if (file.extension.isEmpty()) {
                normalizedPath = if (normalizedPath.endsWith('/')) normalizedPath else "$normalizedPath/"
            }

            return normalizedPath
        }


        fun of(
            path: String,
            fs: VirtualDisk,
            delegatedParent: Path?
        ): Path = with(path) {
            if (delegatedParent != null) {
                return when {
                    startsWith("project://") -> DelegatedMemoryPath(delegatedParent, removePrefix("project://"), fs)
                    startsWith("jar://") -> DelegateResourcePath(delegatedParent, removePrefix("jar://"), fs)
                    startsWith("file://") -> DelegateFileSystemPath(delegatedParent, removePrefix("file://"), fs)
                    else -> DelegatedMemoryPath(delegatedParent, this, fs) // default to project type
                }
            } else
                return when {
                    startsWith("project://") -> MemoryPath(removePrefix("project://"), fs)
                    startsWith("jar://") -> ResourcePath(removePrefix("jar://"), fs)
                    startsWith("file://") -> FileSystemPath(removePrefix("file://"), fs)
                    else -> MemoryPath(this, fs) // default to project type
                }
        }
    }
}
