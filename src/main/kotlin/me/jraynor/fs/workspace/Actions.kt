package me.jraynor.fs.workspace

/**
 * Contains all the builders for the built-in actions.
 */
object Actions {

    /**
     * Initializes the server. This must be the first action sent to the server.
     */
    fun initialize(
        id: Int = 0,
        clientName: String = "jsedit",
        processId: Int? = null,
        clientVersion: String = "1.0",
        locale: String = "en-US",
        rootUri: String? = null,
        capabilities: Action.Builder.() -> Unit = {},
        trace: String = "off",
        workspaceFolders: String? = null
    ): Action =
        Action.of {
            string("jsonrpc", "2.0")
            string("method", "initialize")
            int("id", id)
            child("params", Action.of {
                if (processId == null) nil("processId")
                else int("processId", processId)
                child("clientInfo", Action.of {
                    string("name", clientName)
                    string("version", clientVersion)
                })
            })
            string("locale", locale)
            if (rootUri == null) nil("rootUri")
            else string("rootUri", rootUri)
            child("capabilities", Action.of(capabilities))
            string("trace", trace)
            if (workspaceFolders == null) nil("workspaceFolders")
            else string("workspaceFolders", workspaceFolders)
        }

    object TextDocument {
        fun didOpen(
            uri: String,
            languageId: String,
            version: Int,
            text: String
        ): Action =
            Action.of {
                string("jsonrpc", "2.0")
                string("method", "textDocument/didOpen")
                child("params", Action.of {
                    child("textDocument", Action.of {
                        string("uri", uri)
                        string("languageId", languageId)
                        int("version", version)
                        string("text", text)
                    })
                })
            }
    }

}