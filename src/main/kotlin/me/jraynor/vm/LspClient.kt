package me.jraynor.vm

import java.io.*
import java.net.Socket
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.*

class LspClient {
    private val `in`: BufferedReader
    private val socket: Socket = Socket("localhost", 8123)
    private val out: OutputStream get() = socket.getOutputStream()

    init {
        `in` = BufferedReader(
            InputStreamReader(socket.getInputStream())
        )
        // send initialize request
        // Example JSON-RPC object
        val jsonRpcObject = """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "initialize",
              "params": {
                "processId": null,
                "clientInfo": {
                  "name": "My Client",
                  "version": "1.0.0"
                },
                "locale": "en-US",
                "rootUri": null,
                "initializationOptions": {},
                "capabilities": {},
                "trace": "off",
                "workspaceFolders": null
              }
            }
            """

        writeMessageBytes(jsonRpcObject.toByteArray(StandardCharsets.UTF_8))
        didOpen()
    }

    fun didOpen() {
        val jsonRpcObject = """
            {
              "jsonrpc": "2.0",
              "method": "textDocument/didOpen",
              "params": {
                "textDocument": {
                  "uri": "file:///system.js",
                  "languageId": "javascript",
                  "version": 1,
                  "text": "$testJs"
                }
              }
            }
            """

// Format the complete message

        writeMessageBytes(jsonRpcObject.toByteArray(StandardCharsets.UTF_8))
    }

    private val testJs = """
        import {post, Events} from '/events.js';
        const ThreadClass = Java.type("java.lang.Thread")

        export const Log = {
        	/**
        	 * Clears the console, if lastOnly is true, we only clear the last element
        	 */
        	clear: function(lastOnly = false){
        		post(new Events.Clear(lastOnly))
        	},
        	/**
        	 * Prints out an info message to the log using a post event
        	 */
        	info: function(message){
        		post(new Events.Log(message, Events.Level.INFO, true, os.frame()))
        	},
        	/**
        	 * Prints out a warn message to the log using a post event
        	 */
        	warn: function(message){
        		post(new Events.Log(message, Events.Level.WARN, true, os.frame()))
        	},
        	/**
        	 * Prints out a debug message to the log using a post event
        	 */
        	debug: function(message){
        		post(new Events.Log(message, Events.Level.DEBUG,true, os.frame()))
        	},
        	/**
        	 * Prints out an error message to the log using a post event
        	 */
        	error: function(message){
        		post(new Events.Log(message, Events.Level.ERROR, true, os.frame()))
        	}
        }

        function junkProcess(duration) {
            const start = Date.now();
            while (Date.now() - start < duration) {
                // This loop will run for "duration" milliseconds
            
           
        }
    """.trimIndent()

    @Throws(IOException::class)
    private fun writeMessageBytes(messageBytes: ByteArray) {
        val contentLength = messageBytes.size
        val header = String.format(Locale.ENGLISH, "Content-Length: %d\r\n\r\n", contentLength)
        val headerBytes = header.toByteArray(StandardCharsets.US_ASCII)
        synchronized(out) {
            out.write(headerBytes)
            out.write(messageBytes)
            out.flush()
        }
    }

    /**
     * Processes incoming messages from the server
     */
    @Throws(Exception::class)
    private fun processMessages() {
        // All messages start with a header
        var header: String?
        while (`in`.readLine().also { header = it } != null) {
            // Read all the headers and extract the message content from them
            var contentLength = -1
            while (header != "") {
                println("Header: $header")
                if (isContentLengthHeader(header)) {
                    contentLength = getContentLength(header)
                }
                header = `in`.readLine()
            }

            println("Reading body")
            // Read the body
            if (contentLength == -1) {
                throw RuntimeException("Unexpected content length in message")
            }
            val messageChars = CharArray(contentLength)
            `in`.read(messageChars, 0, contentLength)
            println(messageChars)
        }
    }

    private fun isContentLengthHeader(header: String?): Boolean {
        return header?.toLowerCase()?.contains("content-length") == true
    }

    private fun getContentLength(header: String?): Int {
        return header?.split(" ")?.get(1)?.toInt() ?: 0
    }

    @Throws(Exception::class)
    private fun close() {
        `in`.close()
        out.close()
        socket.close()
    }


    }
