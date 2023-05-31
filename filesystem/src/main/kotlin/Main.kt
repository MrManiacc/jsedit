import me.jraynor.vfs.impl.SystemVFS
import me.jraynor.vfs.vpath

fun main() {
    val vfs = SystemVFS("C:\\Users\\jraynor\\IdeaProjects\\lua") // or whatever your implementation is
    vfs.index("/".vpath)
    vfs.write(vfs.open("test.txt".vpath).document("Hello Worldss!"))
}