package me.jraynor.gui.library

import kotlin.reflect.KClass


/**
 * This is a helper class that allows us to provide a name and value for a given element
 */
fun interface ValueProvider<T : Any> {
    /** The type of the value **/
    @Suppress("UNCHECKED_CAST")
    val type: KClass<T> get() = (this::class.java as Class<T>).kotlin

    /** The name for the value **/
    val name: String? get() = null

    /**
     * @return true if the name is not null
     */
    fun isNamed(): Boolean = name != null

    /**
     * @return true if the value is empty by checking if the provider is an [EmptyProvider]
     *
     * DO NOT OVERRIDE THIS METHOD
     */
    fun isEmpty(): Boolean = this is EmptyProvider

    /**
     * @return the name and value of the element
     */
    fun provide(): T

    /**
     * This is a helper object that allows us to provide a name and value for a given element
     */
    companion object {
        /**
         * Creates a new value provider with the given name and value
         */
        fun <T : Any> of(name: String, value: T): ValueProvider<T> = object : ValueProvider<T> {
            override val name: String = name
            override fun provide(): T = value
        }

        /**
         * Creates a new value provider with the given name and value
         */
        fun <T : Any> of(name: String, value: () -> T): ValueProvider<T> = object : ValueProvider<T> {
            override val name: String = name
            override fun provide(): T = value()

        }
    }
}
