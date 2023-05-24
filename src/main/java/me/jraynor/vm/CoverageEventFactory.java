package me.jraynor.vm;

import com.oracle.truffle.api.instrumentation.EventContext;
import com.oracle.truffle.api.instrumentation.ExecutionEventNode;
import com.oracle.truffle.api.instrumentation.ExecutionEventNodeFactory;

/**
 * A factory for nodes that track coverage
 *
 * Because we
 * {@link CoverageInstrument#enable(com.oracle.truffle.api.instrumentation.TruffleInstrument.Env)
 * attached} an instance of this factory, each time a AST node of interest is created, it is
 * instrumented with a node created by this factory.
 */
final class CoverageEventFactory implements ExecutionEventNodeFactory {

    private CoverageInstrument simpleCoverageInstrument;

    CoverageEventFactory(CoverageInstrument simpleCoverageInstrument) {
        this.simpleCoverageInstrument = simpleCoverageInstrument;
    }

    /**
     * @param ec context of the event, used in our case to lookup the {@link SourceSection} that our
     *            node is instrumenting.
     * @return An {@link ExecutionEventNode}
     */
    public ExecutionEventNode create(final EventContext ec) {
        return new CoverageNode(simpleCoverageInstrument, ec.getInstrumentedSourceSection());
    }
}