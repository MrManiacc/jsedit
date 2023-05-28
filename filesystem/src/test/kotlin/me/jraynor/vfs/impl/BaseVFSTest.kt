package me.jraynor.vfs.impl

import me.jraynor.vfs.*
import kotlin.test.*

class BaseVFSTest {
    @Test
    fun `Test open returns valid VHandle`() {
        val testPath = VPath.of("/foo/bar/test.txt")
        val vfs: VFS = SystemVFS() // or whatever your implementation is
        val testHandle = vfs.open(testPath) // use some VPath
        assertEquals(testPath, testHandle.handle.path)
    }

    @Test
    fun `Test open and close VHandle`() {
        val testPath = VPath.of("/foo/bar/test.txt")
        val vfs: VFS = SystemVFS() // or whatever your implementation is
        val testHandle = vfs.open(testPath) // use some VPath
        assertEquals(testPath, testHandle.handle.path)
        vfs.close(testHandle)
        assertTrue(testHandle.isClosed)
    }

    @Test
    fun `Test exception for access after closed`() {
        val testPath = VPath.of("/foo/bar/test.txt")
        val vfs: VFS = SystemVFS() // or whatever your implementation is
        val testHandle = vfs.open(testPath) // use some VPath
        assertEquals(testPath, testHandle.handle.path)
        vfs.close(testHandle)
        assertTrue(testHandle.isClosed)
        assertFailsWith<IllegalStateException> {
            testHandle.read()
        }
    }

    @Test
    fun `Test multiple handles against their references`() {
        val testPath = VPath.of("/foo/bar/test.txt")
        val vfs: VFS = SystemVFS() // or whatever your implementation is
        val testHandleA = vfs.open(testPath)
        val testHandleB = vfs.open(testPath)
        assertEquals(testHandleA.handle, testHandleB.handle)
        assertNotEquals(testHandleA, testHandleB)
    }

    @Test
    fun `Test handled closed and removed from cached handles`() {
        val testPath = VPath.of("/foo/bar/test.txt")
        val vfs: VFS = SystemVFS() // or whatever your implementation is
        val testHandleA = vfs.open(testPath)
        val testHandleB = vfs.open(testPath)
        assertTrue(vfs.lookup(testPath).contains(testHandleA))
        assertTrue(vfs.lookup(testPath).contains(testHandleB))
        vfs.close(testHandleA)
        assertFalse(vfs.lookup(testPath).contains(testHandleA))
        assertTrue(vfs.lookup(testPath).contains(testHandleB))
    }

    @Test
    fun `Test the creation of the folder tree structure upon opening file`() {
        val testPath = VPath.of("/foo/bar/test.txt")
        val vfs = SystemVFS() // or whatever your implementation is
        val testHandleA = vfs.open(testPath)
        assertNotNull(testHandleA.handle.parent)
        assertNotNull(vfs.find(testHandleA.handle.path.parent))
        println(vfs.dump())
    }


    @Test
    fun `Test multiple files on filesystem dump`() {
        val vfs: VFS = SystemVFS() // or whatever your implementation is
        vfs.open(VPath.of("/foo/bar/test.txt"))
        vfs.open(VPath.of("/foop/test2.txt"))
        vfs.open(VPath.of("/fool/doo/poo/test.txt"))
        vfs.open(VPath.of("/fool/doo/cow/test.txt"))
        vfs.open(VPath.of("/fool/pow/row/test.txt"))
        vfs.open(VPath.of("/fool/pow/row/foop/test.txt"))
        vfs.open(VPath.of("/fool/pow"))
        assertTrue {
            vfs.lookup(VPath.of("/fool/pow")).size == 1
                    && vfs.lookup(VPath.of("/fool/pow/row")).isEmpty()
                    && vfs.lookup(VPath.of("/fool/pow/row/test.txt")).size == 1
                    && vfs.find(VPath.of("/fool/pow/row")) != null
        }
    }

    @Test
    fun `Testing windows path normalization`() {
        val path = VPath.of("C:\\test\\foo\\fan\\ase\\test.txt")
        assertEquals("/c/test/foo/fan/ase/test.txt", path.path)
    }

}
