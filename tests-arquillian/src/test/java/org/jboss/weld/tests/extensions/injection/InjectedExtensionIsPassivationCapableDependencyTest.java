package org.jboss.weld.tests.extensions.injection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.enterprise.inject.spi.Extension;
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
 *
 */
@ExtendWith(ArquillianExtension.class)
public class InjectedExtensionIsPassivationCapableDependencyTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap
                .create(BeanArchive.class,
                        Utils.getDeploymentNameAsHash(InjectedExtensionIsPassivationCapableDependencyTest.class))
                .addClass(Utils.class)
                .addPackage(InjectedExtensionIsPassivationCapableDependencyTest.class.getPackage())
                .addAsServiceProvider(Extension.class, MyExtension.class);
    }

    @Inject
    private Client client;

    @Test
    public void testInjectedExtensionIsPassivationCapableDependency() throws Exception {
        assertNotNull(client.getMyExtension());

        Assertions.assertDoesNotThrow(() -> {
            Utils.serialize(client);
        }, "Expected Client to be serializable, but it was not: ");
    }
}
