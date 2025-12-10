package org.jboss.weld.tests.annotatedType.weld1144;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Richard Kennard
 * @author Marko Luksa
 */
@ExtendWith(ArquillianExtension.class)
public class Weld1144Test {

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(Weld1144Test.class))
                .addPackage(Weld1144Test.class.getPackage())
                .addAsServiceProvider(Extension.class, CdiExtension.class);
    }

    @Inject
    private Instance<CdiTest2> test;

    @Test
    public void testChildClassFieldIsInjected() {
        CdiTest2 cdiTest2 = test.get();
        assertNotNull(cdiTest2.getSomeInjectedBean2());
    }

    @Test
    public void testSuperclassFieldIsInjected() {
        CdiTest2 cdiTest2 = test.get();
        assertNotNull(cdiTest2.getSomeInjectedBean1());
    }

    @Test
    public void testInitializers(@Original Foo foo, Bar bar) {
        assertTrue(foo.isInitCalled());
        assertFalse(bar.isInitCalled()); // because the initializer is overriden without @Inject
        assertFalse(bar.isSubclassInitCalled());
    }
}
