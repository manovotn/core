package org.jboss.weld.tests.proxy.superclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class SuperclassWithConstructorWithArgumentsTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap
                .create(BeanArchive.class, Utils.getDeploymentNameAsHash(SuperclassWithConstructorWithArgumentsTest.class))
                .addPackage(SuperclassWithConstructorWithArgumentsTest.class.getPackage());
    }

    @Inject
    private SimpleBean bean;

    @Test
    public void testSuperClassWithoutSimpleConstructor() {
        // tests deployability of this scenario
        // SimpleBean superclass has a constructor with args
        assertEquals("nothing", bean.giveMeNothing());
    }
}
