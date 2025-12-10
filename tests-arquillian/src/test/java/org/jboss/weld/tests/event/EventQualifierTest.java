/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.event;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.BeanManager;
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
public class EventQualifierTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(EventQualifierTest.class))
                .addPackage(EventQualifierTest.class.getPackage());
    }

    @Inject
    BeanManager bm;

    @Test
    public void testDefaultQualifierNotRequired(Bar bar) {
        bar.fireWithNoQualifiers();
        Assertions.assertTrue(bar.isUnqualifiedObserved());
        Assertions.assertFalse(bar.isUpdatedObserved());
        bar.reset();
        bar.fireWithNoQualifiersViaManager();
        Assertions.assertTrue(bar.isUnqualifiedObserved());
        Assertions.assertFalse(bar.isUpdatedObserved());
        bar.reset();
        bar.fireWithUpdatedQualifierViaAnnotation();
        Assertions.assertTrue(bar.isUnqualifiedObserved());
        Assertions.assertTrue(bar.isUpdatedObserved());
        bar.reset();
        bar.fireWithUpdatedQualifierViaManager();
        Assertions.assertTrue(bar.isUpdatedObserved());
        Assertions.assertTrue(bar.isUnqualifiedObserved());
        bar.reset();
        bar.fireWithUpdatedQualifierViaSelect();
        Assertions.assertTrue(bar.isUnqualifiedObserved());
        Assertions.assertTrue(bar.isUpdatedObserved());
    }

    @Test
    public void testDefaultQualifierPresent(ObservingBean bean)
            throws ExecutionException, InterruptedException, TimeoutException {
        bean.reset();

        Set<Annotation> expectedQualifiers = Set.of(Any.Literal.INSTANCE, Default.Literal.INSTANCE);

        // just get event fire right away - @Default should be included
        bm.getEvent().fire(new Payload());
        Assertions.assertEquals(1, bean.getDefaultObjectNotified());
        Assertions.assertEquals(1, bean.getDefaultPayloadNotified());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultObjectQualifiers());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultPayloadQualifiers());

        // select Payload and fire - @Default should be included
        bm.getEvent().select(Payload.class).fire(new Payload());
        Assertions.assertEquals(2, bean.getDefaultObjectNotified());
        Assertions.assertEquals(2, bean.getDefaultPayloadNotified());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultObjectQualifiers());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultPayloadQualifiers());

        // same in async variant
        // just get event fire right away - @Default should be included
        bm.getEvent().fireAsync(new Payload()).toCompletableFuture().get(2, TimeUnit.SECONDS);
        Assertions.assertEquals(1, bean.getDefaultObjectAsyncNotified());
        Assertions.assertEquals(1, bean.getDefaultPayloadAsyncNotified());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultObjectAsyncQualifiers());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultPayloadAsyncQualifiers());

        // select Payload and fire - @Default should be included
        bm.getEvent().select(Payload.class).fireAsync(new Payload()).toCompletableFuture().get(2, TimeUnit.SECONDS);
        Assertions.assertEquals(2, bean.getDefaultObjectAsyncNotified());
        Assertions.assertEquals(2, bean.getDefaultPayloadAsyncNotified());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultObjectAsyncQualifiers());
        Assertions.assertEquals(expectedQualifiers, bean.getDefaultPayloadAsyncQualifiers());

    }

}
