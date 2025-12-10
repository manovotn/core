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

package org.jboss.weld.tests.event.container.lifecycle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Startup;

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
 * Simple test observing {@link Startup} and {@link jakarta.enterprise.event.Shutdown} events.
 * Note that we cannot properly verify shutdown events in these tests because the entirety of the test happens
 * before container attempts shutdown.
 */
@ExtendWith(ArquillianExtension.class)
public class StartupShutdownEventTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(StartupShutdownEventTest.class))
                .addPackage(StartupShutdownEventTest.class.getPackage());
    }

    @Test
    public void testEventsObserved() {
        Assertions.assertEquals(2, ObservingBean.OBSERVED_STARTING_EVENTS.size());
        Assertions.assertEquals(ObservingBean.OBSERVED_STARTING_EVENTS.get(0), ApplicationScoped.class.getSimpleName());
        Assertions.assertEquals(ObservingBean.OBSERVED_STARTING_EVENTS.get(1), Startup.class.getSimpleName());

        // Note that we cannot assert that shutdown event was invoked because entirety of this test class
        // happens before shutdown
        Assertions.assertTrue(ObservingBean.OBSERVED_SHUTDOWN_EVENTS.isEmpty());
    }
}
