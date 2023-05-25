package me.jraynor.gui.elements

import com.google.common.eventbus.Subscribe
import imgui.ImColor
import imgui.ImFont
import imgui.ImGui
import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import imgui.type.ImString
import me.jraynor.gui.helpers.Codicon
import me.jraynor.gui.helpers.SourceCodePro
import me.jraynor.gui.library.AbstractRenderElement
import me.jraynor.gui.library.AbstractWindowElement
import me.jraynor.os.Events
import me.jraynor.os.fs.ConsoleStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConsoleElement(override val name: String = "Console") : AbstractWindowElement() {
    private val command = ImString(100)
    private val filter = ImString(100)
    private var needsFocus = false
    override fun onAdded(parent: AbstractRenderElement) {
        flag(
            ImGuiWindowFlags.NoTitleBar or
                    ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoBackground or ImGuiWindowFlags.NoDecoration
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
        ConsoleStream.println("$level$prefix$caller$message")
        needsFocus = true
    }


    override fun preWindowRender() {
    }

    override fun onRender() {

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 20f, 10f)
        ImGui.pushStyleVar(ImGuiStyleVar.ChildBorderSize, 10f)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 1.0f, 0.0f, 0.0f, 0.0f)
        ImGui.pushStyleColor(ImGuiCol.Border,  ImGui.getColorU32(ImGuiCol.WindowBg))
        ImGui.pushFont(SourceCodePro.getFont())
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 10f)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 120, 120, 120, 255)
        ImGui.spacing()
        ImGui.setCursorPosY(ImGui.getCursorPosY() -15f)
        if (ImGui.beginChild("Output", ImGui.getContentRegionAvailX(), (-ImGui.getFrameHeightWithSpacing()) - 5f, true, ImGuiWindowFlags.NoScrollbar)) {
//            ImGui.pushItemWidth(250f)
//            ImGui.inputText("##Filter", filter, ImGuiInputTextFlags.CallbackAlways)
//            ImGui.sameLine()
//            ImGui.setCursorPosX(ImGui.getCursorPosX() - 80f)
//            ImGui.text("Filter ${Codicon.ICON_FILTER}")
//            ImGui.indent()
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 1f)
            ConsoleStream.iterateLines { i, it ->
                if (filter.get().isNotEmpty() && !it.contains(filter.get(), true))
                    return@iterateLines
                textWithColors(it)
                ImGui.dummy(0f, 0f)
            }
            ImGui.popStyleVar()
            ImGui.unindent()
            if (needsFocus) {
                ImGui.setScrollHereY(1.0f) // Auto scroll to the bottom
            }
        }
        ImGui.endChild()
        ImGui.popFont()
        ImGui.popStyleVar(3)
        ImGui.popStyleColor(3)
        //Draws a ronded rect around the input
        ImGui.getWindowDrawList().addRectFilled(
            ImGui.getCursorScreenPos().x - 20f,
            ImGui.getCursorScreenPos().y - 8f,
            ImGui.getCursorScreenPos().x + ImGui.getContentRegionAvailX() + 20f,
            ImGui.getCursorScreenPos().y + ImGui.getFrameHeightWithSpacing() + 8f,
            ImGui.getColorU32(ImGuiCol.WindowBg),
            10f
        )
        if (needsFocus) {
            ImGui.setKeyboardFocusHere()
            needsFocus = false
        }



        ImGui.indent()
        ImGui.textColored(ImColor.rgba(255, 255, 255, 255), "${Codicon.ICON_TERMINAL_POWERSHELL}")
        ImGui.sameLine()
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 10f)
        ImGui.pushStyleColor(ImGuiCol.FrameBg, ImGui.getColorU32(ImGuiCol.WindowBg))
        ImGui.pushItemWidth(ImGui.getContentRegionAvailX())
        if (ImGui.inputText("##Command", command, ImGuiInputTextFlags.EnterReturnsTrue)) {
            os?.execute(command.get())
            needsFocus = true
            command.set("")
        }
        ImGui.popStyleColor()
        ImGui.popStyleVar()

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
    val spaceLeft get() = ImGui.getContentRegionAvailX()

    fun textWithColors(tempStr: String) {
        val font = ImGui.getFont()

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
                        val spaceLeft = spaceLeft
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
                val spaceLeft = spaceLeft
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