package me.jraynor.vfs.impl

import me.jraynor.vfs.*
import java.io.File
import java.io.InputStream
import java.util.jar.JarFile

class JarVFS(jarPath: File) : BaseVFS(VPath.of("/")) {
    private val accessor = InternalJarAccessor(jarPath, ::cache)

    private class InternalJarAccessor(private var file: File, private val createAndCache: (path: VPath) -> VFile) :
        FileAccessor {
        private var jarFile: JarFile = JarFile(file)


        /**
         * Reads the data from the file. This is used to read data from a file.
         * @param file the file to read the data from.
         * @return the data that was read from the file.
         */
        override fun read(file: VFile): ByteArray {
            val stream =
                file.meta.getAs<InputStream>("stream") ?: jarFile.getInputStream(jarFile.getJarEntry(file.path.path))
            file.meta["stream"] = stream // cache the entry in the meta of the file
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
        override fun write(file: VFile, data: ByteArray) {
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
         * @param path the path to the directory.
         * @return a list of paths to the children of the directory.
         */
        override fun index(path: VPath): List<VFile> {
            // This will hold the cached files
            val files = mutableListOf<VFile>()
            // Go through the entries in the jar file
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                // Create a VPath object from the entry name
                val file =
                    createAndCache(VPath("/${entry.name}", "jar"))
                println("${file.path.path}  - entry: ${entry.realName}")
                file.meta["stream"] = (jarFile.getInputStream(entry)) //store the jar entry for read later
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
            return files
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


    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override fun fileAccessor(): FileAccessor = accessor


}