package me.jraynor.vfs.impl

import me.jraynor.vfs.VPath
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class BinaryVFSTest {
    @Test
    fun `Test in memory creation of files`() {
        val path = VPath.of("../workspace/workspace.dat")
        val vfs = BinaryVFS(path)
//        val handle = vfs.open(VPath.of("test.txt"), true)
//        vfs.write(handle.document("Hello World!"))
//        vfs.save()
    }


}