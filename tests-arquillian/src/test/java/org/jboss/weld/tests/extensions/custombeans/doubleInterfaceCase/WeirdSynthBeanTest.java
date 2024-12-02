package org.jboss.weld.tests.extensions.custombeans.doubleInterfaceCase;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WeirdSynthBeanTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap
                .create(WebArchive.class, Utils.getDeploymentNameAsHash(WeirdSynthBeanTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addPackage(WeirdSynthBeanTest.class.getPackage())
                .addAsServiceProvider(Extension.class, SynthBeanExtension.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    DummyBean dummy;

    @Inject
    Instance<Object> instance;

    @Test
    public void testAddedBean() {
        // Dummy test to verify deployment is working
        Assert.assertEquals(DummyBean.class.getSimpleName(), dummy.ping());

        // Test that synth bean is instance of Foo and as such can be invoked
        Instance<Foo> fooInstance = instance.select(Foo.class);
        Assert.assertTrue(fooInstance.isResolvable());
        Assert.assertEquals("synth", fooInstance.get().ping());

        // Test that synth bean is instance of Bar and as such can be invoked
        Instance<Bar> barInstance = instance.select(Bar.class);
        Assert.assertTrue(barInstance.isResolvable());
        // Following call will fail the test - the bean cannot be cast to Bar
        Assert.assertEquals("synth", barInstance.get().pong());

    }
}
