package org.jboss.weld.tests.contexts.request.filtering;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class HttpRequestFilteringTest {

    @ArquillianResource(FooServlet.class)
    private URL fooContextPath;

    @ArquillianResource(BarServlet.class)
    private URL barContextPath;

    @Deployment(testable = false)
    public static Archive<?> getDeployment() {
        JavaArchive library = ShrinkWrap.create(JavaArchive.class).addClass(Foo.class);
        WebArchive foo = ShrinkWrap.create(WebArchive.class, "foo.war").addClass(FooServlet.class)
                .addAsWebInfResource(HttpRequestFilteringTest.class.getPackage(), "web.xml", "web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        WebArchive bar = ShrinkWrap.create(WebArchive.class, "bar.war").addClass(BarServlet.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return ShrinkWrap
                .create(EnterpriseArchive.class,
                        Utils.getDeploymentNameAsHash(HttpRequestFilteringTest.class, Utils.ARCHIVE_TYPE.EAR))
                .addAsModules(foo, bar).addAsLibrary(library);
    }

    @Test
    public void testContextsActive() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        WebClient client = new WebClient();
        Assertions.assertEquals("true", client.getPage(fooContextPath + "/foo.html").getWebResponse().getContentAsString());
        Assertions.assertEquals("true", client.getPage(barContextPath + "/foo.html").getWebResponse().getContentAsString());
        Assertions.assertEquals("true", client.getPage(barContextPath + "/foo.css").getWebResponse().getContentAsString());
    }

    @Test
    public void testContextsInactive() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        WebClient client = new WebClient();
        Assertions.assertEquals("false", client.getPage(fooContextPath + "/foo.css").getWebResponse().getContentAsString());
    }
}
