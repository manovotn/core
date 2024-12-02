package org.jboss.weld.tests.extensions.custombeans.doubleInterfaceCase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;

public class SynthBeanExtension implements Extension {

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
        event.addBean()
                .addTypes(Foo.class, Bar.class)
                .produceWith(instance -> (Foo) () -> "synth")
                .scope(ApplicationScoped.class);
    }
}
