package org.jboss.weld.tests.interceptors.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * author Marko Luksa
 */
@ExtendWith(ArquillianExtension.class)
public class InterceptorExceptionWrappingTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(InterceptorExceptionWrappingTest.class))
                .intercept(MyInterceptor.class)
                .addPackage(InterceptorExceptionWrappingTest.class.getPackage());
    }

    @Test
    public void testCheckedExceptionIsNotWrapped(Foo foo) {
        assertThrows(FooCheckedException.class, () -> foo.throwCheckedException());
    }

    @Test
    public void testUncheckedExceptionIsNotWrapped(Foo foo) {
        assertThrows(FooUncheckedException.class, () -> foo.throwUncheckedException());
    }
}
