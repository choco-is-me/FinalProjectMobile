package com.moh.alarmclock.Clock.ClockSugestions;

import java.util.Comparator;

public class SuggestionComparator implements Comparator<PrioritySuggestion> {
    @Override
    public int compare(PrioritySuggestion t, PrioritySuggestion t1) {
        return Float.compare(t.getPriority(),t1.getPriority());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
