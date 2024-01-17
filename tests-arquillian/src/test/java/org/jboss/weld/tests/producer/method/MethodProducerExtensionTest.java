package org.jboss.weld.tests.producer.method;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Extension;
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
public class MethodProducerExtensionTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(MethodProducerExtensionTest.class))
                .addPackage(MethodProducerExtensionTest.class.getPackage())
                .addAsServiceProvider(Extension.class, MyExtension.class);
    }

    @Inject
    Instance<Object> instance;

    @Inject
    String s;

    @Test
    public void test() {
        // assert extension worked
        Assert.assertEquals(true, MyExtension.extensionTriggered);
        // assert producer works
        Assert.assertEquals("42", s);

        // assert both beans are there
        List<? extends Instance.Handle<MyProducer>> collect = instance.select(MyProducer.class).handlesStream()
                .collect(Collectors.toList());
        Assert.assertEquals(2, collect.size());
    }
}
