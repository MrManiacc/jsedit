package me.jraynor.os

import me.jraynor.vfs.VFS
import me.jraynor.vfs.VPath
import me.jraynor.vfs.impl.SystemVFS
import java.io.File

class Os(val workingDirectory: File) {
    private val vfs: VFS = SystemVFS(workingDirectory)



}