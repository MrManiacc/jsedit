package me.jraynor.fs

import me.jraynor.fs.workspace.File
import me.jraynor.fs.workspace.FileStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FileStoreTest {
    private lateinit var fileSystem: FileStore

    @BeforeEach
    fun setup() {
        // Initialize the FileSystem before each test
        fileSystem = FileStore("TestFS", 1, "Test file system")
    }

    @Test
    fun testFile() {
        val file = File("test.kt", "kotlin", "fun main() {}")
        assertEquals("test.kt", file.name)
        assertEquals("kotlin", file.fileType)
        assertEquals("fun main() {}", file.contents)
        assertEquals("test", file.nameWithoutExtension)
    }

    @Test
    fun testFolder() {
        val folder = Folder("src", mutableSetOf())
        val file = File("Main.kt", "kotlin", "fun main() {}")
        assertTrue(folder.addFile(file))

        // Check the file was added
        assertEquals(file, folder.getFile("Main.kt"))

        // Check the removal of the file
        assertTrue(folder.removeFile(file))
        assertNotEquals(file, folder.getFile("Main.kt"))
    }

    @Test
    fun testAddFile() {
        val file = File("testFile.txt", "txt", "This is a test file", 1)
        assertTrue(fileSystem.addFile("/dir1/dir2", file))

        // Check that the file was added
        assertEquals(file, fileSystem.findFile("/dir1/dir2/testFile.txt"))
    }

    @Test
    fun testAddFileOverwrite() {
        val file1 = File("testFile.txt", "txt", "This is a test file", 1)
        val file2 = File("testFile2.txt", "txt", "This is a second test file", 1)
        assertTrue(fileSystem.addFile("/dir1/dir2", file1))
        assertTrue(fileSystem.addFile("/dir1/dir2", file2))

        // Check that the second file overwrote the first one
        assertEquals(file2, fileSystem.findFile("/dir1/dir2/testFile2.txt"))
    }

    @Test
    fun testRemoveFile() {
        val file = File("testFile.txt", "txt", "This is a test file", 1)
        assertTrue(fileSystem.addFile("/dir1/dir2", file))

        // Remove the file
        assertTrue(fileSystem.removeFile("/dir1/dir2/testFile.txt"))

        // Check that the file was removed
        assertEquals(File.EMPTY, fileSystem.findFile("/dir1/dir2/testFile.txt"))
    }

    @Test
    fun testMoveFile() {
        val file = File("testFile.txt", "txt", "This is a test file", 1)
        assertTrue(fileSystem.addFile("/dir1/dir2", file))

        // Move the file
        assertTrue(fileSystem.moveFile("/dir1/dir2/testFile.txt", "/dir3/dir4/newTestFile.txt"))

        // Check that the file was moved
        assertEquals(file, fileSystem.findFile("/dir3/dir4/newTestFile.txt"))
    }

    @Test
    fun testFindFile() {
        val file = File("testFile.txt", "txt", "This is a test file", 1)
        assertTrue(fileSystem.addFile("/dir1/dir2", file))

        // Find the file
        assertEquals(file, fileSystem.findFile("/dir1/dir2/testFile.txt"))
    }

    @Test
    fun testFindFileNotFound() {
        // Find a file that doesn't exist
        assertEquals(File.EMPTY, fileSystem.findFile("/non/existent/file"))
    }


}