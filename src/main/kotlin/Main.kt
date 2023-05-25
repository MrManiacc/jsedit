import imgui.app.Application
import me.jraynor.Window
import me.jraynor.gui.elements.ConsoleElement
import me.jraynor.gui.elements.DiskExplorerElement
import me.jraynor.gui.elements.DockspaceElement
import me.jraynor.gui.helpers.*
import me.jraynor.os.OperatingSystem
import me.jraynor.io.Disk
import me.jraynor.io.DiskIO
import me.jraynor.os.fs.File
import me.jraynor.os.fs.FileSystem

fun main(args: Array<String>) {
//    val os = OperatingSystem(if (File("os.dat").exists()) DiskIO.load("os.dat") else Disk())
    val fs = FileSystem("fs", 0, "main fs")
    fs.addFile("/test/testing4/foo/faa", File("testing.js", "js", "console.log('hello world')", 0))
    fs.addFile("/test/testing4/", File("testing2.js", "js", "console.log('hello world2')", 0))
    fs.addFile("/test/testing4/", File("testing3.js", "js", "console.log('hello world2')", 0))
    fs.addFile("/test/testing4/", File("testing4.js", "js", "console.log('hello world2')", 0))
    fs.addFile("/test/testing4/foo", File("testing4.js", "js", "console.log('hello world2')", 0))
    fs.addFile("/test/", File("testing4.js", "js", "console.log('hello world2')", 0))
    fs.dump()

    fs.removeFile("/test/testing4.js")
    fs.dump()
//    Runtime.getRuntime().addShutdownHook(Thread { os.shutdown() })
//    FontAtlas.addFont(Futura)
//    FontAtlas.addFont(Codicon)
//    FontAtlas.addFont(SourceCodePro)
//    val view = DockspaceElement(os, "MainView")
//    view.addChild(ConsoleElement())
//    view.addChild(DiskExplorerElement(os.disk))
//    Application.launch(Window(view, Eclipse))
//    os.shutdown()
}