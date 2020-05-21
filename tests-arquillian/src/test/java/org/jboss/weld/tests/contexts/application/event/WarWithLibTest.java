/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.tests.contexts.application.event;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.tests.category.Integration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Category(Integration.class)
public class WarWithLibTest {

    @Deployment
    public static WebArchive getDeployment() {
        JavaArchive warLib = ShrinkWrap.create(BeanArchive.class).addClasses(AbstractObserver.class, EventRepository.class, MultiObserver4.class);
        WebArchive war = Testable.archiveToTest(ShrinkWrap.create(WebArchive.class, "test1.war").addClasses(MultiObserver1.class, WarWithLibTest.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml"));
        war.addAsLibrary(warLib);
        return war;
    }

    @Test
    public void testServletContextObservers() {
        // assert that both WAR and WAR/lib get this event
        Assert.assertEquals(2, EventRepository.SERVLET_CONTEXTS.size());
        Assert.assertTrue(EventRepository.SERVLET_CONTEXTS.contains("test1"));
    }

    @Test
    public void testObject() {
        // both, war and lib should be notified
        Assert.assertEquals(EventRepository.OBJECTS.toString(), 2, EventRepository.OBJECTS.size());
        Assert.assertTrue(EventRepository.OBJECTS.contains("test1"));
        Assert.assertTrue(EventRepository.OBJECTS.contains("lib"));
    }
}
