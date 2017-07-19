package org.jboss.weld.tests.interceptors.defaultmethod;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.EmbeddedContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
// use embedded to avoid versions from WFLY
@Category(EmbeddedContainer.class)
public class InterceptedBeanWithDefaultMethodTest {

   @Inject
   private Foo foo;

   @Deployment
   public static WebArchive deploy() {
       
       // TODO - does not work, still uses new one and passes
      File oldClassFileWriter = Maven.resolver().resolve("org.jboss.classfilewriter:jboss-classfilewriter:1.1.2.Final").withTransitivity().asSingleFile();
      return ShrinkWrap.create(WebArchive.class, Utils.getDeploymentNameAsHash(InterceptedBeanWithDefaultMethodTest.class, Utils.ARCHIVE_TYPE.WAR))
          .addAsLibraries(oldClassFileWriter) // force old classfilewriter on CP
          .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
          .addPackage(InterceptedBeanWithDefaultMethodTest.class.getPackage());
   }

   /*
   * description = "WELD-2405"
   */
   @Test
   public void testDefaultMethodInvocationOnInterceptedBean() {
      Assert.assertTrue(foo.isIntercepted());
   }
}
