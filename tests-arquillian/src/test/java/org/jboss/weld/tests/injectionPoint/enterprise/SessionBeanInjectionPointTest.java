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
package org.jboss.weld.tests.injectionPoint.enterprise;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class SessionBeanInjectionPointTest {

    @Inject
    TestClient client;

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SessionBeanInjectionPointTest.class))
                .addPackage(SessionBeanInjectionPointTest.class.getPackage())
                .addClass(Utils.class);
    }

    @Test
    public void testInjectionPointInSLSB() throws Exception {
        assertTrue(client.getBar().isIPAvailable(), "InjectPoint was null in " + Bar.class);
        assertNotNull(client.getBar().getInjectionPointMetadata());
        assertEquals(Bar.class, client.getBar().getInjectionPointType());
        assertEquals(TestClient.class.getDeclaredField("bar"), client.getBar().getInjectionPointMember());
    }

    @Test
    public void testInjectionPointInSingletonBean() throws Exception {
        assertTrue(client.getBaz().isIPAvailable(), "InjectPoint was null in " + Baz.class);
        assertNotNull(client.getBaz().getInjectionPointMetadata());
        assertEquals(Baz.class, client.getBaz().getInjectionPointType());
        assertEquals(TestClient.class.getDeclaredField("baz"), client.getBaz().getInjectionPointMember());
    }

    @Test
    public void testInjectionPointInSFSB() throws Exception {
        InjectionPoint fooIp = client.getFoo().getInjectionPoint();
        assertNotNull(fooIp, "InjectPoint was null in " + Foo.class);
        assertEquals(Foo.class, fooIp.getType());
        assertEquals(TestClient.class.getDeclaredField("foo"), fooIp.getMember());

    }

}
