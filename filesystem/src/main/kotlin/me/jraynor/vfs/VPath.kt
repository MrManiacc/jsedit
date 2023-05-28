package me.jraynor.vfs

import java.io.File
import java.util.*

/**
 * Represents a filesystem path. This is a file that is located in memory as serialized to a .dat file.
 */
data class VPath(val path: String, val scheme: String) {
    /**
     * Gets teh parent of this path
     */
    val parent: VPath get() = VPath(computeParent(), scheme)

    /**
     * Checks if this path is a child of the given path
     */
    fun isChildOf(path: VPath): Boolean = normalizePath(this.path).startsWith(normalizePath(path.path))


    fun normalize(root: VFile): VPath {
        var path = root.path.path
        if (!path.startsWith("/")) path = "/$path"
        path = if (!this.path.startsWith(path)) {
            if (!this.path.startsWith("/"))
                "$path/${this.path}"
            else
                "$path${this.path}"
        } else this.path
        return VPath(normalizePath(path), scheme)
    }

    private fun computeParent(): String {
        if (!path.contains("/") && path.isNotEmpty() || (path == "/" || path == "")) return "/"
        return path.substringBeforeLast("/")
    }

    fun isChild(path: VPath): Boolean {
        return this.path.startsWith(path.path)
    }

    fun toFile(): File =
        if (System.getProperty("os.name").lowercase().contains("win")) File(denormalizePath(path)) else File(
            path
        )

    fun toPath(): String {
        return "$scheme://$path"
    }

    companion object {
        /**
         * Creates a new path from the given path
         */
        fun of(path: String): VPath {
            var normalizedPath = normalizePath(path.replace("//", "/"))
            if (normalizedPath.endsWith("/"))
                normalizedPath = normalizedPath.substring(0, normalizedPath.length - 1)
            return VPath(
                //Clean the scheme from the path if it exists
                if (normalizedPath.contains("://")) normalizedPath.substringAfter("://") else normalizedPath,
                //Take only the actual scheme from the path not including the ://
                if (normalizedPath.contains("://")) normalizedPath.substringBefore("://") else "file"
            )
        }

        /**
         * Converts paths into normalized names. This will convert all backslashes to forward slashes, remove any
         * leading or trailing spaces, and include the drive letter as a directory at the start of the path.
         * On windows this would make system 32's path into: /c/windows/system32 instead of C:\\windows\\system32
         * On linux this would make the path /home/user/Downloads into: /home/user/Downloads
         * @param path the path to normalize
         * @return the normalized path
         */
        private fun normalizePath(path: String): String {
            if (path.startsWith("/") && !path.contains(":")) return path
            val driveLetter = path.substringBefore(":").lowercase(Locale.getDefault())
            val withoutDriveLetter = path.substringAfter(":")
            var output = "/$driveLetter" + withoutDriveLetter.replace("\\", "/")
            output = output.replace("//", "").trim()
            output = if (output.endsWith("/")) output.substring(0, output.length - 1) else output
            return output
        }

        /**
         * Converts normalized paths into Windows format. This will convert all forward slashes to backslashes,
         * and place the drive letter at the start of the path with a colon.
         * This would make the path /c/windows/system32 into: C:\\windows\\system32
         * @param path the normalized path to convert
         * @return the Windows path
         */
        private fun denormalizePath(path: String): String {
            val driveLetter = path.substringAfter("/").substringBefore("/")
            val withoutDriveLetter =
                if (path.startsWith("/")) path.substring(1).substringAfter("/") else path.substringAfter("/")
            return "${driveLetter.uppercase()}:\\" + withoutDriveLetter.replace("/", "\\")
        }

        fun of(file: File) = of(file.absolutePath)
    }


}

val String.vpath: VPath get() = VPath.of(if (this.startsWith("/")) this else "/$this")