package me.jraynor.vfs.impl

import me.jraynor.vfs.VFS
import me.jraynor.vfs.VFSClassloader
import me.jraynor.vfs.VPath
import me.jraynor.vfs.vpath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class SystemVFSTest {
    @Test
    fun `Test open returns valid VHandle`() {
        val path = VPath.of("../")
        val vfs = SystemVFS(path)
        val indexed = vfs.index(VPath.of("/"))
        val project = vfs.find("../filesystem".vpath)
        assertNotNull(project)
        val src = project!!.canonicalLookup("src/main/kotlin/me/jraynor/vfs/impl")
        assertNotNull(src)
        src?.dump()
        val resources = src!!.canonicalLookup("../../../../../../test/resources")
        assertNotNull(resources)
        val jarFile = resources!!.canonicalLookup("chess.jar")
        assertNotNull(jarFile)
        val handle = vfs.open(jarFile!!.path)
        assertFalse(handle.isClosed)
        val jarVfs = JarVFS(jarFile.path)
        jarVfs.index(jarVfs.root.path)

    }


}