package org.jboss.weld.tests.smoke;

import jakarta.enterprise.inject.Instance;

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
 * Smoke tests -- check anything unusual.
 *
 * @author Sam Corbet
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ExtendWith(ArquillianExtension.class)
public class SmokeTest {
    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SmokeTest.class))
                .addPackage(Crasher.class.getPackage());
    }

    @Test
    public void testInnerClass(Instance<Crasher> instance) {
        Crasher crasher = instance.get();
        Assertions.assertNotNull(crasher);
    }

}
