package org.jboss.weld.tests.servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class ServletBeanTest {

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ServletBeanTest.class))
                .addPackage(ServletBeanTest.class.getPackage());
    }

    @Test
    // WELD-492
    public void testImplementsServlet(Foo foo) {
        assertNotNull(foo);
    }

    @Test
    // WELD-492
    public void testImplementsFilter(Bar bar) {
        assertNotNull(bar);
    }

    @Test
    // WELD-492
    public void testImplementsServletContextListener(Baz baz) {
        assertNotNull(baz);
    }

    @Test
    // WELD-492
    public void testImplementsHttpSessionListener(Qux qux) {
        assertNotNull(qux);
    }

    @Test
    // WELD-492
    public void testImplementsServletREquestListener(Corge corge) {
        assertNotNull(corge);
    }

}
