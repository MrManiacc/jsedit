package me.jraynor.services.rpc

abstract class JsonRPC(
    val jsonrpc: String,
    val id: Int,
    val method: String,
    val params: Map<String, Any>
) {
}