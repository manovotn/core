package org.jboss.weld.tests.jsf;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class JsfTest {

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, Utils.getDeploymentNameAsHash(JsfTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addPackage(JsfTest.class.getPackage())
                .addAsWebInfResource(JsfTest.class.getPackage(), "faces-config.xml", "faces-config.xml")
                .addAsWebInfResource(JsfTest.class.getPackage(), "web.xml", "web.xml")
                .addAsWebInfResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml");
    }

    @Test
    // WELD-492
    public void testExtendsUiComponent(Garply garply) {
        Assertions.assertNotNull(garply);
    }

}
