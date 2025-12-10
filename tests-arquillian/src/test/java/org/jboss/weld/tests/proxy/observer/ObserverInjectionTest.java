package org.jboss.weld.tests.proxy.observer;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class ObserverInjectionTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ObserverInjectionTest.class))
                .addPackage(ObserverInjectionTest.class.getPackage());
    }

    @Inject
    private BeanManagerImpl beanManager;

    /*
     * description = "WELD-535"
     */
    @Test
    public void testInjectionHappens(SampleObserver sampleObserver) {
        Assertions.assertFalse(sampleObserver.isInjectionAndObservationOccured());
        beanManager.getEvent().select(Baz.class).fire(new Baz());
        Assertions.assertTrue(sampleObserver.isInjectionAndObservationOccured());
    }

}
