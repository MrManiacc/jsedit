package me.jraynor.vfs.impl.accessors

import me.jraynor.vfs.*
import java.io.File
import java.io.InputStream
import java.util.jar.JarFile

class JarAccessor(private var file: File, private val cache: (path: VPath) -> VFile) :
    FileAccessor {
    var jarFile: JarFile = JarFile(file)
        private set

    /**
     * Reads the data from the file. This is used to read data from a file.
     * @param file the file to read the data from.
     * @return the data that was read from the file.
     */
    override fun read(file: VHandle): ByteArray {
        if (file.isClosed) throw IllegalStateException("Cannot read from a closed file.")
        val stream =
            file.meta.getAs<InputStream>("stream")
                ?: jarFile.getInputStream(jarFile.getJarEntry(file.ref.path.path))
        // cache the entry in the meta of the file
        file.meta["stream"] = stream
        // If the entry doesn't exist, return an empty byte array
        if (stream == null) {
            return ByteArray(0)
        }
        // Read the content and convert it to a byte array
        return stream.readBytes()
    }


    /**
     * Writes the data to the file. This is used to write data to a file.
     * @param file the file to write the data to.
     * @param data the data to write to the file.
     */
    override fun write(file: VHandle, data: ByteArray) {
        throw UnsupportedOperationException("Cannot write to a jar file.")
    }

    /**
     * Reads the attributes of a file. This is used to get the attributes of a file.
     * @param file the file to read the attributes of.
     */
    override fun attribs(file: VFile): FileAttributes {
        return FileAttributes() + FileAttributes.READ_ONLY + FileAttributes.SYSTEM
    }


    /**
     * Uses javas classpath to index the jar file. This is used to index a directory. It will recursively index all the children of the directory.
     * Caching their internally. This is used to index a directory and all its children.
     *
     * @param pathIn the path to the directory.
     * @return a list of paths to the children of the directory.
     */
    override fun index(pathIn: VPath): List<VFile> {
        // This will hold the cached files
        val files = mutableSetOf<VFile>()
        // Go through the entries in the jar file
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            var location = "/${entry.name}"
            if (location.endsWith("/")) location = location.substring(0, location.length - 1)
            val path = VPath(location, "jar")
            if (path.path.isEmpty()) continue // dont index the root
            // Create a VPath object from the entry name
            val file =
                cache(path)
//                println("${file.path.path}  - entry: ${entry.realName}")

//                val entryPath = VPath.of("/${entry.name}")
//                // Check if the entry is under the given path
//                if (entryPath.path.startsWith(path.path)) {
//                    // If it is, create and cache a VFile from it
////                entry.attributes.getValue()
//                    val parent = createAndCache(entryPath.parent)
//                    val file = createAndCache(entryPath)
//                    file.parent = parent
//                    parent.children.add(file)
//                    file.meta["stream"] = (jarFile.getInputStream(entry)) //store the jar entry for read later
            files.add(file)
//                }
        }
        // Return the list of cached files
        return files.toList()
    }

    /**
     * Cleans a path. This is used to clean a path. It will remove the trailing slash and prepend a slash if it doesn't exist.
     * @param pathIn the path to clean.
     * @return the cleaned path.
     */
    private fun cleanPath(pathIn: String): VPath {
        // If the path is empty, return the root path
        if (pathIn.isEmpty()) return VPath("/", "jar")
        var path = pathIn
        //remove the trailing slash
        if (path.endsWith("/")) path = path.substring(0, path.length - 1)
        //prepend the slash if it doesn't exist
        if (!path.startsWith("/")) path = "/$path"
        //return the path with the jar scheme
        return VPath(path, "jar")
    }

    /**
     * Provides an implementation specific way to delete a file. This is used to delete a file from the file system.
     * All fileHandles that are open to the file that is being deleted will be closed. And the cached file will be removed.
     * You must reopen the file handle if you wish by listening to the [VFSEvent.Type.DELETE] event.
     *
     * @param path the path to the file that is being deleted.
     * @return true if the file was deleted successfully, false otherwise.
     *         (If the file does not exist or if the file has a read only attribute, it will return false )
     */
    override fun delete(path: VPath): Boolean {
        throw UnsupportedOperationException("Cannot delete in a jar file.")

    }

    /**
     * Provides an implementation specific way to move a file. This is used to move a file from one location to another.
     *
     * All fileHandles that are open to the file that is being moved will be closed. You must reopen the file handle if you wish
     * by listening to the [VFSEvent.Type.MOVE] event.
     *
     * Will override the file if it already exists in the index by updating its cached document and write to file system.
     *
     * @param path the path to the file that is being moved.
     * @param newPath the path to the new location of the file.
     * @return true if the file was moved successfully, false otherwise.
     */
    override fun move(path: VPath, newPath: VPath): Boolean {
        throw UnsupportedOperationException("Cannot move a file within a jar file.")
    }
}