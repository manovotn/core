/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.tests.jsf.weld1247;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
@Tag("Integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)
public class Weld1247Test {

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, Utils.getDeploymentNameAsHash(Weld1247Test.class, Utils.ARCHIVE_TYPE.WAR))
                .addClass(Bean.class)
                .addAsWebResource(Weld1247Test.class.getPackage(), "index.xhtml", "index.xhtml")
                .addAsWebInfResource(Weld1247Test.class.getPackage(), "web.xml", "web.xml")
                .addAsWebInfResource(Weld1247Test.class.getPackage(), "faces-config.xml", "faces-config.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @RunAsClient
    @Order(1)
    public void openIndexPage(@ArquillianResource URL url) throws Exception {
        WebClient client = new WebClient();
        client.getPage(url + "/index.faces");
    }

    @Test
    @Order(2)
    public void testPreRenderViewExecutedExactlyOnce() throws Exception {
        assertEquals(1, Bean.invocationCount);
    }

}
