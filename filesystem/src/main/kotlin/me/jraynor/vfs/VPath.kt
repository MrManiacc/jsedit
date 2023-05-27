package me.jraynor.vfs

import java.io.File

/**
 * Represents a filesystem path. This is a file that is located in memory as serialized to a .dat file.
 */
@Suppress("DataClassPrivateConstructor")
data class VPath private constructor(val path: String, val scheme: String) {
    /**
     * Gets teh parent of this path
     */
    val parent: VPath get() = VPath(computeParent(), scheme)


    private fun computeParent(): String {
        if (!path.contains("/") || path == "/" || path == "") return path
        return path.substringBeforeLast("/")
    }

    fun toPath(): String {
        return "$scheme://$path"
    }

    companion object {
        /**
         * Creates a new path from the given path
         */
        fun of(path: String): VPath {
            val normalizedPath = normalizePath(path)
            return VPath(
                //Clean the scheme from the path if it exists
                if (normalizedPath.contains("://")) normalizedPath.substringAfter("://") else normalizedPath,
                //Take only the actual scheme from the path not including the ://
                if (normalizedPath.contains("://")) normalizedPath.substringBefore("://") else "file"
            )
        }

        /**
         * Converts paths into normalized names. This will convert all backslashes to forward slashes and will remove any
         * On windows this would make system 32's path into: c/windows/system32 instead of c:\\windows\\system32
         * On linux this would make the path /home/user/Downloads into: /home/user/Downloads
         * @param path the path to normalize
         * @return the normalized path
         */
        private fun normalizePath(path: String): String {
            if (path.contains(":")) {
                val withoutDriveLetter = path.substringAfter(":")
                val normalizedPath = path.substring(0, 1).lowercase() + withoutDriveLetter.replace("\\", "/").trim()
                return if (normalizedPath.startsWith("/")) normalizedPath else "/$normalizedPath"
            }
            return path.replace("\\", "/").trim()
        }

        fun of(file: File) = of(file.absolutePath)
    }


}