package me.jraynor.services

import me.jraynor.services.rpc.JsonRPC
import me.jraynor.services.rpc.Position
import me.jraynor.services.rpc.TextDocument
import java.net.URI

class TextDocumentService {

    fun completion(uri: String, position: Position, id: Int = 1) {
        val completion = Completion.of(uri, position, id)
        println(completion)
    }

    fun didOpen(s: String, testJs: URI) {
    }

    class DidOpen(s: String, contents: String) : JsonRPC(
        "2.0", 1, "textDocument/didOpen", mapOf(
            "textDocument" to TextDocument(contents.toString()), "languageId" to "javascript", "version" to 1, "text" to s
        )
    )


    private class Completion(
        jsonrpc: String, id: Int, method: String = "textDocument/completion", params: Map<String, Any>
    ) : JsonRPC(jsonrpc, id, method, params) {

        companion object {
            fun of(uri: String, position: Position, id: Int = 1): Completion {
                val params = mapOf(
                    "textDocument" to TextDocument(uri), "position" to position
                )
                return Completion("2.0", id, params = params)
            }
        }
    }

    private class Hover(
        jsonrpc: String, id: Int, method: String = "textDocument/hover", params: Map<String, Any>
    ) : JsonRPC(jsonrpc, id, method, params) {

        companion object {
            fun of(uri: String, position: Position, id: Int = 1): Hover {
                val params = mapOf(
                    "textDocument" to TextDocument(uri), "position" to position
                )
                return Hover("2.0", id, params = params)
            }
        }
    }

    private class SignatureHelp(
        jsonrpc: String, id: Int, method: String = "textDocument/signatureHelp", params: Map<String, Any>
    ) : JsonRPC(jsonrpc, id, method, params) {

        companion object {
            fun of(uri: String, position: Position, id: Int = 1): SignatureHelp {
                val params = mapOf(
                    "textDocument" to TextDocument(uri), "position" to position
                )
                return SignatureHelp("2.0", id, params = params)
            }
        }
    }
}