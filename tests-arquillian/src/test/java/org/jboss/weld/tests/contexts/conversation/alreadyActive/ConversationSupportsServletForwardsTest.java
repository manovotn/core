package org.jboss.weld.tests.contexts.conversation.alreadyActive;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Tag("Integration")
@ExtendWith(ArquillianExtension.class)
public class ConversationSupportsServletForwardsTest {
    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ShrinkWrap
                .create(WebArchive.class,
                        Utils.getDeploymentNameAsHash(ConversationSupportsServletForwardsTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addAsWebResource(ConversationSupportsServletForwardsTest.class.getPackage(), "conversations.xhtml",
                        "conversations.xhtml")
                .addAsWebResource(ConversationSupportsServletForwardsTest.class.getPackage(), "conversations.xhtml",
                        "result.xhtml")
                .addAsWebInfResource(ConversationSupportsServletForwardsTest.class.getPackage(),
                        "conversation-faces-config.xml", "faces-config.xml")
                .addAsWebInfResource(ConversationSupportsServletForwardsTest.class.getPackage(),
                        "conversation-web.xml", "web.xml")
                .addClass(ForwardingPhaseListener.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testConversationSupportsForwards(@ArquillianResource URL baseURL) throws Exception {
        WebClient client = new WebClient();
        try {
            client.getPage(new URL(baseURL, "conversations.xhtml"));
        } catch (FailingHttpStatusCodeException e) {
            Assertions.fail("Expected HTTP status code 200 but was " + e.getStatusCode());
        }
    }

    @Test
    public void testConversationSupportsErrorPages(@ArquillianResource URL baseURL) throws Exception {
        WebClient client = new WebClient();
        try {
            client.getPage(new URL(baseURL, "missing-page.xhtml"));
        } catch (FailingHttpStatusCodeException e) {
            Assertions.assertEquals(404, e.getStatusCode());
        }
    }

    @Test
    public void testConversationSupportsForwardingToErrorPages(@ArquillianResource URL baseURL) throws Exception {
        WebClient client = new WebClient();
        try {
            client.getPage(new URL(baseURL, "missing-page-error.xhtml"));
        } catch (FailingHttpStatusCodeException e) {
            Assertions.assertEquals(404, e.getStatusCode());
        }
    }
}
