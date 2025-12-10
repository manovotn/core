package org.jboss.weld.tests.proxy.superclass.named;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Yann Diorcet
 * @see https://issues.jboss.org/browse/WELD-1827
 */
@ExtendWith(ArquillianExtension.class)
public class Weld1827Test {

    @Inject
    private Bar bar;

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(Weld1827Test.class))
                .addPackage(Weld1827Test.class.getPackage());
    }

    @Test
    public void testDeployment() {
        Assertions.assertEquals("Bar", bar.getName());
    }

}
