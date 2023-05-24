package me.jraynor.vm;

import com.oracle.truffle.api.instrumentation.LoadSourceSectionEvent;
import com.oracle.truffle.api.instrumentation.LoadSourceSectionListener;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A listener for new {@link SourceSection}s being loaded.
 *
 * Because we
 * attached} an instance of this listener, each time a new {@link SourceSection} of interest is
 * loaded, we are notified in the
 * {@link #onLoad(com.oracle.truffle.api.instrumentation.LoadSourceSectionEvent) } method.
 */
final class GatherSourceSectionsListener implements LoadSourceSectionListener {

    private final CoverageInstrument instrument;

    GatherSourceSectionsListener(CoverageInstrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Notification that a new {@link LoadSourceSectionEvent} has occurred.
     *
     * @param event information about the event. We use this information to keep our
     */
    @Override
    public void onLoad(LoadSourceSectionEvent event) {
        final SourceSection sourceSection = event.getSourceSection();
        instrument.addLoaded(sourceSection);
    }
}