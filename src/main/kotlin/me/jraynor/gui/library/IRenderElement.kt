package me.jraynor.gui.library

fun interface IRenderElement {
    fun render()
}

/**
 * This is a helper method that allows us to cast this to a given type
 */
inline fun <reified T : IRenderElement> IRenderElement.casted(): T =
    if (this is T) this else throw IllegalStateException("Cannot cast ${this::class.simpleName} to ${T::class.simpleName}")

