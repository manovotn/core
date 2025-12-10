package org.jboss.weld.tests.annotatedType.superclass;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
 * @author Ales Justin
 */
@ExtendWith(ArquillianExtension.class)
public class SuperclassModifiedTest {

    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SuperclassModifiedTest.class))
                .addPackage(Child.class.getPackage())
                .addAsServiceProvider(Extension.class, ModifyExtension.class);
    }

    @Test
    public void shouldNotInjectSuperclassFields(Child child) {
        assertNotNull(child, "Should resolve Child");
        assertNull(child.getFoo(), "Should not have Foo injected");
    }
}
