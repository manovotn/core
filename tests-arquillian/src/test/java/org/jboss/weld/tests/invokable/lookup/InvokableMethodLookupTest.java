package org.jboss.weld.tests.invokable.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@ExtendWith(ArquillianExtension.class)
public class InvokableMethodLookupTest {

    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(InvokableMethodLookupTest.class))
                .addPackage(InvokableMethodLookupTest.class.getPackage())
                .addAsServiceProvider(Extension.class, InvokerRegistreringExtension.class);
    }

    @Inject
    InvokerRegistreringExtension extension;

    @Inject
    @MyQualifier1("abc")
    InvokableBean bean;

    @Test
    public void testInstanceLookupWithQualifiers() throws Exception {
        Object invokerResult = extension.getInstanceLookupInvoker().invoke(null, new Object[] {});
        assertTrue(invokerResult instanceof String);
        assertEquals(InvokableBean.class.getSimpleName(), invokerResult);
    }

    @Test
    public void testCorrectArgLookupWithQualifiers() throws Exception {
        Object invokerResult = extension.getCorrectLookupInvoker().invoke(bean, new Object[] { null, null });
        assertTrue(invokerResult instanceof String);
        assertEquals(
                MyQualifier1.class.getSimpleName() + MyQualifier4.class.getSimpleName() + MyQualifier2.class.getSimpleName(),
                invokerResult);
    }

    @Test
    public void testLookupWithRegisteredQualifier() throws Exception {
        Object invokerResult = extension.getLookupWithRegisteredQualifier().invoke(bean, new Object[] { null });
        assertTrue(invokerResult instanceof String);
        assertEquals(ToBeQualifier.class.getSimpleName(), invokerResult);
    }
}
