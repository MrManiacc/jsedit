package me.jraynor.gui

import imgui.ImGui
import imgui.ImGuiStyle
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import me.jraynor.gui.views.TextEditor
import java.util.concurrent.ConcurrentHashMap

class Dockspace(private val name: String) : IRenderer {
    private val viewports: MutableMap<String, Viewport> = ConcurrentHashMap()
    private val preRendered: MutableMap<String, Viewport> = ConcurrentHashMap()
    private var needsUpdate = false
    /**
     * This will add the viewport to the dockspace. If [preRender] is true, it will be rendered before the main window.
     */
    fun addViewport(viewport: Viewport, preRender: Boolean = false) {
        if (preRender)
            preRendered[viewport.name] = viewport
        else
            viewports[viewport.name] = viewport
        needsUpdate = true
    }

    /**
     * This will get the viewport with the given name. If [searchPreRender] is true, it will search the preRendered
     */
    fun <T : Viewport> getViewport(name: String, searchPreRender: Boolean = false): T? {
        val viewport =
            viewports[name] ?: if (searchPreRender) preRendered[name] else null ?: return null
        return viewport as T
    }

    /**
     * This will be called before rendering the main dockspace. It will render all preRendered viewports
     * and create the main dockspace window.
     */
    private fun preRender() {
        for (view in preRendered.values)
            view.render()
        val size = ImGui.getIO().displaySize
        val viewport = ImGui.getMainViewport()
        ImGui.setNextWindowPos(0f, 0f)
        ImGui.setNextWindowSize(size.x, size.y)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGui.begin(
            "Window##$name", ImGuiWindowFlags.NoNavFocus or
                    ImGuiWindowFlags.NoTitleBar or
                    ImGuiWindowFlags.NoCollapse or
                    ImGuiWindowFlags.NoResize or
                    ImGuiWindowFlags.NoMove or
                    ImGuiWindowFlags.NoBringToFrontOnFocus
        )
        ImGui.setNextWindowViewport(viewport.id)
        ImGui.popStyleVar()
    }

    override fun render() {
        preRender()
        var dockspaceID = ImGui.getID(name)
        val node = imgui.internal.ImGui.dockBuilderGetNode(dockspaceID)
        if(needsUpdate){

            imgui.internal.ImGui.dockBuilderRemoveNode(dockspaceID)
            needsUpdate = false
            buildDockspace()
        }
        if (node == null || node.ptr == 0L || node.id == 0) //Null ptr? it we should now create?
            buildDockspace()
        dockspaceID = ImGui.getID(name)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 225f, 140f);
        ImGui.dockSpace(dockspaceID, 0f, 0f, ImGuiDockNodeFlags.NoWindowMenuButton or ImGuiDockNodeFlags.NoCloseButton)
        ImGui.end()
        ImGui.popStyleVar()

        for (view in viewports.values) {
            ImGui.begin(view.name)
            view.render()
            ImGui.end()
        }
        for (view in viewports.values)
            view.postRender()

        for (view in preRendered.values)
            view.postRender()
        Popups.process()
    }


    /**
     * This will build the dockspace and all of the viewports within it calling [Viewport.buildDock]
     * on each viewport.
     */
    private fun buildDockspace() {
        val viewport = ImGui.getWindowViewport()
        val dockspaceID = ImGui.getID(name)
        imgui.internal.ImGui.dockBuilderRemoveNode(dockspaceID)
        imgui.internal.ImGui.dockBuilderAddNode(
            dockspaceID,
            ImGuiDockNodeFlags.DockSpace or ImGuiDockNodeFlags.NoTabBar
        )
        imgui.internal.ImGui.dockBuilderSetNodeSize(dockspaceID, viewport.sizeX, viewport.sizeY)
        val dockMainId = ImInt(dockspaceID)
        for (view in viewports.values)
            view.buildDock(dockMainId)
        imgui.internal.ImGui.dockBuilderFinish(dockspaceID)

    }

    fun removeViewport(textEditor: Viewport) {
        viewports.remove(textEditor.name)
        needsUpdate = true
    }


}