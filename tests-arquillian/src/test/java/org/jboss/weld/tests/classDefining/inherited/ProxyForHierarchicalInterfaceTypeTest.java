/*
 * JBoss, Home of Professional Open Source
 * Copyright 2021, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.tests.classDefining.inherited;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.classDefining.inherited.base.AncestorInterface;
import org.jboss.weld.tests.classDefining.inherited.extending.MyInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that you can create a proxy (specifically under JDK 11) from producer that returns a hierarchical interface
 * type. E.g. tests that the resulting proxy name has package corresponding to its class name.
 *
 * One of the proxies is built on an interface extending Principal, this is deliberate as that lies in java.* package
 * which gets special treatment.
 */
@ExtendWith(ArquillianExtension.class)
public class ProxyForHierarchicalInterfaceTypeTest {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ProxyForHierarchicalInterfaceTypeTest.class))
                .addClass(AncestorInterface.class)
                .addClass(MyInterface.class)
                .addClass(ConsumerBean.class)
                .addClass(BeanProducer.class)
                .addClass(AMuchBetterPrincipal.class);
    }

    @Inject
    ConsumerBean bean;

    @Test
    public void testProxyDefinitionWorks() {
        // invoke the method, the verification lies mainly in not getting errors when creating proxy
        MyInterface interfaceBean = this.bean.getProducedInterfaceBean();
        Assertions.assertEquals(MyInterface.class.getSimpleName(), interfaceBean.anotherPing());
        Assertions.assertEquals(AncestorInterface.class.getSimpleName(), interfaceBean.ping());
        // assert that the proxy from hierarchical interface starts with package and class of the most specific interface we know of
        Assertions.assertTrue(interfaceBean.getClass().getName()
                .startsWith("org.jboss.weld.tests.classDefining.inherited.extending.MyInterface"));

        AMuchBetterPrincipal principal = this.bean.getPrincipal();
        Assertions.assertEquals(AMuchBetterPrincipal.class.getSimpleName(), principal.getName());
        // assert that the proxy created from Principal and custom class has the package of custom class
        Assertions.assertTrue(
                principal.getClass().getName().startsWith("org.jboss.weld.tests.classDefining.inherited.AMuchBetterPrincipal"));
    }
}
