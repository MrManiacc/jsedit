package me.jraynor.os

import me.jraynor.os.disk.Disk
import me.jraynor.os.disk.File
import me.jraynor.os.vm.parser.LuaBaseListener
import me.jraynor.os.vm.parser.LuaLexer
import me.jraynor.os.vm.parser.LuaParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.tree.ParseTreeWalker
import party.iroiro.luajava.ClassPathLoader
import party.iroiro.luajava.ExternalLoader
import party.iroiro.luajava.JFunction
import party.iroiro.luajava.Lua
import party.iroiro.luajava.luajit.LuaJit
import java.io.ByteArrayOutputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicInteger


class VirtualMachine(private val os: OperatingSystem) {
    private val externalLoader = LuaLoader(os.disk)


    private fun parseForErrors(source: String): Map<Int, String> {
        val lexer = LuaLexer(CharStreams.fromString(source))
        val lineCount = source.count { it == '\n' }
        val firstLineNotEmpty = source.lines().indexOfFirst { it.trim().isNotEmpty() } + 1
        lexer.removeErrorListeners()
        val parser = LuaParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.errorHandler = DefaultErrorStrategy()
        val errors = HashMap<Int, String>()
        parser.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?, offendingSymbol: Any,
                line: Int, charPositionInLine: Int, msg: String, e: RecognitionException?
            ) {
                if (line > lineCount) {
                    errors[firstLineNotEmpty] = msg
                } else
                    errors[line] = msg
            }
        })
        ParseTreeWalker().walk(object : LuaBaseListener() {
            val brackets: AtomicInteger = AtomicInteger(0);

            override fun enterEveryRule(ctx: ParserRuleContext) {
                super.enterEveryRule(ctx);
                brackets.incrementAndGet();
            }

            override fun exitEveryRule(ctx: ParserRuleContext) {
                super.exitEveryRule(ctx);
                if (ctx.exception == null) {
                    brackets.decrementAndGet();
                }
            }
        }, parser.chunk())
        return errors

    }

    fun execute(source: String): Map<Int, String> {
        javaClass
        val vm = LuaJit().apply {
            openLibraries()
            setExternalLoader(externalLoader)
            pushJavaObject(os)
            setGlobal("os")
            run("sys = require('sys')")
        }


        val parsed = parseForErrors(source).toMutableMap()
        if (parsed.isEmpty()) {
            if (vm.run(source) != Lua.LuaError.OK) {
                val error = vm.get().toJavaObject() as String
                parsed[1] = "Runtime error: $error"
            }
        }
        vm.close()
        return parsed
    }

    fun execute(file: File) = execute(String(file.content))

    private class LuaLoader(private val disk: Disk) : ExternalLoader {
        private val fallback = ClassPathLoader()
        override fun load(module: String, L: Lua): Buffer? {
            var path = module.replace(".", "/")
            path = if (path.startsWith("/")) path.substring(1) else path
            path = if (path.endsWith(".lua")) path else "$path.lua"
            val file = disk.findFile(path)
            if (file != null) {
                return load(file.content)
            }
            return fallback.load(module, L)
        }

        private fun load(inputByteArray: ByteArray): Buffer? {
            return try {
                val output = ByteArrayOutputStream()
                val buffer = ByteBuffer.allocateDirect(inputByteArray.size)
                output.write(inputByteArray)
                output.writeTo(ClassPathLoader.BufferOutputStream(buffer))
                buffer.flip()
                buffer
            } catch (e: Exception) {
                null
            }
        }
    }

    fun shutdown() {
    }
}
