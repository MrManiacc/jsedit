package me.jraynor.gui.library

/**
 * This is a null safe empty provider
 */
class EmptyProvider<T : Any> : ValueProvider<T> {
    override val name: String = "EMPTY"

    /**
     * A direct override to return true only should be done here
     */
    override fun isEmpty(): Boolean = true

    override fun provide(): T =
        error("Cannot provide an empty value. Please check isEmpty() before calling provide()")

}