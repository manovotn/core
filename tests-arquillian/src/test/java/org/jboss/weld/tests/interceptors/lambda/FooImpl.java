package org.jboss.weld.tests.interceptors.lambda;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@MyBinding
public class FooImpl extends FooBase {

    public String ping() {
        return FooImpl.class.getSimpleName();
    }

    public void invokeLambda() {
        // invokes lambda - this will compile into a method on this class which we want to avoid intercepting
        log(() -> "convert()");
        // do something else....
        ping();
    }

}
