package me.jraynor.gui.views

import imgui.ImGui
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.flag.ImGuiStyleVar
import imgui.type.ImInt
import me.jraynor.gui.Codicon
import me.jraynor.gui.SourceCodePro
import me.jraynor.gui.Viewport
import me.jraynor.os.disk.File
import me.jraynor.os.OperatingSystem
import java.nio.charset.Charset

class TextEditor(override val os: OperatingSystem, private var file: File) : Viewport {

    override val name: String = "${file.name} ${Codicon.ICON_FILE_TEXT}"

    private val editor = TextEditor().apply {
        setLanguageDefinition(TextEditorLanguageDefinition.c())
        setShowWhitespaces(false)
        text = String(file.content, Charset.defaultCharset())
    }

    fun updateFile(file: File) {
        this.file = file
        editor.text = String(file.content)
    }

    /**
     * Renders our main explorer window
     */
    override fun render() {
        if (file.isRemoved) {
            os.view.removeViewport(this)
            return
        }
        ImGui.dummy(0f, 0f)
        ImGui.sameLine(ImGui.getContentRegionAvailX() - 110f)
        ImGui.setCursorPosY(ImGui.getCursorPosY() - 4f)
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 50f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 6f, 3f)
        if (ImGui.button("${Codicon.ICON_RUN}")) {
            file.content = editor.text.toByteArray()
            editor.setErrorMarkers(os.execute(file))
        }
        ImGui.sameLine()
        if (ImGui.button("${Codicon.ICON_SAVE}")) {
            file.content = editor.text.toByteArray()
            os.save()
        }
        ImGui.sameLine()
        if (ImGui.button("${Codicon.ICON_CHROME_CLOSE}")) {
            file.content = editor.text.toByteArray()
            os.view.removeViewport(this)
        }
        ImGui.popStyleVar(2)
        ImGui.pushFont(SourceCodePro.getFont())
        editor.render("TextEditor")
        ImGui.popFont()
    }

    /**
     * Creates a dock on the left side of the dockspace with a default size of 23%.
     */
    override fun buildDock(parentId: ImInt) {
        imgui.internal.ImGui.dockBuilderDockWindow(name, parentId.get())
    }
}