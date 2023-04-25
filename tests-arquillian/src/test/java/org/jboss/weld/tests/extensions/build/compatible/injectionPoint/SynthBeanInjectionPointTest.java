package org.jboss.weld.tests.extensions.build.compatible.injectionPoint;

import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SynthBeanInjectionPointTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SynthBeanInjectionPointTest.class))
                .addPackage(SynthBeanInjectionPointTest.class.getPackage())
                .addAsServiceProvider(BuildCompatibleExtension.class, SynthBeanBuildCompatibleExtension.class);
    }

    @Inject
    DummyBean bean1;

    @Inject
    DummyBean2 bean2;

    @Test
    public void testSynthBeanInjectionPoint() {
        Assert.assertEquals("foo", bean1.getSynthStringBean());
        Assert.assertEquals("bar", bean2.getSynthStringBean());
    }
}
