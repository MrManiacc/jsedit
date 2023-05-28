package me.jraynor.vfs

class VFSClassloader(val vfs: VFS) : ClassLoader(getSystemClassLoader()) {

    override fun loadClass(name: String?): Class<*> {
        val path = VPath(name?.replace(".", "/") + ".class", "jar")
        if(!vfs.contains(path))
            return super.loadClass(name)
        val handle = vfs.open(path)
        val read = vfs.read(handle)
        return defineClass(name, read.data, 0, read.data.size)
    }


}