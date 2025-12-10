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
package org.jboss.weld.tests.extensions.interceptors.annotated;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InterceptionType;
import jakarta.enterprise.util.AnnotationLiteral;
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
public class SpiAddedInterceptorBindingTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SpiAddedInterceptorBindingTest.class))
                .intercept(QuickInterceptor.class).intercept(SlowInterceptor.class)
                .addPackage(SpiAddedInterceptorBindingTest.class.getPackage())
                .addAsServiceProvider(Extension.class, QuickExtension.class);
    }

    @Inject
    BeanManager beanManager;

    @Inject
    Hack hack;

    @Inject
    Snail snail;

    @Inject
    James james;

    @SuppressWarnings("serial")
    @Test
    public void testAddedInterceptorBindingAnnotatedType() {
        QuickInterceptor.reset();
        Assertions.assertTrue(beanManager.isInterceptorBinding(Quick.class));
        Assertions.assertEquals(1, beanManager.resolveInterceptors(InterceptionType.AROUND_INVOKE, new QuickLiteral() {

            @Override
            public String name() {
                return "man";
            }

            @Override
            public boolean dirty() {
                return false;
            }
        }).size());
        Assertions.assertFalse(QuickInterceptor.isIntercepted);
        hack.ping();
        Assertions.assertTrue(QuickInterceptor.isIntercepted);

        SlowInterceptor.reset();
        Assertions.assertTrue(beanManager.isInterceptorBinding(Slow.class));
        Assertions.assertEquals(1,
                beanManager.resolveInterceptors(InterceptionType.AROUND_INVOKE, new AnnotationLiteral<Slow>() {
                }).size());
        Assertions.assertFalse(SlowInterceptor.isIntercepted);
        snail.ping();
        Assertions.assertTrue(SlowInterceptor.isIntercepted);
    }

    @Test
    public void testAddedInterceptorBindingAnnotatedTypeWithStereotype() {
        QuickInterceptor.reset();
        Assertions.assertFalse(QuickInterceptor.isIntercepted);
        james.ping();
        Assertions.assertTrue(QuickInterceptor.isIntercepted);
    }

}
