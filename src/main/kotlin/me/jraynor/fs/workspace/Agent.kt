package me.jraynor.fs.workspace

fun interface Subscription {
    fun invoke(action: Action): Boolean
}

/**
 * An agent is a wrapper around a callback to a workspace action. It is used to provide a means to send a response back to the client.
 */
data class Agent(val target: Action, private val callbacks: MutableList<Subscription>) {
    /**
     * Sends a response back to the client
     */
    fun respond(response: Action) {
        for (callback in callbacks)
            if (callback.invoke(response))
                break
    }

    fun subscribe(sub: Subscription) = callbacks.add(sub)

}