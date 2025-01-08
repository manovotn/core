package org.jboss.weld.tests.interceptors.lambda;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InterceptedClassWithLambdaTest {

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(InterceptedClassWithLambdaTest.class))
                .addPackage(InterceptedClassWithLambdaTest.class.getPackage());
    }

    @Inject
    FooImpl foo;

    @Test
    public void testInterceptorInvoked() {
        // NOTE - this does NOT test the actual behavior; so long as javac is used, it works regardless!!!
        Assert.assertEquals(0, MyInterceptor.timesInvoked);
        foo.invokeLambda();
        Assert.assertEquals(1, MyInterceptor.timesInvoked);
    }
}
