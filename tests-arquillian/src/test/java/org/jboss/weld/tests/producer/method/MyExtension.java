package org.jboss.weld.tests.producer.method;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;

public class MyExtension implements Extension {

    public static boolean extensionTriggered = false;

    public <T> void observe(@Observes ProcessAnnotatedType<T> pat) {
        extensionTriggered = true; // will trigger multiple times, but that shouldn't matter here
        pat.configureAnnotatedType();
    }
}
