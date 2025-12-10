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
package org.jboss.weld.tests.scope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import jakarta.servlet.http.HttpServletResponse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class RemoteScopeTest {

    @Deployment(testable = false)
    public static Archive<?> deploy() {
        return ShrinkWrap.create(WebArchive.class, Utils.getDeploymentNameAsHash(RemoteScopeTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addClasses(Bar.class, Foo.class, RemoteClient.class, Special.class, Temp.class, TempConsumer.class,
                        TempProducer.class, Useless.class)
                .addClasses(Utils.class, Assertions.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /*
     * description = "WELD-311"
     */
    @Test
    public void testScopeOfProducerMethod(@ArquillianResource URL baseURL) throws Exception {
        WebClient client = new WebClient();
        Page page = client.getPage(new URL(baseURL, "request1"));
        assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
        page = client.getPage(new URL(baseURL, "request2"));
        assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
    }

}
