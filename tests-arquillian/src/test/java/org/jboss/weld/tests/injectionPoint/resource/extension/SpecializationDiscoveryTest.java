package org.jboss.weld.tests.injectionPoint.resource.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test inheritance of {@code @Resource} field when adding a specialized bean through discovery versus through extension
 * See https://issues.redhat.com/browse/WELD-2798
 */
@Tag("Integration")
@ExtendWith(ArquillianExtension.class)
public class SpecializationDiscoveryTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap
                .create(WebArchive.class,
                        Utils.getDeploymentNameAsHash(SpecializationDiscoveryTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addClasses(Foo.class, FooSpecialized.class, SpecializationDiscoveryTest.class)
                .addAsWebInfResource(SpecializationDiscoveryTest.class.getPackage(), "web.xml", "web.xml");
    }

    @Inject
    Foo foo;

    @Test
    public void test() {
        // foo is an instance of the specialized bean
        Assertions.assertTrue(foo instanceof FooSpecialized);
        // resource has been injected
        assertEquals("hello world", foo.value());
    }
}
