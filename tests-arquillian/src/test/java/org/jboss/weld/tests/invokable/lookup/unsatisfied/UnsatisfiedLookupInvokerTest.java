package org.jboss.weld.tests.invokable.lookup.unsatisfied;

import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.enterprise.inject.spi.Extension;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class UnsatisfiedLookupInvokerTest {

    @Deployment
    @ShouldThrowException(DeploymentException.class)
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(UnsatisfiedLookupInvokerTest.class))
                .addPackage(UnsatisfiedLookupInvokerTest.class.getPackage())
                .addAsServiceProvider(Extension.class, InvokerRegistreringExtension.class);
    }

    @Test
    public void testUnsatisfiedLookupWithQualifier() {
        // test should throw a deployment exception as the arg lookup for invoker is unsatisfied
    }
}
