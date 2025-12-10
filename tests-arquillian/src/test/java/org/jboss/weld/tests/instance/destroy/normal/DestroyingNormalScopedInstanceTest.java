/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.instance.destroy.normal;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.enterprise.context.spi.AlterableContext;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for https://issues.jboss.org/browse/CDI-139
 *
 * @author Jozef Hartinger
 *
 */
@ExtendWith(ArquillianExtension.class)
public class DestroyingNormalScopedInstanceTest {

    private static final String[] VALUES = { "foo", "bar", "baz" };

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(DestroyingNormalScopedInstanceTest.class))
                .addPackage(DestroyingNormalScopedInstanceTest.class.getPackage())
                .addAsServiceProvider(Extension.class, CustomScopeExtension.class);
    }

    @Inject
    private BeanManager manager;

    @Test
    public void testApplicationScopedComponent(Instance<ApplicationScopedComponent> instance) {
        testComponent(instance);
    }

    @Test
    public void testRequestScopedComponent(Instance<RequestScopedComponent> instance) {
        testComponent(instance);
    }

    @Test
    public void testCustomScopedComponent(Instance<CustomScopedComponent> instance) {
        testComponent(instance);
    }

    @Test
    public void testNothingHappensIfNoInstanceToDestroy(ApplicationScopedComponent application) {
        Bean<?> bean = manager.resolve(manager.getBeans(ApplicationScopedComponent.class));
        AlterableContext context = (AlterableContext) manager.getContext(bean.getScope());

        AbstractComponent.reset();
        application.setValue("value");
        context.destroy(bean);
        assertTrue(AbstractComponent.isDestroyed());

        context.destroy(bean); // make sure subsequent calls do not raise exception
        context.destroy(bean);
    }

    private <T extends AbstractComponent> void testComponent(Instance<T> instance) {
        for (String string : VALUES) {
            T reference = instance.get();
            assertNull(reference.getValue());
            reference.setValue(string);
            assertEquals(string, reference.getValue());

            AbstractComponent.reset();
            instance.destroy(reference);
            assertTrue(AbstractComponent.isDestroyed());
            assertNull(reference.getValue(), reference.getValue());
        }
    }

    @Test
    public void testUnsupportedOperationExceptionThrownIfUnderlyingContextNotAlterable(Instance<CustomScopedComponent> instance,
            CustomScopeExtension extension) {
        try {
            extension.switchToNonAlterable();
            CustomScopedComponent component = instance.get();
            instance.destroy(component);
            Assertions.fail("expected exception not thrown");
        } catch (UnsupportedOperationException expected) {
        } finally {
            extension.switchToAlterable();
        }
    }

    @Test
    public void testContextDestroyCalled(Instance<CustomScopedComponent> instance) {
        CustomScopedComponent component = instance.get();
        CustomAlterableContext.reset();
        instance.destroy(component);
        assertTrue(CustomAlterableContext.isDestroyCalled());
    }

    @Test
    public void testNullParameter(Instance<ApplicationScopedComponent> instance) {
        assertThrows(NullPointerException.class, () -> instance.destroy(null));
    }

    @Tag("Integration")
    @Test
    public void testSFSessionBeanDependentDestroy() {
        SFSessionBean.DESTROYED.set(false);
        Instance<SFSessionBean> sessionBeanInstance = CDI.current().select(SFSessionBean.class);
        SFSessionBean sessionBean = sessionBeanInstance.get();
        sessionBean.ping();
        sessionBeanInstance.destroy(sessionBean);
        assertTrue(SFSessionBean.DESTROYED.get());
    }
}
