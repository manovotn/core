package org.jboss.weld.tests.producer.method;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@Dependent
public class MyProducer {

    @Produces
    String p = "42";
}
