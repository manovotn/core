package org.jboss.weld.tests.classDefining.unimplemented.interfaces;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.classDefining.unimplemented.interfaces.ifaces.BeanIface;
import org.jboss.weld.tests.classDefining.unimplemented.interfaces.ifaces.NotImplementedIface;
import org.jboss.weld.tests.classDefining.unimplemented.interfaces.impl.CDIBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class ProxyCreationForCdiBeanWithUnimplementedInterfaceTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ProxyCreationForEjbLocalTest.class))
                .addClasses(MyExtension.class, ProxyCreationForCdiBeanWithUnimplementedInterfaceTest.class, CDIBean.class,
                        NotImplementedIface.class, BeanIface.class)
                .addAsServiceProvider(Extension.class, MyExtension.class);
    }

    @Inject
    NotImplementedIface cdiBean;

    @Test
    public void testProxyPackageMatchesTheClass() {
        // sanity check of the testing setup
        Assertions.assertEquals(NotImplementedIface.class.getSimpleName(), cdiBean.ping3());

        // The assertion is based solely on inspecting the proxy format - expected package and first mentioned class
        // We cannot rely on verifying that the class can be defined because if this runs on WFLY, it is a non-issue
        // due to using ClassLoader#defineClass. The mismatch only shows when using MethodHandles.Lookup
        // see https://github.com/jakartaee/platform-tck/issues/1194 for more information
        Assertions.assertEquals(CDIBean.class.getPackage(), cdiBean.getClass().getPackage());
        Assertions.assertTrue(cdiBean.getClass().getName().startsWith(CDIBean.class.getName()));
    }
}
