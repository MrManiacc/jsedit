package me.jraynor.gui.elements

import com.google.common.eventbus.Subscribe
import imgui.ImColor
import imgui.ImFont
import imgui.ImGui
import imgui.ImVec2
import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.ImGuiWindow
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import imgui.type.ImString
import me.jraynor.gui.helpers.Codicon
import me.jraynor.gui.helpers.SourceCodePro
import me.jraynor.gui.library.AbstractRenderElement
import me.jraynor.gui.library.AbstractWindowElement
import me.jraynor.os.Events
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConsoleElement(override val name: String = "Console") : AbstractWindowElement() {
    private val command = ImString(100)
    private val filter = ImString(100)
    private var needsFocus = false
    private val output = mutableListOf<String>()

    override fun onAdded(parent: AbstractRenderElement) {
        flag(
            ImGuiWindowFlags.NoTitleBar or
                    ImGuiWindowFlags.NoCollapse
        )
        Events.register(this)
    }

    override fun onRemoved() {
        Events.unregister(this)
    }


    @Subscribe
    private fun print(event: Events.Console.Log) {
        val caller =
            if (event.frame != null) {
                val pos = event.frame
                val caller = pos.sourceLocation

                "\$ffffff(\$d4d660${caller.source.name}:${caller.startLine}\$ffffff): "
            } else " "
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val now = time.format(formatter)
        val prefix = if (event.time) "\$54d676$now\$ffffff - " else " "
        val level =
            if (event.level != Events.Console.Level.NONE) "\$ffffff[${event.level.color}${event.level.name}\$ffffff] " else " "
        val message = "\$a1aeb5${event.message}"
        output.add("$level$prefix$caller$message")
        needsFocus = true
    }


    @Subscribe
    private fun clear(event: Events.Console.Clear) {
        if (event.lastOnly)
            if (output.isNotEmpty()) output.removeAt(output.size - 1) else return
        else output.clear()
    }

    override fun preWindowRender() {
    }

    override fun onRender() {
        ImGui.pushItemWidth(250f)
        ImGui.inputText("##Filter", filter, ImGuiInputTextFlags.CallbackAlways)
        ImGui.sameLine()
        ImGui.setCursorPosX(ImGui.getCursorPosX() - 80f)

        ImGui.text("Filter ${Codicon.ICON_FILTER}")
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 20f, 20f)
        ImGui.pushStyleVar(ImGuiStyleVar.ChildBorderSize, 20f)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 1.0f, 0.0f, 0.0f, 0.0f)
        ImGui.pushFont(SourceCodePro.getFont())
        if (ImGui.beginChild("Output", 0f, (-ImGui.getFrameHeightWithSpacing()) - 5f)) {
            ImGui.indent()
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 1f)
            output.forEach {
                if (filter.get().isNotEmpty() && !it.contains(filter.get(), true))
                    return@forEach
                textWithColors(it)
                ImGui.pushStyleColor(ImGuiCol.Separator, ImGui.getColorU32(ImGuiCol.Border))
                ImGui.separator()
                ImGui.popStyleColor()
            }
            ImGui.popStyleVar()
            ImGui.unindent()
            if (needsFocus) {
                ImGui.setScrollHereY(1.0f) // Auto scroll to the bottom
            }
        }
        ImGui.endChild()
        ImGui.popFont()
        ImGui.popStyleVar(2)
        ImGui.popStyleColor()
        if (needsFocus) {
            ImGui.setKeyboardFocusHere()
            needsFocus = false
        }
        ImGui.pushItemWidth(ImGui.getContentRegionAvailX())
        if (ImGui.inputText("##Command", command, ImGuiInputTextFlags.EnterReturnsTrue)) {
            os?.execute(command.get())
            needsFocus = true
            command.set("")
        }
    }

    override fun onPostRender() {
        super.onPostRender()
    }

    private fun parseColor(index: Int, buffer: String): ImVec4 {
        val color = ImVec4()
        try {
            val colorStr = buffer.substring(index, index + 6)
            color.x = Integer.parseInt(colorStr.substring(0, 2), 16) / 255.0f
            color.y = Integer.parseInt(colorStr.substring(2, 4), 16) / 255.0f
            color.z = Integer.parseInt(colorStr.substring(4, 6), 16) / 255.0f
            color.w = 1.0f
        } catch (e: Exception) {
            e.printStackTrace()
            return ImVec4(1f, 1f, 1f, 1f)
        }
        return color
    }

    fun textWithColors(tempStr: String) {
        val start = ImGui.getCursorStartPos()
        val font = ImGui.getFont()
        val spaceLeft = ImGui.getContentRegionAvailX() - ImGui.getCursorPosX()

        var accumulated = ""
        var currentColor: ImVec4? = null
        var inColor = 0

        val draw: (String) -> Unit = { str ->
            if (currentColor == null)
                ImGui.textUnformatted(str)
            else
                ImGui.textColored(currentColor!!.x, currentColor!!.y, currentColor!!.z, currentColor!!.w, str)
        }

        for (i in tempStr.indices) {
            val char = tempStr[i]

            if (char == '$') {
                if (tempStr[i + 1] == '$') {
                    currentColor = null
                } else {
                    if (accumulated.isNotEmpty()) {
                        val textSize = font.calcTextSizeA(font.fontSize, Float.MAX_VALUE, Float.MAX_VALUE, accumulated)
                        if (textSize.x > spaceLeft) {
                            val parts = splitToFit(accumulated, spaceLeft, font)
                            parts.forEach {
                                draw(it)
                                ImGui.newLine()
                            }
                        } else {
                            draw(accumulated)
                            ImGui.sameLine()
                        }
                    }
                    accumulated = ""
                    currentColor = parseColor(i + 1, tempStr)
                    inColor = 6
                }
            } else {
                if (inColor > 0) {
                    inColor--
                    continue
                }
                accumulated += char
            }
            if (i == tempStr.length - 1 && accumulated.isNotEmpty()) {
                val textSize = font.calcTextSizeA(font.fontSize, Float.MAX_VALUE, Float.MAX_VALUE, accumulated)
                if (textSize.x > spaceLeft) {
                    val parts = splitToFit(accumulated, spaceLeft, font)
                    parts.forEach {
                        draw(it)
                        ImGui.newLine()
                        ImGui.setCursorPosY(ImGui.getCursorPosY() - ImGui.getFontSize())

                    }
                } else {
                    draw(accumulated)
                    ImGui.sameLine()
                }
            }
        }

    }

    fun splitToFit(str: String, maxWidth: Float, font: ImFont): List<String> {
        val words = str.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val textSize = font.calcTextSizeA(font.fontSize, Float.MAX_VALUE, Float.MAX_VALUE, "$currentLine $word")
            if (textSize.x > maxWidth) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine += " $word"
            }
        }

        lines.add(currentLine)

        return lines
    }

    override fun buildDock(parentId: ImInt): ImInt {
        val downb: Int =
            imgui.internal.ImGui.dockBuilderSplitNode(parentId.get(), imgui.flag.ImGuiDir.Down, 0.33f, null, parentId)
        imgui.internal.ImGui.dockBuilderDockWindow(name, downb)
        imgui.internal.ImGui.dockBuilderGetNode(downb).localFlags =
            ImGuiDockNodeFlags.NoTabBar or ImGuiDockNodeFlags.NoWindowMenuButton
        return parentId
    }
}