package me.jraynor.gui.elements

import imgui.ImGui
import imgui.flag.*
import imgui.type.ImInt
import me.jraynor.gui.helpers.Action
import me.jraynor.gui.helpers.Popups
import me.jraynor.gui.helpers.Codicon
import me.jraynor.gui.library.AbstractWindowElement
import me.jraynor.gui.library.casted
import me.jraynor.io.Disk
import me.jraynor.io.IOElement
import me.jraynor.io.File
import me.jraynor.io.Folder

class DiskExplorerElement(private val disk: Disk, override val name: String = "Project View") :
    AbstractWindowElement() {
    private var updated = false
    private var expandState = -1


    override fun onRender() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0f, 0f)
        renderFolder(disk.root)
//        renderHeader()
        ImGui.popStyleVar(2)
        if (updated) {
            updated = false
            expandState = -1
        }
    }

    private fun renderHeader() {
        ImGui.setCursorPosY(ImGui.getWindowContentRegionMinY() - 2)
        ImGui.setCursorPosX(ImGui.getWindowContentRegionMaxX() - 80f)
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 50f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 6f, 3f)
        if (ImGui.button("${Codicon.ICON_EXPAND_ALL}")) {
            expandState = 0
        }
        if (ImGui.isItemHovered())
            ImGui.setTooltip("Expand all folders")
        ImGui.sameLine()
        if (ImGui.button("${Codicon.ICON_COLLAPSE_ALL}")) {
            expandState = 1
        }
        if (ImGui.isItemHovered())
            ImGui.setTooltip("Collapse all folders")
//        ImGui.setCursorPosY(ImGui.getCursorPosY() +4)

        ImGui.popStyleVar(2)
        ImGui.setCursorPosY(ImGui.getCursorPosY() - 30f)
    }


    private fun openFile(file: File) {
        val dock = parent?.casted<DockspaceElement>() ?: return
        if (dock.hasChild(file.name))
            ImGui.setWindowFocus(file.name)
        else
            dock.addChild(CodeElement(file))

    }

    private fun renderFolderContext(folder: Folder) {
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("New File")) {
                Popups.play(Action(disk, "new_file", folder))
            }
            ImGui.separator()
            if (ImGui.menuItem("New Folder")) {
                Popups.play(Action(disk, "new_folder", folder))
            }
            ImGui.separator()
            if (ImGui.menuItem("Rename")) {
                Popups.play(Action(disk, "rename", folder))
            }
            ImGui.separator()
            if (ImGui.menuItem("Delete")) {
                Popups.play(Action(disk, "delete", folder))
            }
            ImGui.endPopup()
        }
    }

    private fun renderFileContext(file: File) {
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Rename")) {
                Popups.play(Action(disk, "rename", file))

            }
            ImGui.separator()
            if (ImGui.menuItem("Delete")) {
                Popups.play(Action(disk, "delete", file))
            }
            ImGui.endPopup()
        }
    }

    /**
     * renders a file
     */
    private fun renderFile(file: File) {
        ImGui.indent(35f)

        if (ImGui.selectable(
                "${Codicon.ICON_FILE_CODE} ${file.name}",
                false,
                ImGuiSelectableFlags.SpanAllColumns
            )
        ) openFile(file)
        renderFileContext(file)

        // Check if this item is being dragged
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            // Set the payload to carry the pointer of our item
            ImGui.setDragDropPayload("DND_FILE", file)

            // Display a preview (could be anything, e.g. when dragging an image we could decide to display
            // the original image or a smaller version of it)
            ImGui.text("${Codicon.ICON_FILE_CODE} ${file.name}")

            ImGui.endDragDropSource()
        }

        ImGui.unindent(35f)
    }

    private fun renderFolder(folder: Folder) {

        if (expandState == 0) {
            ImGui.setNextItemOpen(true)
            updated = true
        } else if (expandState == 1) {
            if (folder.name != "/")
                ImGui.setNextItemOpen(false)
            updated = true
        }
        val flags = if (expandState == 0) ImGuiTreeNodeFlags.DefaultOpen else ImGuiTreeNodeFlags.None
        val open = ImGui.treeNodeEx(
            "${Codicon.ICON_FOLDER} ${folder.name}",
            flags or ImGuiTreeNodeFlags.AllowItemOverlap or ImGuiTreeNodeFlags.SpanFullWidth
        )
        // Check if this item is being dragged
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            // Set the payload to carry the pointer of our item
            ImGui.setDragDropPayload("DND_FOLDER", folder)

            // Display a preview
            ImGui.text("${Codicon.ICON_FOLDER} ${folder.name}")

            ImGui.endDragDropSource()
        }

        renderFolderContext(folder)

        // Check if we are a target of a drag and drop operation
        if (ImGui.beginDragDropTarget()) {
            var payload = ImGui.acceptDragDropPayload<IOElement>("DND_FILE")

            if (payload != null) {
                val file = payload as File
                // Do something with the dropped file
                disk.move(file, folder)
            }

            payload = ImGui.acceptDragDropPayload("DND_FOLDER")

            if (payload != null) {
                val droppedFolder = payload as Folder
                // Do something with the dropped folder
                disk.move(
                    droppedFolder,
                    folder
                )
            }

            ImGui.endDragDropTarget()
        }

        if (open) {
            folder.content.filterIsInstance<Folder>().forEach {
                ImGui.indent(15f)
                renderFolder(it)
                ImGui.unindent(15f)
            }
            folder.content.filterIsInstance<File>().forEach {
                renderFile(it)
            }
            ImGui.treePop()
        }
    }

    override fun buildDock(dockspaceID: ImInt): ImInt {
        val dockLeft: Int =
            imgui.internal.ImGui.dockBuilderSplitNode(dockspaceID.get(), ImGuiDir.Left, 0.16f, null, dockspaceID)
        imgui.internal.ImGui.dockBuilderDockWindow(name, dockLeft)
        return dockspaceID
    }

}

