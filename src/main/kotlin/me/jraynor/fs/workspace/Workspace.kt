package me.jraynor.fs.workspace

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * A workspace contains a filesystem. It is responsible for handling all the messages that are sent to the server and then providing a means to
 * delegate responses back to the client. It is a high level wrapper around the LSP protocol.
 *
 */

data class Workspace(
    private val fileSystem: FileStore,
    private val inputStream: InputStream,
    private val outputStream: OutputStream
) {
    private val actions: MutableMap<String, Agent> = mutableMapOf()
    private var job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    /**
     * Registers a given action to the subscription list
     */
    fun subscribe(target: Action, subscription: Subscription) {
        val method = target.parameters["method"]
            ?: error("Expected an action to be registered with the method: such as \"textDocument/didOpen\"")
        if (method !is String) error("Expected the action to be a string")
        subscriptions(method).subscribe(subscription)
    }

    fun subscribe(name: String, subscription: Subscription) =
        subscribe(Action.of { string("method", name) }, subscription)

    /**
     * Post the action to all the subscriptions of the given method
     */
    fun post(action: Action) {
        if (!action.parameters.containsKey("method") || !actions.containsKey(action.parameters["method"])) return
        subscriptions(action.parameters["method"]!! as String).respond(action)
    }

    /**
     * Get or create a subscription model for the given method
     */
    private fun subscriptions(method: String): Agent {
        if (!actions.containsKey(method)) actions[method] =
            Agent(Action.of { string("method", method) }, mutableListOf())
        return actions[method]!!
    }

    fun start() {
        scope.launch {
            try {
                while (true) {
                    receiveAction()
                }
            } catch (ex: Exception) {
                //Handle exception if needed
            }
        }
    }

    private suspend fun receiveAction() {
        val actionJson = inputStream.bufferedReader(Charset.defaultCharset()).readLine()
        if (actionJson != null) {
            val action = Action.fromJson(actionJson)
            val method = action.string("method")
            val subscription = subscriptions(method)
            subscription.respond(action)
        }
    }

    suspend fun sendAction(action: Action) = writeMessageBytes(action.toString().toByteArray(Charsets.UTF_8))

    @Throws(IOException::class)
    private fun writeMessageBytes(messageBytes: ByteArray) {
        val contentLength = messageBytes.size
        val header = String.format(Locale.ENGLISH, "Content-Length: %d\r\n\r\n", contentLength)
        val headerBytes = header.toByteArray(StandardCharsets.US_ASCII)
        synchronized(outputStream) {
            outputStream.write(headerBytes)
            outputStream.write(messageBytes)
            outputStream.flush()
        }
    }

    fun shutdown() {
        job.cancel()
    }
}