package org.jboss.weld.tests.event.crossdeployment.common;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Application scoped synchronous observer that lives in the second web module.
 */
@ApplicationScoped
public class War2Observer {

    void observe(@Observes Ping ping) {
        ping.recordVisit("war2");
    }
}
