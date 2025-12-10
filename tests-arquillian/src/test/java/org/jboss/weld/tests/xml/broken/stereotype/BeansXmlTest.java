package org.jboss.weld.tests.xml.broken.stereotype;

import jakarta.enterprise.inject.spi.DefinitionException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class BeansXmlTest {
    @Deployment
    @ShouldThrowException(DefinitionException.class)
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(BeansXmlTest.class))
                .stereotype(Foo.class)
                .beanDiscoveryMode(BeanDiscoveryMode.ALL)
                .addPackage(BeansXmlTest.class.getPackage());
    }

    @Test
    public void testStereotypeRespectedInBeansXml() {
        //assert false; // Arquillian ShouldThrowException marks it as allowed, does not stop @Test from execution
    }

}
