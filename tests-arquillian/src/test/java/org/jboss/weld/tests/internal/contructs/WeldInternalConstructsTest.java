/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.internal.contructs;

import java.util.Set;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.bean.WeldBean;
import org.jboss.weld.proxy.WeldClientProxy;
import org.jboss.weld.proxy.WeldConstruct;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * A test set covering WELD-1914. All our subclasses/proxies should implement {@link WeldContruct} and client proxies should
 * also implement {@link WeldClientProxy}. This test asserts this as well as functionality of methods in
 * {@link WeldClientProxy}.
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@ExtendWith(ArquillianExtension.class)
public class WeldInternalConstructsTest {

    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(WeldInternalConstructsTest.class))
                .addPackage(WeldInternalConstructsTest.class.getPackage());
    }

    @Inject
    BeanHolder holder;

    @Inject
    BeanManager bm;

    @Test
    public void testClientProxyBean() {
        ClientProxyBean clientProxyBean = holder.getClientProxyBean();
        // trigger proxy creation
        clientProxyBean.ping();

        // injected bean should be instance of WeldConstruct and WeldClientProxy
        Assertions.assertTrue(clientProxyBean instanceof WeldConstruct);
        Assertions.assertTrue(clientProxyBean instanceof WeldClientProxy);

        // cast to WeldClientProxy and test the methods
        WeldClientProxy wcp = (WeldClientProxy) clientProxyBean;
        WeldClientProxy.Metadata cm = wcp.getMetadata();
        Object contextualInstance = cm.getContextualInstance();
        // kind of indirect check that this is the actual contextual instance
        Assertions.assertTrue(contextualInstance instanceof ClientProxyBean);
        Assertions.assertFalse(contextualInstance instanceof WeldConstruct);

        Bean<?> bean = cm.getBean();
        Set<Bean<?>> beans = bm.getBeans(ClientProxyBean.class);
        Assertions.assertEquals(1, beans.size());
        Assertions.assertEquals(((WeldBean) beans.iterator().next()).getIdentifier().asString(),
                ((WeldBean) bean).getIdentifier().asString());
    }

    @Test
    public void testInterceptedDependentBean() {
        InterceptedDependentBean interceptedBean = holder.getInterceptedDependentBean();
        // trigger interception and assert it works
        Assertions.assertTrue(interceptedBean.ping());

        // should be instance of WeldConstruct but NOT WeldClientProxy
        Assertions.assertTrue(interceptedBean instanceof WeldConstruct);
        Assertions.assertFalse(interceptedBean instanceof WeldClientProxy);
    }

    @Test
    public void testDecoratedDependentBean() {
        DecoratedDependentBean decoratedBean = holder.getDecoratedDependentBean();
        // trigger decoration and assert it works
        Assertions.assertTrue(decoratedBean.ping());

        // should be instance of WeldConstruct but NOT WeldClientProxy
        Assertions.assertTrue(decoratedBean instanceof WeldConstruct);
        Assertions.assertFalse(decoratedBean instanceof WeldClientProxy);
    }

    @Test
    public void testDecoratedProxiedBean() {
        DecoratedProxiedBean decoratedBean = holder.getDecoratedProxiedBean();
        // trigger decoration and assert it works
        Assertions.assertTrue(decoratedBean.ping());

        // should be instance of WeldConstruct and WeldClientProxy
        Assertions.assertTrue(decoratedBean instanceof WeldConstruct);
        Assertions.assertTrue(decoratedBean instanceof WeldClientProxy);

        // cast to WeldClientProxy and test the methods
        WeldClientProxy wcp = (WeldClientProxy) decoratedBean;
        WeldClientProxy.Metadata cm = wcp.getMetadata();

        Object contextualInstance = cm.getContextualInstance();
        // kind of indirect check that this is the actual contextual instance
        Assertions.assertTrue(contextualInstance instanceof DecoratedProxiedBean);
        Assertions.assertFalse(contextualInstance instanceof WeldClientProxy);
        // NOTE - contextual instance is still a Weld subclass because of interception/decoration
        Assertions.assertTrue(contextualInstance instanceof WeldConstruct);

        Bean<?> bean = cm.getBean();
        Set<Bean<?>> beans = bm.getBeans(DecoratedProxiedBean.class);
        Assertions.assertEquals(1, beans.size());
        Assertions.assertEquals(((WeldBean) beans.iterator().next()).getIdentifier().asString(),
                ((WeldBean) bean).getIdentifier().asString());
    }

    @Test
    public void testInterceptedProxiedBean() {
        InterceptedProxiedBean interceptedBean = holder.getInterceptedProxiedBean();
        // trigger interception and assert it works
        Assertions.assertTrue(interceptedBean.ping());

        // should be instance of WeldConstruct and WeldClientProxy
        Assertions.assertTrue(interceptedBean instanceof WeldConstruct);
        Assertions.assertTrue(interceptedBean instanceof WeldClientProxy);

        // cast to WeldClientProxy and test the methods
        WeldClientProxy wcp = (WeldClientProxy) interceptedBean;
        WeldClientProxy.Metadata cm = wcp.getMetadata();

        Object contextualInstance = cm.getContextualInstance();
        // kind of indirect check that this is the actual contextual instance
        Assertions.assertTrue(contextualInstance instanceof InterceptedProxiedBean);
        Assertions.assertFalse(contextualInstance instanceof WeldClientProxy);
        // NOTE - contextual instance is still a Weld subclass because of interception/decoration
        Assertions.assertTrue(contextualInstance instanceof WeldConstruct);

        Bean<?> bean = cm.getBean();
        Set<Bean<?>> beans = bm.getBeans(InterceptedProxiedBean.class);
        Assertions.assertEquals(1, beans.size());
        Assertions.assertEquals(((WeldBean) beans.iterator().next()).getIdentifier().asString(),
                ((WeldBean) bean).getIdentifier().asString());
    }
}
