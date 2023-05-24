package me.jraynor.gui.elements

import imgui.ImGui
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.flag.ImGuiStyleVar
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import me.jraynor.gui.helpers.Codicon
import me.jraynor.gui.helpers.SourceCodePro
import me.jraynor.gui.library.AbstractRenderElement
import me.jraynor.gui.library.AbstractWindowElement
import me.jraynor.io.File
import me.jraynor.os.Events
import org.graalvm.polyglot.PolyglotException
import java.nio.charset.Charset

class CodeElement(val file: File) : AbstractWindowElement() {
    private var flagsSet = false

    /**
     * We do it this way because a file can be renamed.
     */
    override val name: String get() = file.name

    /**
     * Prepare our text editor with our text and language definition
     */
    private val editor = TextEditor().apply {
        setLanguageDefinition(TextEditorLanguageDefinition.c())
        setShowWhitespaces(false)
        text = String(file.content, Charset.defaultCharset())
    }

    override fun onAdded(parent: AbstractRenderElement) {
//        flag(ImGuiWindowFlags.UnsavedDocument)

        editor.palette = editor.lightPalette
    }

    override fun onInitialRender() {
    }

    /**
     * Render our code window
     */
    override fun onRender() {

        if (file.isRemoved) {
            parent?.removeChild(name)
            return
        }
        renderHeader()
        ImGui.pushFont(SourceCodePro.getFont())
        editor.render("TextEditor")
        ImGui.popFont()
        if (!flagsSet) {
            val node = imgui.internal.ImGui.dockBuilderGetNode(imgui.internal.ImGui.getWindowDockID())
            node.localFlags = node.localFlags or ImGuiDockNodeFlags.NoWindowMenuButton
            flagsSet = true
        }
    }


    private fun renderHeader() {
        ImGui.dummy(0f, 0f)
        ImGui.sameLine(ImGui.getContentRegionAvailX() - 200f)
        ImGui.setCursorPosY(ImGui.getCursorPosY() - 4f)
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 50f)
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, 0f, 0f)
        ImGui.popStyleVar()
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 6f, 3f)
        ImGui.sameLine()
        if (ImGui.button("${Codicon.ICON_RUN}")) {
            file.content = editor.text.toByteArray()
            val result = os?.execute(file)
            processResult(result)
        }
        ImGui.sameLine()
        if (ImGui.button("${Codicon.ICON_SAVE}")) {
            file.content = editor.text.toByteArray()
            os?.save()
        }
        ImGui.sameLine()
        if (ImGui.button("${Codicon.ICON_CHROME_CLOSE}")) {
            file.content = editor.text.toByteArray()
            close()
        }
        ImGui.popStyleVar(2)
    }

    private fun processResult(result: Throwable?) {
        when (result) {
            is PolyglotException -> {
                val lineNumber = if (result.sourceLocation == null)
                    result.polyglotStackTrace.firstOrNull()?.sourceLocation?.startLine ?: 1
                else
                    result.sourceLocation.startLine
                editor.setErrorMarkers(hashMapOf(lineNumber to result.message))
                Events.post(
                    Events.Console.Log(
                        "${result.message} at ${result.sourceLocation}" ?: "Unknown error",
                        Events.Console.Level.ERROR,
                        true,
                        null
                    )
                )
            }

            else -> {
                if (result == null)
                    editor.setErrorMarkers(emptyMap())
                else
                    editor.setErrorMarkers(
                        hashMapOf(
                            1 to (result.message ?: "Unknown error ${result?.javaClass?.simpleName}")
                        )
                    )
            }
        }
    }

    override fun buildDock(dockspaceID: ImInt): ImInt {
        super.buildDock(dockspaceID)

        return dockspaceID
    }

}