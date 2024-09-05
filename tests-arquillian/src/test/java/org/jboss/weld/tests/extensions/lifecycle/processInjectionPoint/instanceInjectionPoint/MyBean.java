package org.jboss.weld.tests.extensions.lifecycle.processInjectionPoint.instanceInjectionPoint;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class MyBean {

    @Inject
    Instance<Foo> fooInstance;

    @Inject
    Instance<Bar> barInstance;

    @Inject
    Baz baz;


    public Instance<Foo> getFooInstance() {
        return fooInstance;
    }

    public Instance<Bar> getBarInstance() {
        return barInstance;
    }

    public Baz getBaz() {
        return baz;
    }
}
