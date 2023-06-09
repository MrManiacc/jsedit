package me.jraynor.vm;

import java.util.HashSet;
import java.util.Set;

import com.oracle.truffle.api.source.SourceSection;

/**
 * Contains per {@link com.oracle.truffle.api.source.Source} coverage by keeping track of loaded and
 * covered {@link com.oracle.truffle.api.source.SourceSection}s.
 */
public final class Coverage {
    private final Set<SourceSection> loaded = new HashSet<>();
    private final Set<SourceSection> covered = new HashSet<>();

    void addCovered(SourceSection sourceSection) {
        covered.add(sourceSection);
    }

    void addLoaded(SourceSection sourceSection) {
        loaded.add(sourceSection);
    }

    private Set<SourceSection> nonCoveredSections() {
        final HashSet<SourceSection> nonCovered = new HashSet<>();
        nonCovered.addAll(loaded);
        nonCovered.removeAll(covered);
        return nonCovered;
    }

    Set<Integer> nonCoveredLineNumbers() {
        Set<Integer> linesNotCovered = new HashSet<>();
        for (SourceSection ss : nonCoveredSections()) {
            for (int i = ss.getStartLine(); i <= ss.getEndLine(); i++) {
                linesNotCovered.add(i);
            }
        }
        return linesNotCovered;
    }

    Set<Integer> loadedLineNumbers() {
        Set<Integer> loadedLines = new HashSet<>();
        for (SourceSection ss : loaded) {
            for (int i = ss.getStartLine(); i <= ss.getEndLine(); i++) {
                loadedLines.add(i);
            }
        }
        return loadedLines;
    }
}