package me.jraynor.gui.library

import java.util.concurrent.ConcurrentHashMap


/**
 * A container for all the values of a given type T that are named and can be accessed by name
 */
class ValueBag<T : Any> : Iterable<ValueProvider<T>> {
    private val valueStore: MutableMap<String, ValueProvider<T>> = ConcurrentHashMap()

    /**
     * Adds the given value to the bag and returns the provider or returns an empty provider if the value was empty.
     */
    fun add(value: ValueProvider<T>): ValueProvider<T> {
        if (value.isEmpty() || !value.isNamed()) return EmptyProvider()
        valueStore[value.name!!] = value
        return value
    }

    /**
     * Removes the given value from the bag and returns it or returns an empty provider
     * if it was not found in the bag or the bag was empty to begin with
     */
    fun remove(value: ValueProvider<T>): ValueProvider<T> {
        if (value.isEmpty() || !value.isNamed()) error("Cannot add an empty value to the bag or empty name.")
        return valueStore.remove(value.name!!) ?: EmptyProvider()
    }

    /**
     * Removes the given value from the bag and returns it or returns an empty provider.
     */
    fun remove(name: String): ValueProvider<T> =
        valueStore.remove(name) ?: EmptyProvider()


    /**
     * Removes the given value from the bag and returns it or returns an empty provider.
     */
    fun <T : Any> cast(): ValueBag<T> {
        return this as ValueBag<T>
    }

    /**
     * Adds the given value to the bag
     */
    operator fun plusAssign(value: ValueProvider<T>) {
        add(value)
    }

    /**
     * Adds the given value to the bag
     */
    operator fun plus(value: ValueProvider<T>): ValueBag<T> {
        add(value)
        return this
    }


    /**
     * @return true if the bag contains the given name
     */
    operator fun get(name: String): ValueProvider<T> = valueStore[name] ?: EmptyProvider()


    /**
     * @return true if the bag contains the given name
     */
    operator fun contains(name: String): Boolean = valueStore.containsKey(name)

    /**
     * gets all the values as a collection
     */
    fun get(): Collection<T> = valueStore.values.map { it.provide() }

    override fun iterator(): Iterator<ValueProvider<T>> = valueStore.values.iterator()


}