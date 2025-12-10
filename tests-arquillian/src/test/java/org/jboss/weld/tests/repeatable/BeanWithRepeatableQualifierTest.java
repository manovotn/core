/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.repeatable;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.repeatable.RepeatableQualifier.Literal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class BeanWithRepeatableQualifierTest {

    @Inject
    @RepeatableQualifier("foo")
    private String foo;

    @Inject
    @RepeatableQualifier("bar")
    private String bar;

    @Inject
    @RepeatableQualifier("bar")
    @RepeatableQualifier("baz")
    private String combined;

    @Inject
    @Any
    private Instance<String> instance;

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(EventWithRepeatableQualifierTest.class))
                .addPackage(BeanWithRepeatableQualifierTest.class.getPackage());
    }

    @Test
    public void testInjection() {
        Assertions.assertEquals("yeah", foo);
        Assertions.assertEquals("yeah", bar);
        Assertions.assertEquals("yeah", combined);
    }

    @Test
    public void testBeanMetadata(BeanManagerImpl manager) {
        Bean<?> bean = manager.resolve(manager.getBeans(String.class, new Literal("foo")));
        Set<String> values = bean.getQualifiers().stream()
                .filter((x) -> x instanceof RepeatableQualifier)
                .map((x) -> ((RepeatableQualifier) x).value())
                .collect(Collectors.toSet());
        Assertions.assertEquals(3, values.size());
        Assertions.assertTrue(values.contains("foo"));
        Assertions.assertTrue(values.contains("bar"));
        Assertions.assertTrue(values.contains("baz"));
    }

    @Test
    public void testInstanceLookup() {
        instance = instance.select(new Literal("foo"));
        Assertions.assertFalse(instance.isAmbiguous());
        Assertions.assertFalse(instance.isUnsatisfied());
        instance = instance.select(new Literal("bar"), new Literal("baz"));
        Assertions.assertFalse(instance.isAmbiguous());
        Assertions.assertFalse(instance.isUnsatisfied());
        Assertions.assertEquals("yeah", instance.get());
        instance = instance.select(new Literal("qux"));
        Assertions.assertFalse(instance.isAmbiguous());
        Assertions.assertTrue(instance.isUnsatisfied());
    }

    @Test
    public void testBeanManagerLookup(BeanManager manager) {
        Assertions.assertNotNull(
                manager.resolve(manager.getBeans(String.class, new Literal("foo"), new Literal("bar"), new Literal("baz"))));
        Assertions.assertNotNull(manager.resolve(manager.getBeans(String.class, new Literal("foo"), new Literal("bar"))));
        Assertions.assertTrue(manager.getBeans(String.class, new Literal("foo"), new Literal("qux")).isEmpty());
    }
}
