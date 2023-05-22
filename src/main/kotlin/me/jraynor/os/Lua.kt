package me.jraynor.os

object Lua {
    fun print(message: Runnable) {
        val thread = Thread({
            println("testing")

        })
        message.run()
        thread.start()
//        println("Hello from Lua")
    }


}