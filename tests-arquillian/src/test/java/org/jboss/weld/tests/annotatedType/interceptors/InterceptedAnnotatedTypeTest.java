package org.jboss.weld.tests.annotatedType.interceptors;

import jakarta.enterprise.inject.Default;
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

@ExtendWith(ArquillianExtension.class)
public class InterceptedAnnotatedTypeTest {

    @Inject
    @Default
    private Box defaultBox;

    @Inject
    @Additional
    private Box additionalBox;

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(InterceptedAnnotatedTypeTest.class))
                .intercept(BoxInterceptor.class).addPackage(InterceptedAnnotatedTypeTest.class.getPackage())
                .addAsServiceProvider(Extension.class, SetupExtension.class)
                .addClass(Utils.class);
    }

    @Test
    public void test() throws Exception {
        Assertions.assertTrue(defaultBox.isIntercepted());
        Assertions.assertFalse(additionalBox.isIntercepted());

        // test after deserialization

        Assertions.assertTrue(Utils.<Box> deserialize(Utils.serialize(defaultBox)).isIntercepted());
        Assertions.assertFalse(Utils.<Box> deserialize(Utils.serialize(additionalBox)).isIntercepted());
    }
}
