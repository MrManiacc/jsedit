package me.jraynor.gui.views

import imgui.ImGui
import imgui.flag.*
import imgui.type.ImInt
import me.jraynor.gui.Codicon
import me.jraynor.gui.Popups
import me.jraynor.gui.Viewport
import me.jraynor.os.*
import me.jraynor.os.disk.DiskElement
import me.jraynor.os.disk.File
import me.jraynor.os.disk.Folder

class Explorer(override val os: OperatingSystem) : Viewport {
    private var updated = false
    private var expandState = -1

    /**
     * Renders our main explorer window
     */
    override fun render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0f, 0f)
        renderFolder(os.disk.root)
        renderHeader()
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
            var payload = ImGui.acceptDragDropPayload<DiskElement>("DND_FILE")

            if (payload != null) {
                val file = payload as File
                // Do something with the dropped file
                os.disk.move(file, folder)
            }

            payload = ImGui.acceptDragDropPayload("DND_FOLDER")

            if (payload != null) {
                val droppedFolder = payload as Folder
                // Do something with the dropped folder
                os.disk.move(
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


    private fun openFile(file: File) {
        val name = "${file.name} ${Codicon.ICON_FILE_TEXT}"
        val view = (os.view.getViewport<TextEditor>(name))
        if (view == null) {
            val editor = TextEditor(os, file)
            os.view.addViewport(editor)
        } else {
            view.updateFile(file)
            ImGui.setWindowFocus(name)
        }
    }

    private fun renderFolderContext(folder: Folder) {
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("New File")) {
                Popups.play(Popups.Action(os.disk, "new_file", folder))
            }
            ImGui.separator()
            if (ImGui.menuItem("New Folder")) {
                Popups.play(Popups.Action(os.disk, "new_folder", folder))
            }
            ImGui.separator()
            if (ImGui.menuItem("Rename")) {
                Popups.play(Popups.Action(os.disk, "rename", folder))
            }
            ImGui.separator()
            if (ImGui.menuItem("Delete")) {
                Popups.play(Popups.Action(os.disk, "delete", folder))
            }
            ImGui.endPopup()
        }
    }

    private fun renderFileContext(file: File) {
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Rename")) {
                Popups.play(Popups.Action(os.disk, "rename", file))

            }
            ImGui.separator()
            if (ImGui.menuItem("Delete")) {
                Popups.play(Popups.Action(os.disk, "delete", file))
            }
            ImGui.endPopup()
        }
    }


    /**
     * Creates a dock on the left side of the dockspace with a default size of 23%.
     */
    override fun buildDock(parentId: ImInt) {
        val dockLeft: Int =
            imgui.internal.ImGui.dockBuilderSplitNode(parentId.get(), ImGuiDir.Left, 0.23f, null, parentId)
        imgui.internal.ImGui.dockBuilderDockWindow(name, dockLeft)
    }
}