import imgui.app.Application
import me.jraynor.Window
import me.jraynor.gui.elements.ConsoleElement
import me.jraynor.gui.elements.DiskExplorerElement
import me.jraynor.gui.elements.DockspaceElement
import me.jraynor.gui.helpers.*
import me.jraynor.os.OperatingSystem
import me.jraynor.os.io.Disk
import me.jraynor.os.io.DiskIO
import java.io.File

fun main(args: Array<String>) {
    val os = OperatingSystem(if (File("os.dat").exists()) DiskIO.load("os.dat") else Disk())
    Runtime.getRuntime().addShutdownHook(Thread { os.shutdown() })
    FontAtlas.addFont(Futura)
    FontAtlas.addFont(Codicon)
    FontAtlas.addFont(SourceCodePro)
    val view = DockspaceElement(os, "MainView")
    view.addChild(ConsoleElement())
    view.addChild(DiskExplorerElement(os.disk))
    Application.launch(Window(view, Eclipse))
    os.shutdown()
}