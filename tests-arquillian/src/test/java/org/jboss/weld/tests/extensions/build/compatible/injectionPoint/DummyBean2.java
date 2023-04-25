package org.jboss.weld.tests.extensions.build.compatible.injectionPoint;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DummyBean2 {

    @Inject
    String synthStringBean;

    public String getSynthStringBean() {
        return synthStringBean;
    }
}
