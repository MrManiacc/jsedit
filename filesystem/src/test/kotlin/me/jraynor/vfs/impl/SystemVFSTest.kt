package me.jraynor.vfs.impl

import me.jraynor.vfs.VFS
import me.jraynor.vfs.VFSClassloader
import me.jraynor.vfs.VPath
import me.jraynor.vfs.vpath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URL
import java.net.URLClassLoader
import javax.print.DocFlavor

class SystemVFSTest {
    @Test
    fun `Test open returns valid VHandle`() {
        val vfs = SystemVFS("C:\\Users\\jraynor\\IdeaProjects\\lua\\filesystem") // or whatever your implementation is
        vfs.index("/".vpath)
        val src = vfs.find("src/main/kotlin".vpath)
        assertNotNull(src)
        val resources = src!!.canonicalLookup("../../test/resources")
        assertNotNull(resources)
        val jarFile = resources!!.canonicalLookup("chess.jar")
        assertNotNull(jarFile)
        val handle = vfs.open(jarFile!!.path)
        assertFalse(handle.isClosed)
        val jarVfs = JarVFS(jarFile.path)
        jarVfs.index(jarVfs.root.path)
        val classLoader = VFSClassloader(jarVfs)
        val clazz = classLoader.loadClass("MainKt")
        try{
           val main = clazz.methods.find { it.name == "main" }?.invoke(null)

        }catch (ex: Exception){
            ex.printStackTrace()
        }
        vfs.close(handle)
    }


}