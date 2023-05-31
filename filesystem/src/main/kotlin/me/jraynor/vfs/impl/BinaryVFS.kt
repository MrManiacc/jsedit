package me.jraynor.vfs.impl

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import me.jraynor.vfs.*
import me.jraynor.vfs.impl.accessors.BinaryAccessor
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Represents a VFS project. This is an in memory VFS meaning there's no physical representation of it within
 * a physical file disk that you could for example navigate to in Windows explorer.
 */
class BinaryVFS(workspacePath: VPath) : BaseVFS<BinaryAccessor>(workspacePath) {
    private var cache = FileDocumentCache()

    fun save() {
        val output = Output(FileOutputStream(root.path.toFile()))
        updateCache()
        kryo.writeClassAndObject(output, cache.cachedDocuments)
        output.close()
    }

    private fun updateCache() {
        for (file in cachedFiles.values) {
            val document = open(file.path).read()
            cache.cachedDocuments[file] = document.data
            close(document.ref)
        }

    }

    fun load() {
        if (!root.path.toFile().exists()) return
        val input = Input(FileInputStream(root.path.toFile()))
        cache = kryo.readObject(input, FileDocumentCache::class.java)
        buildTree()
        input.close()
    }

    private fun buildTree() {
        for (file in cache.cachedDocuments.keys) {
//            val document = cache.cachedDocuments[file]!!
            val ref = open(file.path) //cache the files
            //TODO: maybe we should just cache the file and write the document when we save?
            // write(Document(ref, document))
            close(ref)
        }
    }

    private val kryo = Kryo().apply {
        register(VPath::class.java)
        register(VFile::class.java)
        register(ByteArray::class.java)
        register(Map::class.java)
        register(MutableSet::class.java)
        register(HashSet::class.java)
        register(VFS::class.java)
        register(BinaryVFS::class.java)
        register(MutableMap::class.java)
        register(HashMap::class.java)
        register(FileDocumentCache::class.java)
        register(BinaryAccessor::class.java)
    }

    class FileDocumentCache(val cachedDocuments: HashMap<VFile, ByteArray> = HashMap()){

    }

    /**
     * Provides an implementation specific way to create a file accessor. This is used to read and write to the file.
     */
    override val accessor: BinaryAccessor = BinaryAccessor(::cache, ::cache)

    /**
     * Checks if the file exists in the file system.
     * @return true if the file exists, false otherwise.
     */
    override fun contains(path: VPath): Boolean {
        //TODO: this is a hack, we should probably just cache the files
        for (file in cache.cachedDocuments.keys)
            if (file.path == path)
                return true
        return false
    }


}