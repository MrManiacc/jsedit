package me.jraynor.gui.elements

import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import me.jraynor.gui.helpers.Popups
import me.jraynor.gui.library.AbstractRenderElement
import me.jraynor.gui.library.AbstractWindowElement
import me.jraynor.gui.library.IDockableElement
import me.jraynor.gui.library.IRenderElement
import me.jraynor.os.OperatingSystem
import kotlin.random.Random
import kotlin.random.nextUInt

class DockspaceElement(override val os: OperatingSystem, private val dockspaceName: String = "Dockspace_${Random.nextUInt(69420000u)}") :
    AbstractRenderElement() {
    private val dockspaceID: Int get() = ImGui.getID(dockspaceName)
    private var dirty = false

    override fun onRender() {
        for (child in children)
            if (child is AbstractWindowElement)
                child.onInitialRender()
        preRender()
        prepareDockspace()
    }

    private fun preRender() {
        val size = ImGui.getIO().displaySize
        val viewport = ImGui.getMainViewport()
//        ImGui.setNextWindowPos(0f, 0f)
//        ImGui.setNextWindowSize(size.x, size.y)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        val flags = ImGuiWindowFlags.NoNavFocus or
                ImGuiWindowFlags.NoTitleBar or
                ImGuiWindowFlags.NoResize or
                ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus
        ImGui.begin(
            "Window##$dockspaceName"
        )
        ImGui.setNextWindowViewport(viewport.id)
        ImGui.popStyleVar()
    }


    /**
     * Mark the dockspace as dirty if the child is a [IDockableElement]
     */
    override fun onChildAdded(child: IRenderElement) {
        if (child is IDockableElement)
            dirty = true
    }

    /**
     * Mark the dockspace as dirty if the child is a [IDockableElement]
     */
    override fun onChildRemoved(child: IRenderElement) {
        if (child is IDockableElement)
            dirty = true
    }

    /**
     * Do the final render after all children have been rendered.
     */
    override fun onPostRender() {
        for (child in children)
            if (child is AbstractWindowElement)
                child.onFinalRender()
        Popups.process()
    }

    /**
     * A delegate method for [addChild] that will add the child with the given name.
     */
    fun addChild(child: AbstractWindowElement): Boolean {
        return addChild(child.name, child)
    }

    /**
     * This will prepare the dockspace for rendering. If the dockspace is dirty, it will rebuild the dockspace.
     */
    private fun prepareDockspace() {
        if (dirty) {
            imgui.internal.ImGui.dockBuilderRemoveNode(dockspaceID)
            dirty = false
            buildDockspace()
        }
        val node = imgui.internal.ImGui.dockBuilderGetNode(dockspaceID)
        if (node == null || node.ptr == 0L || node.id == 0)
            buildDockspace()
        ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 225f, 140f);
        ImGui.dockSpace(dockspaceID, 0f, 0f)
        ImGui.end()
        ImGui.popStyleVar()
    }

    /**
     * This will build the dockspace and all of the viewports within it calling [Viewport.buildDock]
     * on each viewport.
     */
    private fun buildDockspace() {
        val viewport = ImGui.getWindowViewport()
        imgui.internal.ImGui.dockBuilderRemoveNode(dockspaceID)
        imgui.internal.ImGui.dockBuilderAddNode(
            dockspaceID,
            ImGuiDockNodeFlags.DockSpace or ImGuiDockNodeFlags.NoTabBar
        )
        imgui.internal.ImGui.dockBuilderSetNodeSize(dockspaceID, viewport.sizeX, viewport.sizeY)
        val dockMainId = ImInt(dockspaceID)
        for (view in getChildrenOfType<IDockableElement>())
            view.buildDock(dockMainId)
        imgui.internal.ImGui.dockBuilderFinish(dockspaceID)
    }


}