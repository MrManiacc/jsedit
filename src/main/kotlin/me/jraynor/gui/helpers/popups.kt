package me.jraynor.gui.helpers

import imgui.ImColor
import imgui.ImGui
import imgui.ImVec2
import imgui.flag.*
import imgui.type.ImString
import me.jraynor.os.io.*
import java.time.LocalDateTime

object Popups {
    private var action: Action? = null
    private var min: ImVec2 = ImVec2()
    private var max: ImVec2 = ImVec2()
    private var wantsFocus = false

    fun play(action: Action) {
        Popups.action = action
        if (action.name == "rename")
            action.nameStore.set(action.target.name)
        wantsFocus = true
    }

    fun process() {
        if (action != null) ImGui.openPopup(action!!.name)

        setupStyle()

        val center = ImGui.getMainViewport().center
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f)
        if (ImGui.beginPopup("new_file")) {
            createItem("Create a new file", ::File)
        }

        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f)

        if (ImGui.beginPopup("new_folder")) {
            createItem("Create a new folder", ::Folder)
        }

        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f)
        if (ImGui.beginPopup("rename"))
            processRename()

        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f)
        if (ImGui.beginPopup("delete"))
            processDelete()

        ImGui.popStyleVar(1)
        ImGui.popStyleColor()
    }

    private fun processDelete(){
        if (createHeader("Delete '${action!!.target.name}'")) return
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5f)
        ImGui.indent()
        ImGui.pushFont(SourceCodePro.regular)
        ImGui.text("Delete '${action!!.target.name}'?")
        ImGui.popFont()
        ImGui.pushItemWidth(250f)
        ImGui.unindent()
        ImGui.sameLine()
        ImGui.setCursorPosY(ImGui.getCursorPosY() - 5f)
        if (ImGui.button("Delete")) {
            //TODO delete
            action!!.disk.delete(action!!.target)
            clearContext()
            ImGui.closeCurrentPopup()
        }
        ImGui.dummy(0f, 5f)
        ImGui.endPopup()
    }

    private fun processRename() {
        if (createHeader("Rename '${action!!.target.name}'")) return
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5f)
        ImGui.indent()
        ImGui.pushFont(SourceCodePro.regular)
        ImGui.text("Name:")
        ImGui.popFont()
        ImGui.pushItemWidth(250f)

        ImGui.inputText("##Item Name", action!!.nameStore, ImGuiInputTextFlags.AutoSelectAll)
        if(wantsFocus){
            ImGui.setKeyboardFocusHere(-1)
            wantsFocus = false
        }
        ImGui.unindent()
        ImGui.sameLine()
        if (ImGui.button("Rename")) {
            val oldName =action!!.target.name

            action!!.target.name = action!!.nameStore.get()
            clearContext()
            ImGui.closeCurrentPopup()
        }
        ImGui.dummy(0f, 5f)
        ImGui.endPopup()
    }

    private fun setupStyle() {
        ImGui.pushStyleColor(ImGuiCol.Button, 0.5f, 0.5f, 0.5f, 0.8f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 2f, 2f)

    }

    private fun createHeader(title: String, width: Float = 350f): Boolean {

        ImGui.dummy(width, 0f)
        val drawList = ImGui.getWindowDrawList()
        val start = ImGui.getItemRectMin()
        drawList.addRectFilled(
            start.x - 15f, start.y - 15f,
            start.x + width + 15f,
            start.y + 40f,
            ImColor.rgba(78, 3, 252, 95),
            25f,
            ImDrawFlags.RoundCornersBottom
        )
        ImGui.pushFont(SourceCodePro.large)
        ImGui.setCursorPosX(((width / 2) - ((ImGui.calcTextSize(title).x / 2f) - 10f)))
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5)
        drawList.addRectFilled(min.x - 10, min.y - 2, max.x + 10, max.y + 2, ImColor.rgba(35, 36, 33, 255), 10f)
        ImGui.text(title)
        min = ImGui.getItemRectMin()
        max = ImGui.getItemRectMax()
        ImGui.popFont()
        ImGui.setCursorPosY(ImGui.getCursorPosY() - 5f)

        ImGui.sameLine(10f)
        ImGui.pushStyleColor(ImGuiCol.Button, 35, 36, 33, 255)
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 100f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 2f, 2f)
        if (ImGui.button("${Codicon.ICON_REMOVE_CLOSE}")) {
            action = null
            ImGui.closeCurrentPopup()
            ImGui.popStyleVar(2)
            ImGui.popStyleColor()
            ImGui.endPopup()
            return true
        }
        ImGui.popStyleVar(2)
        ImGui.popStyleColor()
        ImGui.dummy(0f, 20f)

//        drawList.addText(SourceCodePro.getFont(), 25f, min.x, min.y, ImColor.rgba(255, 255, 255, 255), title)
        return false
    }

    private fun createItem(title: String, itemCreator: () -> IOElement) {
        if (createHeader(title)) return
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5f)
        val folder = action!!.target as Folder
        ImGui.indent()
        ImGui.pushFont(SourceCodePro.regular)
        ImGui.text("Name:")
        ImGui.popFont()
        ImGui.pushItemWidth(260f)

        ImGui.inputText("##Item Name", action!!.nameStore, 256)
        if(wantsFocus){
            ImGui.setKeyboardFocusHere(-1)
            wantsFocus = false
        }
        ImGui.unindent()
        ImGui.sameLine()
        if (ImGui.button("Create")) {
            val item = itemCreator()
            item.name = action!!.nameStore.get()
            item.owner = User("root", Role.ADMIN)
            if (item is File) {
                item.lastWritten = LocalDateTime.now()
                item.lastRead = LocalDateTime.now()
            }
            folder.content.add(item)
            clearContext()
            ImGui.closeCurrentPopup()
        }
        ImGui.dummy(0f, 5f)
        ImGui.endPopup()
    }

    private fun clearContext() {
        action = null

    }


}
data class Action(val disk: Disk, val name: String, val target: IOElement, val nameStore: ImString = ImString())
