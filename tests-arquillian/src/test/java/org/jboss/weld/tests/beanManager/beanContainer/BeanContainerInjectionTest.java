package org.jboss.weld.tests.beanManager.beanContainer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.BeanContainer;
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
 * Simple test which verifies that we can provide {@link BeanContainer} as a built-in bean
 */
@ExtendWith(ArquillianExtension.class)
public class BeanContainerInjectionTest {

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(BeanContainerInjectionTest.class))
                .addPackage(BeanContainerInjectionTest.class.getPackage());
    }

    @Inject
    MyBean myBean;

    @Inject
    Instance<Object> instance;

    @Test
    public void testInjectionOfBeanContainerType() {
        // bean injection; use the container for arbitrary action
        myBean.getBeanContainer().isNormalScope(ApplicationScoped.class);

        // dynamic resolution, verify type, explicit qualifier and test scope
        Instance<BeanContainer> beanContainerInstance = instance.select(BeanContainer.class, Default.Literal.INSTANCE);
        Assertions.assertTrue(beanContainerInstance.isResolvable());
        Assertions.assertEquals(Dependent.class, beanContainerInstance.getHandle().getBean().getScope());
    }
}
