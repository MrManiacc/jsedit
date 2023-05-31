package me.jraynor

import me.jraynor.osgui.*

import imgui.ImGui
import imgui.ImGuiIO
import imgui.app.Application
import imgui.app.Configuration
import imgui.flag.ImGuiConfigFlags

class NativeWindow(private val renderer: Renderer, private val style: IStyle) : Application() {
    override fun initImGui(config: Configuration?) {
        super.initImGui(config)
        val io: ImGuiIO = ImGui.getIO()
        io.iniFilename = null // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard) // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable) // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable) // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.DpiEnableScaleViewports) // Enable Multi-Viewport / Platform Windows
        style.apply(ImGui.getStyle())
        FontAtlas.addFont(Futura)
        FontAtlas.addFont(Codicon)
        FontAtlas.addFont(SourceCodePro)
        FontAtlas.configure(io)
    }

    override fun process() {
        renderer.render()
    }
}

fun main() {
    Application.launch(NativeWindow(window {
        name = "Testing window"
        label {
            "Time: ${System.currentTimeMillis()}"
        }
        label("Child window")
        textInput("Text input") {
            println("Text input: $it")
        }
        childWindow("Child") {

        }
    }, Eclipse))
}