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
package org.jboss.weld.tests.resolution.circular.self;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Kirill Gaevskii
 *
 */
@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class SelfInjectionBeanTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SelfInjectionBeanTest.class))
                .intercept(SelfInterceptor.class)
                .addPackage(SelfInjectionBeanTest.class.getPackage());
    }

    @Inject
    SelfInjectionBean bean;

    @Inject
    SingletonSelfInjectionBean singletonBean;

    @Test
    public void testMethodA() {
        Assertions.assertEquals(new Integer(11), bean.methodA(10));
    }

    @Test
    public void testMethodB() {
        Assertions.assertEquals(new Integer(10), bean.methodB(10));
    }

    @Test
    public void testSingletonMethodA() {
        Assertions.assertEquals(new Integer(11), singletonBean.methodA(10));
    }

    @Test
    public void testSingletonMethodB() {
        Assertions.assertEquals(new Integer(10), singletonBean.methodB(10));
    }
}
