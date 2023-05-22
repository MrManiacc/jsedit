package me.jraynor.gui

import imgui.type.ImInt
import me.jraynor.os.OperatingSystem

/**
 * Represents a viewport in the dockspace. You may setup the docking for this viewport by overriding [buildDock].
 * In the backend, the viewports dockspace is rendered as a window within the parent Viewspace
 */
interface Viewport {
    /**
     * The display name/unique identifier for this viewport, defined as the classname by default.
     */
    val name: String
        get() = javaClass.simpleName
    /**
     * The parent dockspace is required to be able to look for sibling viewports
     */
    val os: OperatingSystem


    /**
     * This renders within the main dockspace window
     */
    fun render()

    /**
     * This renders after the window. It allows for popups and other things to be rendered outside of the window
     */
    fun postRender() = Unit

    /**
     * Create the dock for the dockspace, defaults to the center of the dockspace
     */
    fun buildDock(parentId: ImInt) {
        imgui.internal.ImGui.dockBuilderDockWindow(name, parentId.get())
    }

}