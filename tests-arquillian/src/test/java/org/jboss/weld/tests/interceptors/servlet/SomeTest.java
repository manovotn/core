/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.interceptors.servlet;

import static org.junit.Assert.assertFalse;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.Integration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@RunWith(Arquillian.class)
@Category(Integration.class)
public class SomeTest {

    @ArquillianResource
    private URL url;

    private static final String BEANS_CONFIG = "<beans><interceptors>"
        + "<class>org.jboss.weld.tests.interceptors.servlet.SomeInterceptor</class>"
        + "</interceptors></beans>";

    @Deployment(testable = false)
    public static EnterpriseArchive createTestArchive() {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "some.ear");
        ear.addAsModule(ShrinkWrap
            .create(WebArchive.class,
                Utils.getDeploymentNameAsHash(SomeTest.class,
                    Utils.ARCHIVE_TYPE.WAR))
            .addPackage(TestServlet.class.getPackage())
            .addAsWebInfResource(new StringAsset(BEANS_CONFIG), "beans.xml"));
        return ear;
    }

    @Test
    public void testHttpSession() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(true);
        Page page = client.getPage(url + "test");
        String text = page.getWebResponse().getContentAsString();
        assertFalse(text.isEmpty());
        Assert.assertTrue(text.contains("barfoo"));
    }
}
