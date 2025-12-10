package org.jboss.weld.tests.classDefining.packPrivate;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.classDefining.packPrivate.api.Alpha;
import org.jboss.weld.tests.classDefining.packPrivate.interceptor.MyInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * See WELD-2758
 */
@ExtendWith(ArquillianExtension.class)
public class ProxyForInterceptedPackagePrivateBeanTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap
                .create(BeanArchive.class, Utils.getDeploymentNameAsHash(ProxyForInterceptedPackagePrivateBeanTest.class))
                .addPackages(true, ProxyForInterceptedPackagePrivateBeanTest.class.getPackage());
    }

    @Inject
    Instance<Object> instance;

    @Test
    public void testProxyCanBeCreated() {
        Instance<Alpha> select = instance.select(Alpha.class);
        Assertions.assertTrue(select.isResolvable());
        Assertions.assertEquals(MyInterceptor.class.getSimpleName() + Alpha.class.getSimpleName(), select.get().ping());
    }
}
