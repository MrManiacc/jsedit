package me.jraynor.vm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.*;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;

/**
 * Instrument for registered for the debugger. Do not use directly.
 *
 * @since 0.17
 */

@TruffleInstrument.Registration(id = CallInstrument.ID, services = CallInstrument.class)
public class CallInstrument extends TruffleInstrument {

    @Override
    public void onCreate(TruffleInstrument.Env env) {
        env.getInstrumenter().attachExecutionEventListener(
                SourceSectionFilter.newBuilder().tagIs(StandardTags.CallTag.class).build(), new CallListener()
        );
        env.registerService(this);
    }

    static class CallListener implements ExecutionEventListener {
        @Override
        public void onEnter(EventContext context, VirtualFrame frame) {
        }

        @Override
        public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
            System.out.println("Entering frame: " + frame.toString());
        }

        @Override
        public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
            System.out.println("Entering frame: " + frame.toString());

        }
    }

    public static final String ID = "CallTracer";
}