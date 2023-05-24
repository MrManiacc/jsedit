package me.jraynor.gui.library

import kotlin.reflect.KClass

/**
 * This is a container for all the values
 */
class ValueLibrary {
    private val values: MutableMap<KClass<*>, ValueBag<*>> = HashMap()

    /**
     * Adds the given value to the library creating a new bag if needed.
     * If the value already exists, it will throw an error
     */
    fun <T : Any> addValue(value: ValueProvider<T>): ValueProvider<T> {
        if (values.containsKey(value.type)) {
            val bag = values[value.type]!!.cast<T>()
            if (bag.contains(value.name!!))
                error("Cannot add a value with the same name as another value. Please override the name property")
            bag.add(value)
        } else values[value.type] = (ValueBag<T>().add(value)) as ValueBag<*>
        return value
    }

    /**
     * Removes the given value from the library.
     * @return the value that was removed or EmptyProvider if it was not found
     */
    fun <T : Any> removeValue(value: ValueProvider<T>): ValueProvider<T> {
        if (values.containsKey(value.type)) {
            val bag = values[value.type]!!.cast<T>()
            return bag.remove(value)
        }
        return EmptyProvider()
    }


    /**
     * Gets the value of the given type and name or returns an empty provider
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getValue(type: KClass<T>, name: String): ValueProvider<T> {
        if (values.containsKey(type)) {
            val bag = values[type]!!
            if (bag.contains(name)) return bag[name] as ValueProvider<T>
        }
        return EmptyProvider()
    }

    /**
     * Gets the value of the given type and name or returns an empty provider
     */
    inline fun <reified T : Any> getValue(name: String): ValueProvider<T> = getValue(T::class, name)

}
