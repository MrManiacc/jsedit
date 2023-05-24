package me.jraynor.gui.library

import imgui.type.ImInt

interface IDockableElement : IRenderElement {


    /**
     * Builds the dockspace, by default, it will build the dockspace in the center of the parent dockspace.
     */
    fun buildDock(dockspaceID: ImInt): ImInt
}