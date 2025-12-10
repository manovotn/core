package org.jboss.weld.tests.interceptors.weld1019;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 */
@ExtendWith(ArquillianExtension.class)
public class Weld1019Test {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap
                .create(BeanArchive.class, Utils.getDeploymentNameAsHash(Weld1019Test.class))
                .intercept(UppercasingInterceptor.class)
                .addPackage(Weld1019Test.class.getPackage())
                .addAsServiceProvider(Extension.class, MyScopeExtension.class);
    }

    @Test
    public void testInterceptorInvoked(HelloBean helloBean, BeanManager beanManager) {
        assertEquals("HELLO WORLD", helloBean.getMessage());
    }

}
