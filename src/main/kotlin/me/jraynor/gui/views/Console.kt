package me.jraynor.gui.views

import imgui.*
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.type.ImInt
import imgui.type.ImString
import me.jraynor.gui.SourceCodePro
import me.jraynor.gui.Viewport
import me.jraynor.os.OperatingSystem


class Console(override val os: OperatingSystem) : Viewport {
    private val command = ImString(100)
    private var needsFocus = false


    override fun render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 20f, 20f)
        ImGui.pushStyleVar(ImGuiStyleVar.ChildBorderSize, 20f)
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 1.0f, 0.0f, 0.0f, 0.0f)
        ImGui.pushFont(SourceCodePro.getFont())
        if (ImGui.beginChild("Output", 0f, -ImGui.getFrameHeightWithSpacing())) {
            ImGui.indent()
            os.getOutput().forEach {
                textWithColors(it)
            }
            ImGui.unindent()
            if (os.isDirty()) {
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
            os.execute(command.get())
            needsFocus = true
            command.set("")
        }
    }

    private fun parseColor(index: Int, buffer: String): ImVec4 {
        val color = ImVec4()
        val colorStr = buffer.substring(index, index + 6)
        color.x = Integer.parseInt(colorStr.substring(0, 2), 16) / 255.0f
        color.y = Integer.parseInt(colorStr.substring(2, 4), 16) / 255.0f
        color.z = Integer.parseInt(colorStr.substring(4, 6), 16) / 255.0f
        color.w = 1.0f
        return color
    }

    fun textWithColors(tempStr: String) {

        var accumulated = ""
        var currentColor: ImVec4? = null
        var inColor = 0
        val draw: () -> Unit = {
            if (currentColor == null)
                ImGui.textUnformatted(accumulated)
            else
                ImGui.textColored(currentColor!!.x, currentColor!!.y, currentColor!!.z, currentColor!!.w, accumulated)
            ImGui.sameLine(0.0f, 0.0f)
            accumulated = ""
        }
        for (i in tempStr.indices) {
            val char = tempStr[i]
            if (char == '$') {
                if (tempStr[i + 1] == '$') {
                    currentColor = null
                } else {
                    draw()
                    ImGui.sameLine(0.0f, 0.0f)
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
            if (i == tempStr.length - 1) {
                draw()
            }
        }
        ImGui.newLine()


//        while (textCur < tempStr.length && tempStr[textCur] != '\u0000') {
//            if (tempStr[textCur] == '#') {
//                // Print accumulated text
//                if (textCur != textStart) {
//                    ImGui.textUnformatted(tempStr.substring(textStart, textCur))
//                    ImGui.sameLine(0.0f, 0.0f)
//                }
//
//                // Process color code
//                val colorStart = textCur + 1
//                textCur = tempStr.indexOf('#', colorStart)  // find next `
//                if (textCur == -1) textCur = tempStr.length  // if no more `, go to the end
//
//                // Change color
//                if (pushedColorStyle) {
//                    ImGui.popStyleColor()
//                    pushedColorStyle = false
//                }
//
//                val textColor = ImVec4()
//                if (processInlineHexColor(tempStr.substring(colorStart, textCur), textColor)) {
//                    ImGui.pushStyleColor(ImGuiCol.Text, textColor.x, textColor.y, textColor.z, textColor.w)
//                    pushedColorStyle = true
//                }
//
//                textStart = textCur + 1
//            } else if (tempStr[textCur] == '\n') {
//                // Print accumulated text and go to the next line
//                ImGui.textUnformatted(tempStr.substring(textStart, textCur))
//                textStart = textCur + 1
//            }
//
//            textCur++
//        }


    }

    fun processInlineHexColor(colorStr: String, color: ImVec4): Boolean {
        try {
            if (colorStr.length == 6 || colorStr.length == 8) {
                val r = colorStr.substring(0, 2).toInt(16)
                val g = colorStr.substring(2, 4).toInt(16)
                val b = colorStr.substring(4, 6).toInt(16)
                var a = 255
                if (colorStr.length == 8) {
                    a = colorStr.substring(6, 8).toInt(16)
                }
                // ImGui uses colors in the range [0, 1], so we divide by 255.0f
                color.set(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f)
                return true
            }
        } catch (e: NumberFormatException) {
            // Invalid color string, ignore
        }
        return false
    }

    override fun buildDock(parentId: ImInt) {
        val downb: Int =
            imgui.internal.ImGui.dockBuilderSplitNode(parentId.get(), imgui.flag.ImGuiDir.Down, 0.33f, null, parentId)
        imgui.internal.ImGui.dockBuilderDockWindow(name, downb)
    }
}