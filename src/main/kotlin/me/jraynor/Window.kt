package me.jraynor

import imgui.ImGui
import imgui.ImGuiIO
import imgui.app.Application
import imgui.app.Configuration
import imgui.flag.ImGuiConfigFlags
import me.jraynor.gui.helpers.FontAtlas
import me.jraynor.gui.library.IRenderElement
import me.jraynor.gui.helpers.IStyle
import me.jraynor.os.OperatingSystem


class Window(private val renderer: IRenderElement, private val style: IStyle) : Application() {
    override fun initImGui(config: Configuration?) {
        super.initImGui(config)
        val io: ImGuiIO = ImGui.getIO()
        io.iniFilename = null // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard) // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable) // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.DpiEnableScaleViewports) // Enable Multi-Viewport / Platform Windows
        io.configViewportsNoTaskBarIcon = true
        style.apply(ImGui.getStyle())
        FontAtlas.configure(io)
    }


    override fun process() {
        renderer.render()
    }
}
