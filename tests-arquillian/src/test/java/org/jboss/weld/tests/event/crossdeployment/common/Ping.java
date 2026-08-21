package org.jboss.weld.tests.event.crossdeployment.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A plain (non-bean) event type shared by two web modules.
 * <p>
 * It carries a mutable set into which every notified observer records its origin. Because synchronous event delivery is
 * finished by the time {@code Event#fire(Object)} returns, the firing side can inspect this set to see exactly which
 * observers - and therefore which bean archives - were reached.
 */
public class Ping {

    private final Set<String> visitedBy = Collections.synchronizedSet(new HashSet<String>());

    public void recordVisit(String name) {
        visitedBy.add(name);
    }

    public Set<String> getVisitedBy() {
        return visitedBy;
    }
}
