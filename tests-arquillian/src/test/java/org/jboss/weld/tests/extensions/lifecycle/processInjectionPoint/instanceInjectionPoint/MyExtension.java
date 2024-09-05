package org.jboss.weld.tests.extensions.lifecycle.processInjectionPoint.instanceInjectionPoint;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;

public class MyExtension implements Extension {

    private InjectionPoint fooInjectionPoint;
    private InjectionPoint barInjectionPoint;
    private InjectionPoint bazInjectionPoint;
    private InjectionPoint instanceBarInjectionPoint;
    private InjectionPoint instanceFooInjectionPoint;

    public void createSynthBar(@Observes AfterBeanDiscovery abd) {
        abd.addBean()
                .produceWith(instance -> new Bar())
                .addTransitiveTypeClosure(Bar.class)
                .id(Bar.class.getSimpleName() + ".synth");
    }

    public void processInjectionPointFoo(@Observes ProcessInjectionPoint<MyBean, Foo> pip) {
        if (fooInjectionPoint == null) {
            this.fooInjectionPoint = pip.getInjectionPoint();
        } else {
            throw new IllegalStateException("In this test there should be only a single IP for this type");
        }
    }

    public void processInjectionPointBar(@Observes ProcessInjectionPoint<MyBean, Bar> pip) {
        if (barInjectionPoint == null) {
            this.barInjectionPoint = pip.getInjectionPoint();
        } else {
            throw new IllegalStateException("In this test there should be only a single IP for this type");
        }
    }

    public void processInjectionPointBaz(@Observes ProcessInjectionPoint<MyBean, Baz> pip) {
        if (bazInjectionPoint == null) {
            this.bazInjectionPoint = pip.getInjectionPoint();
        } else {
            throw new IllegalStateException("In this test there should be only a single IP for this type");
        }
    }

    public void processInjectionPointInstanceBar(@Observes ProcessInjectionPoint<MyBean, Instance<Bar>> pip) {
        if (instanceBarInjectionPoint == null) {
            this.instanceBarInjectionPoint = pip.getInjectionPoint();
        } else {
            throw new IllegalStateException("In this test there should be only a single IP for this type");
        }
    }

    public void processInjectionPointInstanceFoo(@Observes ProcessInjectionPoint<MyBean, Instance<Foo>> pip) {
        if (instanceFooInjectionPoint == null) {
            this.instanceFooInjectionPoint = pip.getInjectionPoint();
        } else {
            throw new IllegalStateException("In this test there should be only a single IP for this type");
        }
    }

    public InjectionPoint getFooInjectionPoint() {
        return fooInjectionPoint;
    }

    public InjectionPoint getBarInjectionPoint() {
        return barInjectionPoint;
    }

    public InjectionPoint getBazInjectionPoint() {
        return bazInjectionPoint;
    }

    public InjectionPoint getInstanceBarInjectionPoint() {
        return instanceBarInjectionPoint;
    }

    public InjectionPoint getInstanceFooInjectionPoint() {
        return instanceFooInjectionPoint;
    }
}
