import imgui.app.Application
import me.jraynor.Window
import me.jraynor.gui.*
import me.jraynor.gui.views.Console
import me.jraynor.gui.views.Explorer
import me.jraynor.os.OperatingSystem
import me.jraynor.os.disk.Disk
import me.jraynor.os.disk.DiskIO
import java.io.File

fun main(args: Array<String>) {
    val os = OperatingSystem(if (File("os.dat").exists()) DiskIO.load("os.dat") else Disk(), Dockspace("MainView"))
    Runtime.getRuntime().addShutdownHook(Thread { os.shutdown() })

    FontAtlas.addFont(Futura)
    FontAtlas.addFont(Codicon)
    FontAtlas.addFont(SourceCodePro)
    os.view.addViewport(Console(os))
    os.view.addViewport(Explorer(os))
    Application.launch(Window(os, os.view, Eclipse))
    os.shutdown()

}