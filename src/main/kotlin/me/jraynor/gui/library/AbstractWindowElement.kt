package me.jraynor.gui.library

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImInt

abstract class AbstractWindowElement() : AbstractRenderElement(), IDockableElement {
    private var flags: Int = ImGuiWindowFlags.NoCollapse
    abstract val name: String

    /**
     * Add a flag to the window
     */
    protected fun flag(flag: Int) {
        flags = flags or flag
    }

    /**
     * Allow for custom rendering before the window is rendered
     */
    protected open fun preWindowRender() = Unit

    /**
     * Makes an imgui window then calls render methods in the correct order
     */
    override fun render() {
        preWindowRender()
        if (ImGui.begin(name, flags)) {
            onRender()
            children.forEach { it.provide().render() }
            onPostRender()
        }
        ImGui.end()
    }

    /**
     * Called before the dockspace is created. Used for main menu bars.
     */
    open fun onInitialRender() = Unit

    /**
     * Called after all children have been rendered, used for popups.
     */
    open fun onFinalRender() = Unit

    /**
     * Attempts to remove self from parent
     */
    protected fun close() {
        parent?.removeChild(name)
    }

    /**
     * Builds a default dock of a window in the center of the parent dockspace
     */
    override fun buildDock(dockspaceID: ImInt): ImInt {
        imgui.internal.ImGui.dockBuilderDockWindow(name, dockspaceID.get())
        return dockspaceID
    }
}