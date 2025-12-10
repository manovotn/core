/*
 * JBoss, Home of Professional Open Source
 * Copyright 2022, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.tests.observers.metadata;

import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ObserverMethod;
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
 * Tests metadata obtainable for a given observer method
 */
@ExtendWith(ArquillianExtension.class)
public class ObserverDeclaringBeanTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ObserverDeclaringBeanTest.class))
                .addAsServiceProvider(Extension.class, ObserverRegisteringExtension.class)
                .addPackage(ObserverDeclaringBeanTest.class.getPackage());
    }

    @Inject
    BeanManager bm;

    @Test
    public void testMetadataForStandardEvent() {
        EventPayload payload = new EventPayload();
        Assertions.assertFalse(FirstObserver.OBSERVER_NOTIFIED);
        bm.getEvent().fire(payload);
        Assertions.assertTrue(FirstObserver.OBSERVER_NOTIFIED);

        Set<ObserverMethod<? super EventPayload>> observerMethods = bm.resolveObserverMethods(payload);
        Assertions.assertEquals(1, observerMethods.size());
        Bean<?> declaringBean = observerMethods.iterator().next().getDeclaringBean();
        Assertions.assertTrue(declaringBean.isAlternative());
        Assertions.assertEquals(Dependent.class, declaringBean.getScope());
        Assertions.assertEquals(FirstObserver.class, declaringBean.getBeanClass());
    }

    // specification doesn't say what should happen in this case, the behavior is Weld-specific
    @Test
    public void testMetadataForSyntheticEvent() {
        String stringLoad = "payload";

        Assertions.assertEquals(0, ObserverRegisteringExtension.TIMES_OBSERVERS_NOTIFIED);
        bm.getEvent().fire(stringLoad);
        Assertions.assertEquals(1, ObserverRegisteringExtension.TIMES_OBSERVERS_NOTIFIED);

        Set<ObserverMethod<? super Object>> observerMethods = bm.resolveObserverMethods(stringLoad);
        Assertions.assertEquals(1, observerMethods.size());
        Assertions.assertNull(observerMethods.iterator().next().getDeclaringBean());
    }
}
