package org.jboss.weld.tests.extensions.lifecycle.processInjectionPoint.instanceInjectionPoint;

import static org.jboss.weld.tests.util.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.util.TypeLiteral;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProcessInjectionPointInstanceTest {

    @Deployment
    public static JavaArchive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ProcessInjectionPointInstanceTest.class))
                .addPackage(ProcessInjectionPointInstanceTest.class.getPackage())
                .addAsServiceProvider(Extension.class, MyExtension.class);
    }

    @Inject
    MyBean bean;

    @Inject
    MyExtension extension;

    @Test
    public void testInstanceInjectionPoint() {
        // assert initial state - all beans should exist and be injectable
        assertTrue(bean.getBaz() != null);
        assertTrue(bean.getBarInstance().isResolvable());
        assertTrue(bean.getFooInstance().isResolvable());

        // Baz - injected directly, no Instance<X> involved
        assertEquals(Baz.class, extension.getBazInjectionPoint().getType());

        // Foo, class-based bean, injected via Instance<Foo>, observed as PIP<MyBean,Foo>, NEVER INVOKED
        assertTrue(extension.getFooInjectionPoint() == null);
        // Foo, class-based bean, injected via Instance<Foo>, observed as PIP<MyBean,Instance<Foo>>
        assertEquals(new TypeLiteral<Instance<Foo>>() {}.getType(), extension.getInstanceFooInjectionPoint().getType());

        // Bar, synthetic bean, injected via Instance<Bar>, observed as PIP<MyBean,Bar>, NEVER INVOKED
        assertTrue(extension.getBarInjectionPoint() == null);
        // Bar, synthetic bean, injected via Instance<Bar>, observed as PIP<MyBean, Instance<Bar>>
        assertEquals(new TypeLiteral<Instance<Bar>>() {}.getType(), extension.getInstanceBarInjectionPoint().getType());
    }
}
