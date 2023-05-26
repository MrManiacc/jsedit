package me.jraynor.vm

import me.jraynor.os.Events
import me.jraynor.os.OperatingSystem
import org.graalvm.polyglot.*
import me.jraynor.gui.helpers.ConsoleStream


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
    private val engine: Engine = Engine.newBuilder()
//        .option(CoverageInstrument.ID, "true").option(CoverageInstrument.ID + ".PrintCoverage", "true")
//        .option("lsp", "true")
        .allowExperimentalOptions(true)
        .build()
    init{
//        val instrument = engine.instruments["lsp"]
//        val envProvider = instrument!!.lookup(EnvironmentProvider::class.java)
    }
    private fun buildContext(): Context {
        val context = finalizeContext(Context.newBuilder("js"))
        configureEnvironment(context.getBindings("js"))
        return context
    }


    private fun finalizeContext(context: Context.Builder): Context {
//        sandbox.build(context)
//            .allowExperimentalOptions(true)
        context.engine(engine)
            .allowIO(true)
            .fileSystem(VmFileSystem(os))
            .allowAllAccess(true)

//            .allowHostAccess(HostAccess.ALL)
        return context.build()
    }


    private fun configureEnvironment(bindings: Value) {
        bindings.putMember("os", os)
        bindings.putMember("bus", Events)
        bindings.putMember("out", ConsoleStream)
    }


    fun execute(source: Source): Throwable? {
        val context = buildContext()

        try {
            context.eval(source)
//            val coverageInstrument: CoverageInstrument = context.engine.instruments[CoverageInstrument.ID]!!
//                .lookup(
//                    CoverageInstrument::class.java
//                )
            // We then use the looked up service to assert that it behaves as expected, just like in any
            // other test.
            // We then use the looked up service to assert that it behaves as expected, just like in any
            // other test.
//            val coverageMap: Map<com.oracle.truffle.api.source.Source, Coverage> = coverageInstrument.getCoverageMap()
//            for (file in coverageMap.keys) {
//                val coverage = coverageMap[file]!!
//                for (line in 1 until file.lineCount) {
//                    if (coverage.loadedLineNumbers().contains(line))
//                        println("+ ${file.createSection(line).characters}")
//                    else
//                        println("- ${file.createSection(line).characters}")
//                }
//            }
        } catch (e: PolyglotException) {
            context.close()
            return e
        }
        return null
    }


}
