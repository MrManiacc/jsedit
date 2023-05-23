package me.jraynor.os.vm

import me.jraynor.os.OperatingSystem
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.graalvm.polyglot.io.FileSystem
import java.io.IOException
import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.FileAttribute

class VmFileSystem(private val os: OperatingSystem) : FileSystem {
    override fun parsePath(uri: URI): Path? {
        return Paths.get(uri)
    }

    override fun parsePath(path: String): Path {
        return Paths.get(path)
    }

    override fun checkAccess(path: Path?, modes: MutableSet<out AccessMode>?, vararg linkOptions: LinkOption?) {

    }

    override fun createDirectory(dir: Path?, vararg attrs: FileAttribute<*>?) {
    }

    override fun delete(path: Path?) {

    }

    @Throws(IOException::class)
    override fun newByteChannel(
        path: Path,
        options: Set<OpenOption?>?,
        vararg attrs: FileAttribute<*>?
    ): SeekableByteChannel? {
        val file = os.disk.findFile(path.toString()) ?: return null
        return SeekableInMemoryByteChannel(file.content)
    }

    override fun newDirectoryStream(
        dir: Path?,
        filter: DirectoryStream.Filter<in Path>?
    ): DirectoryStream<Path> {
        return object : DirectoryStream<Path> {
            override fun iterator(): MutableIterator<Path> {
                return object : MutableIterator<Path> {
                    override fun hasNext(): Boolean {
                        return false
                    }

                    override fun next(): Path {
                        return Paths.get("")
                    }

                    override fun remove() {
                    }
                }
            }

            override fun close() {
            }
        }
    }

    override fun toAbsolutePath(path: Path): Path {
        return path
    }

    override fun toRealPath(path: Path?, vararg linkOptions: LinkOption?): Path {
        return path!!
    }

    override fun readAttributes(
        path: Path?,
        attributes: String?,
        vararg options: LinkOption?
    ): MutableMap<String, Any> {
        return mutableMapOf()
    }
}

