package me.jraynor.vfs

/**
 * Represents a filesystem path. This is a file that is located in memory as serialized to a .dat file.
 */
@Suppress("DataClassPrivateConstructor")
data class VPath private constructor(val path: String, val scheme: String) {
    
    private fun parsePath(path: String): VPath {
        val scheme = if (path.contains("://")) path.substringBefore("://") else "file"
        return VPath(if (path.contains("://")) path.substringAfter("://") else path, scheme)
    }

    companion object {
        fun of(path: String): VPath {
            return VPath(path, if (path.contains("://")) path.substringBefore("://") else "file")
        }
    }


}