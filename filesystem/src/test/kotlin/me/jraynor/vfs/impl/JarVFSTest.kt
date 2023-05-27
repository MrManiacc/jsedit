package me.jraynor.vfs.impl

import me.jraynor.vfs.VFS
import me.jraynor.vfs.VPath
import org.junit.jupiter.api.Assertions.*
import java.io.File

class JarVFSTest {
    @org.junit.jupiter.api.Test
    fun `Indexes a jar file`() {
        val vfs: BaseVFS =
//            JarVFS(File("C:\\Users\\jraynor\\IdeaProjects\\untitled1\\build\\libs\\untitled1-1.0-SNAPSHOT.jar"))
            JarVFS(File("C:\\Users\\jraynor\\IdeaProjects\\lua\\filesystem\\src\\test\\resources\\chess.jar"))

        vfs.index(vfs.root.path)
        vfs.root.children.forEach {
            println(it.path)
        }

    }


    @org.junit.jupiter.api.Test
    fun `Test open and close VHandle`() {
    }

    @org.junit.jupiter.api.Test
    fun `Test exception for access after closed`() {
    }

    @org.junit.jupiter.api.Test
    fun `Test multiple handles against their references`() {
    }

    @org.junit.jupiter.api.Test
    fun `Test handled closed and removed from cached handles`() {
    }
}