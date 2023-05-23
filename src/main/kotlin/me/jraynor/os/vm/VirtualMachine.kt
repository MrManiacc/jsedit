package me.jraynor.os.vm

import me.jraynor.os.OperatingSystem
import org.graalvm.polyglot.*
import org.graalvm.polyglot.proxy.ProxyExecutable
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class VirtualMachine(
    private val os: OperatingSystem,
    private val sandbox: Sandbox.Builder = Sandbox.Builder()
        .allow("java.util.*")
        .allow("java.lang.*")
        .allow("java.time.*")
        .allow("me.jraynor.os.*")
        .disallow("java.io.*")
        .disallow("java.net.*")
        .disallow("java.lang.reflect.*")
) {
    private val executor = Executors.newSingleThreadExecutor()

    private fun buildContext(): Context {
        val context = finalizeContext(Context.newBuilder("js"))
        configureEnvironment(context.getBindings("js"))
        return context
    }

    private fun finalizeContext(context: Context.Builder): Context {
        sandbox.build(context)
            .allowExperimentalOptions(true)
            .allowIO(true)
            .fileSystem(VmFileSystem(os))
            .allowHostAccess(HostAccess.ALL)
        return context.build()
    }


    private fun configureEnvironment(bindings: Value) {
        bindings.putMember("os", os)
        bindings.putMember("require", ProxyExecutable { args -> require(args[0].asString()) })
    }

    private fun require(path: String): Value {
        val context = buildContext()
        val file = os.disk.findFile(path)
        if (file == null) {
            context.close()
            throw RuntimeException("File not found: $path")
        }

        val result = context.eval(Source.newBuilder("js", String(file.content), "<require>").build())
        context.close()
        return result
    }

    fun execute(source: Source): Throwable? {
        val context = buildContext()
        try {
            context.eval(source)
        } catch (e: PolyglotException) {
            context.close()
            return e
        }
        context.close()
        return null
    }


}
