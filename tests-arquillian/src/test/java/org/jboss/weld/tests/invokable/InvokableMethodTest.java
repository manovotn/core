package org.jboss.weld.tests.invokable;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.invokable.common.ArgTransformer;
import org.jboss.weld.tests.invokable.common.FooArg;
import org.jboss.weld.tests.invokable.common.HelperBean;
import org.jboss.weld.tests.invokable.common.InstanceTransformer;
import org.jboss.weld.tests.invokable.common.SimpleBean;
import org.jboss.weld.tests.invokable.common.TransformableBean;
import org.jboss.weld.tests.invokable.common.TrulyExceptionalBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class InvokableMethodTest {

    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(InvokableMethodTest.class))
                .addPackage(InvokableMethodTest.class.getPackage())
                .addPackage(SimpleBean.class.getPackage())
                .addAsServiceProvider(Extension.class, ObservingExtension.class);
    }

    @Inject
    ObservingExtension extension;

    @Inject
    SimpleBean simpleBean;

    @Inject
    TransformableBean transformableBean;

    @Inject
    TrulyExceptionalBean trulyExceptionalBean;

    @Test
    public void testSimpleInvokableMethod() throws Exception {
        SimpleBean.resetDestroyCounter();
        HelperBean.clearDestroyedCounters();

        // no transformation invocation, no lookup
        Assertions.assertEquals("foo1", extension.getNoTransformationInvoker().invoke(simpleBean, new Object[] { "foo", 1 }));
        Assertions.assertEquals(0, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(0, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(0, HelperBean.timesIntDestroyed);

        // no transformation, instance lookup
        Assertions.assertEquals("foo1", extension.getInstanceLookupInvoker().invoke(null, new Object[] { "foo", 1 }));
        Assertions.assertEquals(1, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(0, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(0, HelperBean.timesIntDestroyed);

        // no transformation, arg lookup (all)
        Assertions.assertEquals("bar42", extension.getArgLookupInvoker().invoke(simpleBean, new Object[] { "blah", null }));
        Assertions.assertEquals(1, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(1, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(1, HelperBean.timesIntDestroyed);

        // no transformation, instance lookup and arg lookup (all)
        Assertions.assertEquals("bar42",
                extension.getLookupAllInvoker().invoke(new SimpleBean(), new Object[] { "blah", null }));
        Assertions.assertEquals(2, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(2, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(2, HelperBean.timesIntDestroyed);
    }

    @Test
    public void testSimpleStaticInvokableMethod() throws Exception {
        SimpleBean.resetDestroyCounter();
        HelperBean.clearDestroyedCounters();

        // no transformation invocation, no lookup
        Assertions.assertEquals("foo1",
                extension.getStaticNoTransformationInvoker().invoke(simpleBean, new Object[] { "foo", 1 }));
        Assertions.assertEquals(0, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(0, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(0, HelperBean.timesIntDestroyed);

        // no transformation, no instance lookup (configured but skipped, it is a static method)
        Assertions.assertEquals("foo1", extension.getStaticInstanceLookupInvoker().invoke(null, new Object[] { "foo", 1 }));
        Assertions.assertEquals(0, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(0, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(0, HelperBean.timesIntDestroyed);

        // no transformation, arg lookup (all)
        Assertions.assertEquals("bar42",
                extension.getStaticArgLookupInvoker().invoke(simpleBean, new Object[] { "blah", null }));
        Assertions.assertEquals(0, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(1, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(1, HelperBean.timesIntDestroyed);

        // no transformation, no instance lookup (configured but skipped) and arg lookup (all)
        Assertions.assertEquals("bar42",
                extension.getStaticLookupAllInvoker().invoke(new SimpleBean(), new Object[] { "blah", null }));
        Assertions.assertEquals(0, SimpleBean.preDestroyInvoked);
        Assertions.assertEquals(2, HelperBean.timesStringDestroyed);
        Assertions.assertEquals(2, HelperBean.timesIntDestroyed);
    }

    @Test
    public void testArgTransformingInvokableMethod() throws Exception {
        ArgTransformer.runnableExecuted = 0;

        String fooArg = "fooArg";
        String expected = fooArg + fooArg + ArgTransformer.transformed;
        Assertions.assertEquals(expected,
                extension.getArgTransformingInvoker().invoke(transformableBean, new Object[] { new FooArg(fooArg), "bar" }));
        Assertions.assertEquals(expected,
                extension.getStaticArgTransformingInvoker().invoke(null, new Object[] { new FooArg(fooArg), "bar" }));

        // transformer with Consumer<Runnable> parameter
        Assertions.assertEquals(0, ArgTransformer.runnableExecuted);
        Assertions.assertEquals(expected, extension.getArgTransformerWithConsumerInvoker().invoke(transformableBean,
                new Object[] { new FooArg(fooArg), "bar" }));
        Assertions.assertEquals(1, ArgTransformer.runnableExecuted);
        Assertions.assertEquals(expected, extension.getStaticArgTransformerWithConsumerInvoker().invoke(null,
                new Object[] { new FooArg(fooArg), "bar" }));
        Assertions.assertEquals(2, ArgTransformer.runnableExecuted);
    }

    @Test
    public void testInstanceTransformingInvokableMethod() throws Exception {
        InstanceTransformer.runnableExecuted = 0;

        //test is intentionally *NOT* using the bean, but instead passes in new instance every time
        String fooArg = "fooArg";
        String expected = (fooArg + fooArg).toUpperCase();
        TransformableBean targetInstance;

        // transformer defined on other class
        targetInstance = new TransformableBean();
        Assertions.assertFalse(targetInstance.isTransformed());
        Assertions.assertEquals(expected,
                extension.getInstanceTransformerInvoker().invoke(targetInstance, new Object[] { new FooArg(fooArg), fooArg }));
        Assertions.assertTrue(targetInstance.isTransformed());

        // transformer defined on other class with Consumer<Runnable> param
        targetInstance = new TransformableBean();
        Assertions.assertFalse(targetInstance.isTransformed());
        Assertions.assertEquals(0, InstanceTransformer.runnableExecuted);
        Assertions.assertEquals(expected, extension.getInstanceTransformerWithConsumerInvoker().invoke(targetInstance,
                new Object[] { new FooArg(fooArg), fooArg }));
        Assertions.assertEquals(1, InstanceTransformer.runnableExecuted);
        Assertions.assertTrue(targetInstance.isTransformed());

        // transformer on the bean class, no arg method
        targetInstance = new TransformableBean();
        Assertions.assertFalse(targetInstance.isTransformed());
        Assertions.assertEquals(expected, extension.getInstanceTransformerNoParamInvoker().invoke(targetInstance,
                new Object[] { new FooArg(fooArg), fooArg }));
        Assertions.assertTrue(targetInstance.isTransformed());
    }

    @Test
    public void testReturnValueTransformingInvokableMethod() throws Exception {
        String fooArg = "  fooArg  ";
        String expected = (fooArg + fooArg).strip();

        // transformer defined on other class
        Assertions.assertEquals(expected,
                extension.getReturnTransformerInvoker().invoke(transformableBean, new Object[] { new FooArg(fooArg), fooArg }));
        Assertions.assertEquals(expected, extension.getStaticReturnTransformerInvoker().invoke(transformableBean,
                new Object[] { new FooArg(fooArg), fooArg }));

        // transformer on the result class (String), no arg method
        Assertions.assertEquals(expected, extension.getReturnTransformerNoParamInvoker().invoke(transformableBean,
                new Object[] { new FooArg(fooArg), fooArg }));
        Assertions.assertEquals(expected, extension.getStaticReturnTransformerNoParamInvoker().invoke(transformableBean,
                new Object[] { new FooArg(fooArg), fooArg }));
    }

    @Test
    public void testExceptionTransformingInvokableMethod() throws Exception {
        String expected = IllegalArgumentException.class.getSimpleName();
        String expectedStatic = IllegalStateException.class.getSimpleName();

        // exception transformer can only be defined on other class
        Assertions.assertEquals(expected,
                extension.getExceptionTransformerInvoker().invoke(trulyExceptionalBean, new Object[] { "foo", 42 }));
        Assertions.assertEquals(expectedStatic,
                extension.getStaticExceptionTransformerInvoker().invoke(trulyExceptionalBean, new Object[] { "foo", 42 }));
    }

    @Test
    public void testInvocationWrapperInvokableMethod() throws Exception {
        String expected = "foo42foo42";

        // invocation wrapper defined on other class
        Assertions.assertEquals(expected,
                extension.getInvocationWrapperInvoker().invoke(simpleBean, new Object[] { "foo", 42 }));
        Assertions.assertEquals(expected,
                extension.getStaticInvocationWrapperInvoker().invoke(simpleBean, new Object[] { "foo", 42 }));
    }
}
