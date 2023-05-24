package me.jraynor.os.io

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import java.io.*
import java.time.LocalDateTime
import java.time.ZoneOffset


object DiskIO {
    private val files = mutableListOf<File>()

    private val kryo = Kryo().apply {
        register(File::class.java)
        register(Folder::class.java)
        register(Disk::class.java)
        register(User::class.java)
        register(Role::class.java)
        register(LocalDateTime::class.java)
        register(ZoneOffset::class.java)
    }

    fun save(virtualDisk: Disk, path: String) {
        val output = Output(FileOutputStream(path))
        kryo.writeObject(output, virtualDisk)
        output.close()
    }

    fun load(path: String): Disk {
        val input = Input(FileInputStream(path))
        return kryo.readObject(input, Disk::class.java)
    }

    fun load(path: String, disk: Disk) {
        val out = load(path)
        disk.root.owner = out.root.owner
        disk.root.content.clear()
        disk.root.content.addAll(out.root.content)
    }
}