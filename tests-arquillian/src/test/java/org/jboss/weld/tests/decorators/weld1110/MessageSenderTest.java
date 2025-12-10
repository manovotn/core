package org.jboss.weld.tests.decorators.weld1110;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class MessageSenderTest {

    @Deployment(testable = false)
    public static WebArchive create() {
        return ShrinkWrap
                .create(WebArchive.class, Utils.getDeploymentNameAsHash(MessageSenderTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addPackage(MessageSenderTest.class.getPackage())
                .setWebXML(new StringAsset(
                        "<web-app>" +
                                "<display-name>jax</display-name>" +
                                "<servlet-mapping>" +
                                "<servlet-name>jakarta.ws.rs.core.Application</servlet-name>" +
                                "<url-pattern>/rest/*</url-pattern>" +
                                "</servlet-mapping>" +
                                "</web-app>"))
                .addAsWebInfResource(
                        new BeansXml().decorators(MessageDecorator.class), "beans.xml");
    }

    @ArquillianResource
    private URL base;

    @Test
    public void testImpl() throws Exception {
        String response = getHttpGETResponse("rest/message/Hello");
        Assertions.assertEquals("Decorated Hello", response);
    }

    @Test
    public void testFacade() throws Exception {
        String response = getHttpGETResponse("rest/facade/Hello");
        Assertions.assertEquals("Decorated Hello", response);
    }

    private String getHttpGETResponse(String urlPath) throws IOException {
        URL url = new URL(base, urlPath);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return in.readLine();
        }
    }
}
