package me.jraynor.os.vm

import me.jraynor.os.Events
import me.jraynor.os.OperatingSystem
import org.graalvm.polyglot.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future


class VirtualMachine(
    private val os: OperatingSystem,
    private val engine: Engine = Engine.newBuilder().allowExperimentalOptions(true).build(),
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

    fun executeAsync(source: Source): Future<Throwable> {
        return executor.submit(Callable { execute(source)!! })
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
        bindings.putMember("bus", Events)
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
