package org.jboss.weld.tests.proxy.superclass.instance;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Yann Diorcet
 * @see https://issues.jboss.org/browse/WELD-1834
 */
@ExtendWith(ArquillianExtension.class)
public class Weld1834Test {

    @Inject
    private Instance<Foo> instance;

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(Weld1834Test.class))
                .addPackage(Weld1834Test.class.getPackage());
    }

    @Test
    public void testDeployment() {
        for (Foo foo : instance) {
            foo.getName();
        }
    }

}
