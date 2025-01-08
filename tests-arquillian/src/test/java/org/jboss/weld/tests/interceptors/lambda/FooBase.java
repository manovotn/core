package org.jboss.weld.tests.interceptors.lambda;

import java.util.function.Supplier;

public class FooBase {

    final Object userBean = new Object();

    public void test() {
        // perform something more complex than just returning string
        // this causes the lambda here to generate as private NON-STATIC method
        log(() -> "test(" + userBean.toString() + ")");
    }

    public static void log(Supplier<String> msg) {
        System.out.println(msg.get());
    }
}
