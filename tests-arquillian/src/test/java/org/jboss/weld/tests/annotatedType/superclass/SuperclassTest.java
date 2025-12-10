package org.jboss.weld.tests.annotatedType.superclass;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
 * @author Gert Palok
 * @author Ales Justin
 */
@ExtendWith(ArquillianExtension.class)
public class SuperclassTest {

    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SuperclassTest.class))
                .addPackage(Child.class.getPackage())
                .addAsServiceProvider(Extension.class, TestExtension.class);
    }

    @Test
    public void shouldInjectSuperclassFields(Child child) {
        assertNotNull(child, "Should resolve Child");
        assertNotNull(child.getFoo(), "Should have Foo injected");
    }
}
