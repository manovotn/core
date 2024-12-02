package org.jboss.weld.tests.extensions.custombeans.doubleInterfaceCase;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DummyBean {

    public String ping() {
        return DummyBean.class.getSimpleName();
    }
}
