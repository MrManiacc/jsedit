package me.jraynor.os

import me.jraynor.os.disk.Disk
import me.jraynor.os.disk.File
import me.jraynor.os.vm.parser.LuaBaseListener
import me.jraynor.os.vm.parser.LuaLexer
import me.jraynor.os.vm.parser.LuaParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.io.FileSystem
import party.iroiro.luajava.ClassPathLoader
import party.iroiro.luajava.ExternalLoader
import party.iroiro.luajava.Lua
import party.iroiro.luajava.luajit.LuaJit
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.FileAttribute
import java.util.concurrent.atomic.AtomicInteger


class
VirtualMachine(private val os: OperatingSystem) {


    fun execute(source: String): Map<Int, String> {
        executeJavScript(source)

        return emptyMap()

    }

    private fun executeJavScript(source: String) {
        try {
            val context = Context.newBuilder("js").fileSystem(configureBuilder())
                .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL).build())
//                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup { true }
                .allowIO(true)
                .build()

            context.getBindings("js").putMember("os", os)
            context.eval(org.graalvm.polyglot.Source.newBuilder("js", source, "main.jsm").mimeType("application/javascript+module").build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun configureBuilder(): FileSystem {
        return object : FileSystem {
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
    }

    fun execute(file: File) = execute(String(file.content))

    private class LuaLoader(private val disk: Disk) : ExternalLoader {
        private val fallback = ClassPathLoader()
        override fun load(module: String, L: Lua): Buffer? {
            var path = module.replace(".", "/")
            path = if (path.startsWith("/")) path.substring(1) else path
            path = if (path.endsWith(".lua")) path else "$path.lua"
            val file = disk.findFile(path)
            if (file != null) {
                return load(file.content)
            }
            return fallback.load(module, L)
        }

        private fun load(inputByteArray: ByteArray): Buffer? {
            return try {
                val output = ByteArrayOutputStream()
                val buffer = ByteBuffer.allocateDirect(inputByteArray.size)
                output.write(inputByteArray)
                output.writeTo(ClassPathLoader.BufferOutputStream(buffer))
                buffer.flip()
                buffer
            } catch (e: Exception) {
                null
            }
        }
    }

    fun shutdown() {
    }
}
