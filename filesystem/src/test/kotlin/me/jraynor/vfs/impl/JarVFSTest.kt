package me.jraynor.vfs.impl

import me.jraynor.vfs.VFS
import me.jraynor.vfs.VPath
import org.junit.jupiter.api.Assertions.*

class JarVFSTest {
    @org.junit.jupiter.api.Test
    fun `Indexes a jar file`() {
        val vfs: VFS =
//            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))
            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))

        val cached = vfs.index(vfs.root.path)
        assertNotNull(vfs.find(VPath("/helloworld.txt", "jar")))
    }

    @org.junit.jupiter.api.Test
    fun `reads the helloworld txt file from the base of the jar`() {
        val vfs: VFS =
//            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))
            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))

        vfs.index(vfs.root.path)
        val handle = vfs.open(VPath("/helloworld.txt", "jar"))
        val read = vfs.read(handle)
        assertEquals("Hi Mom!", String(read.data))
    }


    @org.junit.jupiter.api.Test
    fun `Test exception for access after closed`() {
        val vfs: VFS =
//            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))
            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))

        vfs.index(vfs.root.path)
        val handleA = vfs.open(VPath("/helloworld.txt", "jar"))
        val handleB = vfs.open(VPath("/helloworld.txt", "jar"))
        val read = vfs.read(handleA)
        assertEquals("Hi Mom!", String(read.data))
        vfs.close(handleA)
        assertThrows(IllegalStateException::class.java) {
            vfs.read(handleA)
        }
        assertThrows(UnsupportedOperationException::class.java) {
            vfs.write(handleB.document("Hello World!".toByteArray()))
        }
        vfs.close(handleB)
    }

    @org.junit.jupiter.api.Test
    fun `Test multiple handles against their references`() {
        val vfs: VFS =
            JarVFS(VPath.of("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))

        vfs.index(vfs.root.path)
        val handleA = vfs.open(VPath("/helloworld.txt", "jar"))
        val handleB = vfs.open(VPath("/helloworld.txt", "jar"))
        assertEquals(handleA.handle, handleB.handle)
        assertNotEquals(handleA, handleB)
        vfs.close(handleA)
        assertThrows(IllegalStateException::class.java) {
            vfs.read(handleA)
        }
    }

    @org.junit.jupiter.api.Test
    fun `Test handled closed and removed from cached handles`() {
        val vfs =
            JarVFS(VPath.of("/c/Program Files/Java/jdk-17/lib/jrt-fs.jar"))
        vfs.index(vfs.root.path)
        assertNotNull(vfs.find(VPath("/jdk/internal/jrtfs/SystemImage\$1.class", "jar")))
        val handle = vfs.open(VPath("/jdk/internal/jrtfs/SystemImage\$1.class", "jar"))
        vfs.close(handle)
        assertThrows(IllegalStateException::class.java) {
            vfs.read(handle)
        }
    }
}