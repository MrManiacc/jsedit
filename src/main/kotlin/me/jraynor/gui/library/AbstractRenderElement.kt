package me.jraynor.gui.library

import me.jraynor.os.OperatingSystem

abstract class AbstractRenderElement(parent: AbstractRenderElement? = null) : IRenderElement {
    protected val properties: ValueLibrary = ValueLibrary()
    protected val children = ValueBag<IRenderElement>()
    protected var parent: AbstractRenderElement? = parent
        private set

    protected open val os: OperatingSystem? get() =  parent?.os

    /**
     * Returns true if this is the root element
     */
    val isRoot: Boolean
        get() = parent == null


    /**
     * Called when this is added to a parent
     */
    protected open fun onAdded(parent: AbstractRenderElement) = Unit

    /**
     * This is actually called to do the rendering. Its implementation is required by the user.
     */
    protected abstract fun onRender()

    /**
     *  Called after all children have been rendered
     */
    protected open fun onPostRender() = Unit

    /**
     * Called when a child is removed
     */
    protected open fun onRemoved() = Unit

    /**
     * Called when a child is added
     */
    protected open fun onChildAdded(child: IRenderElement) = Unit

    /**
     * Called when a child is removed
     */
    protected open fun onChildRemoved(child: IRenderElement) = Unit


    /**
     * Renders itself and then all of its children
     */
    override fun render() {
        onRender()
        children.forEach { it.provide().render() }
        onPostRender()
    }

    /**
     * Disposes of its self first by calling [onRemoved] and then disposes of all of its children
     */
    private fun added(parent: AbstractRenderElement) {
        this.parent = parent
        onAdded(parent)
        children.get().filterIsInstance<AbstractRenderElement>().forEach {
            it.added(this)
        }
    }

    /**
     * Disposes of its self first by calling [onRemoved] and then disposes of all of its children
     */
    private fun removed() {
        onRemoved()
        children.get().filterIsInstance<AbstractRenderElement>().forEach(AbstractRenderElement::removed)
    }


    /**
     * Adds the given value to the library. return true if added successfully
     */
    fun addChild(name: String, child: IRenderElement): Boolean {
        val added = !children.add(ValueProvider.of(name, child)).isEmpty()
        if (added && child is AbstractRenderElement)
            child.added(this)
        onChildAdded(child)
        return added
    }


    /**
     * Attempts to remove a child, returns true if removed successfully
     */
    fun removeChild(name: String): Boolean {
        val removed = children.remove(name)
        if (removed.isEmpty()) return false
        val instance = removed.provide()
        if (instance is AbstractRenderElement)
            instance.removed()
        onChildRemoved(instance)
        return true
    }


    /**
     * Checks to see if the given child exists
     */
    fun hasChild(name: String): Boolean = children.contains(name)

    /**
     * Gets the child of the given name
     */
    fun getChild(name: String): IRenderElement? = if (hasChild(name)) children[name].provide() else null

    /**
     * Gets all the children
     */
    fun getChildren(): List<IRenderElement> = children.get().toList()

    /**
     * Gets all the children of the given type [T]
     */
    fun <T : IRenderElement> getChildrenOfType(type: Class<T>): List<T> = children.get().filterIsInstance(type)

    /**
     * Gets all the children of the given type [T]
     */
    inline fun <reified T : IRenderElement> getChildrenOfType(): List<T> = getChildrenOfType(T::class.java)
}