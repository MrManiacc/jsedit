package me.jraynor.services

/**
 * A workspace is a central wrapper around communication between the LSP server and the client.
 *
 * IT is responsible for handling all the messages that are sent to the server and then providing a means to
 * delegate responses back to the client. It is a high level wrapper around the LSP protocol.
 */
class Workspace {
    private val textDocumentService: TextDocumentService = TextDocumentService()

}