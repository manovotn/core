/*
 * Copyright The Weld Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.weld.environment.se.test.extension.observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ObserverMethod;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;
import jakarta.enterprise.inject.spi.ProcessObserverMethod;
import jakarta.inject.Inject;
import jakarta.inject.Qualifier;

import org.jboss.weld.bean.builtin.BeanManagerProxy;
import org.jboss.weld.bootstrap.FastProcessAnnotatedTypeResolver;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.event.GlobalObserverNotifierService;
import org.junit.Test;

/**
 * Tests resolution of extension observer type variables in the concrete extension's class hierarchy.
 *
 * @see <a href="https://github.com/weld/core/discussions/3554">Discussion 3554</a>
 */
public class GenericExtensionObserverTest {

    @Test
    public void testDifferentBindingsOfSameObserverMethod() {
        verifyDifferentBindings(false);
        verifyDifferentBindings(true);
    }

    private void verifyDifferentBindings(boolean reverse) {
        Extension[] extensions = reverse
                ? new Extension[] { new NumberMethodVariableExtension(), new StringMethodVariableExtension() }
                : new Extension[] { new StringMethodVariableExtension(), new NumberMethodVariableExtension() };
        try (WeldContainer container = new Weld().disableDiscovery().addBeanClasses(InjectedBean.class, Producers.class)
                .addExtensions(extensions).initialize()) {
            assertEquals(Arrays.asList(String.class), container.getBeanManager()
                    .getExtension(StringMethodVariableExtension.class).injectionPointTypes);
            assertEquals(Arrays.asList(Integer.class), container.getBeanManager()
                    .getExtension(NumberMethodVariableExtension.class).injectionPointTypes);
        }
    }

    @Test
    public void testMethodVariableInManagedBeanObserver() {
        try (WeldContainer container = new Weld().disableDiscovery()
                .addBeanClasses(StringObserver.class, NumberObserver.class).initialize()) {
            container.event().fire("event");
            container.event().fire(1);
            assertEquals(Arrays.asList(String.class), container.select(StringObserver.class).get().getEventTypes());
            assertEquals(Arrays.asList(Integer.class), container.select(NumberObserver.class).get().getEventTypes());
        }
    }

    @Test
    public void testConfiguredObserverPreservesClassBindings() {
        try (WeldContainer container = new Weld().disableDiscovery()
                .addBeanClasses(StringObserver.class, NumberObserver.class)
                .addExtensions(ChangePriorityExtension.class).initialize()) {
            container.event().fire("event");
            container.event().fire(1);
            assertEquals(Arrays.asList(String.class), container.select(StringObserver.class).get().getEventTypes());
            assertEquals(Arrays.asList(Integer.class), container.select(NumberObserver.class).get().getEventTypes());
        }
    }

    @Test
    public void testConfiguredObserverUsesReplacementType() {
        try (WeldContainer container = new Weld().disableDiscovery().addBeanClasses(StringObserver.class)
                .addExtensions(ChangeObservedTypeExtension.class).initialize()) {
            container.event().fire("event");
            container.event().fire(1);
            assertEquals(Arrays.asList(Integer.class), container.select(StringObserver.class).get().getEventTypes());
        }
    }

    @Test
    public void testTopLevelMethodVariableInProcessAnnotatedTypeObserver() {
        try (WeldContainer container = new Weld().disableDiscovery().addBeanClasses(InjectedBean.class, Producers.class)
                .addExtensions(TopLevelTypeExtension.class, CheckFastResolverExtension.class).initialize()) {
            assertEquals(Arrays.asList(InjectedBean.class), container.getBeanManager()
                    .getExtension(TopLevelTypeExtension.class).types);
        }
    }

    @Test
    public void testMethodVariableInProcessAnnotatedTypeObserver() {
        try (WeldContainer container = new Weld().disableDiscovery().addBeanClasses(InjectedBean.class, Producers.class)
                .addExtensions(InjectedBeanTypeExtension.class).initialize()) {
            assertEquals(Arrays.asList(InjectedBean.class), container.getBeanManager()
                    .getExtension(InjectedBeanTypeExtension.class).types);
        }
    }

    @Test
    public void testClassVariableInAnonymousInstance() {
        ClassVariableExtension<String> extension = new ClassVariableExtension<String>() {
        };
        verify(new Weld().addExtensions(extension), extension.getClass(), String.class);
    }

    @Test
    public void testMethodVariableInAnonymousInstance() {
        MethodVariableExtension<String> extension = new MethodVariableExtension<String>() {
        };
        verify(new Weld().addExtensions(extension), extension.getClass(), String.class);
    }

    @Test
    public void testClassVariableInNamedInstance() {
        verify(new Weld().addExtensions(new StringClassVariableExtension()), StringClassVariableExtension.class, String.class);
    }

    @Test
    public void testMethodVariableInNamedInstance() {
        verify(new Weld().addExtensions(new StringMethodVariableExtension()), StringMethodVariableExtension.class,
                String.class);
    }

    @Test
    public void testClassVariableInRegisteredClass() {
        verify(new Weld().addExtensions(StringClassVariableExtension.class), StringClassVariableExtension.class, String.class);
    }

    @Test
    public void testMethodVariableInRegisteredClass() {
        verify(new Weld().addExtensions(StringMethodVariableExtension.class), StringMethodVariableExtension.class,
                String.class);
    }

    @Test
    public void testMethodVariableWithNonFinalBoundInInstance() {
        verify(new Weld().addExtensions(new NumberMethodVariableExtension()), NumberMethodVariableExtension.class,
                Integer.class);
    }

    @Test
    public void testMethodVariableWithNonFinalBoundInRegisteredClass() {
        verify(new Weld().addExtensions(NumberMethodVariableExtension.class), NumberMethodVariableExtension.class,
                Integer.class);
    }

    @Test
    public void testMethodVariableWithDirectBound() {
        verify(new Weld().addExtensions(NumberBoundExtension.class), NumberBoundExtension.class, Integer.class);
    }

    @Test
    public void testErasedClassVariableInInstance() {
        // Without a subclass, the String argument exists only at the call site, not in runtime class metadata.
        verify(new Weld().addExtensions(new ClassVariableExtension<String>()), ClassVariableExtension.class,
                String.class, Integer.class);
    }

    @Test
    public void testErasedMethodVariableInInstance() {
        verify(new Weld().addExtensions(new MethodVariableExtension<String>()), MethodVariableExtension.class,
                String.class, Integer.class);
    }

    @Test
    public void testErasedClassVariableInRegisteredClass() {
        verify(new Weld().addExtensions(ClassVariableExtension.class), ClassVariableExtension.class,
                String.class, Integer.class);
    }

    @Test
    public void testErasedMethodVariableInRegisteredClass() {
        verify(new Weld().addExtensions(MethodVariableExtension.class), MethodVariableExtension.class,
                String.class, Integer.class);
    }

    private void verify(Weld weld, Class<? extends RecordingExtension> extensionClass, Type... expectedTypes) {
        try (WeldContainer container = weld.disableDiscovery().addBeanClasses(InjectedBean.class, Producers.class)
                .initialize()) {
            container.select(InjectedBean.class).get();
            RecordingExtension extension = container.getBeanManager().getExtension(extensionClass);
            assertEquals(Arrays.asList(expectedTypes), extension.injectionPointTypes);
        }
    }

    public static class TypeExtension<T> implements Extension {
        final List<Type> types = new ArrayList<>();

        <S extends T> void observe(@Observes ProcessAnnotatedType<S> event) {
            types.add(event.getAnnotatedType().getJavaClass());
        }
    }

    public static class ChangePriorityExtension implements Extension {
        void configure(@Observes ProcessObserverMethod<?, ?> event) {
            Class<?> beanClass = event.getObserverMethod().getBeanClass();
            if (beanClass.equals(StringObserver.class) || beanClass.equals(NumberObserver.class)) {
                event.configureObserverMethod().priority(100);
            }
        }
    }

    public static class ChangeObservedTypeExtension implements Extension {
        void configure(@Observes ProcessObserverMethod<?, ?> event) {
            if (event.getObserverMethod().getBeanClass().equals(StringObserver.class)) {
                event.configureObserverMethod().observedType(Integer.class);
            }
        }
    }

    public static class TopLevelExtension<T> implements Extension {
        final List<Type> types = new ArrayList<>();

        <S extends T> void observe(@Observes S event) {
            if (event instanceof ProcessAnnotatedType<?>) {
                types.add(((ProcessAnnotatedType<?>) event).getAnnotatedType().getJavaClass());
            }
        }
    }

    public static class TopLevelTypeExtension extends TopLevelExtension<ProcessAnnotatedType<InjectedBean>> {
    }

    public static class CheckFastResolverExtension implements Extension {
        void check(@Observes BeforeBeanDiscovery event, BeanManager manager) {
            List<ObserverMethod<?>> observers = new ArrayList<>();
            BeanManagerProxy.unwrap(manager).getServices().get(GlobalObserverNotifierService.class)
                    .getAllObserverMethods().forEach(observer -> {
                        if (observer.getBeanClass().equals(TopLevelTypeExtension.class)) {
                            observers.add(observer);
                        }
                    });
            assertEquals(1, observers.size());
            // A chain of type-variable bounds needs contextual matching, unavailable to the fast resolver.
            Exception failure = assertThrows(Exception.class, () -> new FastProcessAnnotatedTypeResolver(observers));
            assertEquals("UnsupportedObserverMethodException", failure.getClass().getSimpleName());
        }
    }

    public static class InjectedBeanTypeExtension extends TypeExtension<InjectedBean> {
    }

    public static class EventObserver<T> {
        private final List<Type> eventTypes = new ArrayList<>();

        <S extends T> void observe(@Observes S event) {
            eventTypes.add(event.getClass());
        }

        public List<Type> getEventTypes() {
            return eventTypes;
        }
    }

    @ApplicationScoped
    public static class StringObserver extends EventObserver<String> {
    }

    @ApplicationScoped
    public static class NumberObserver extends EventObserver<Number> {
    }

    public abstract static class RecordingExtension implements Extension {
        final List<Type> injectionPointTypes = new ArrayList<>();

        void record(ProcessInjectionPoint<?, ?> event) {
            // Assert delivery for controlled application injection points, independently of Weld's internal beans.
            if (event.getInjectionPoint().getMember().getDeclaringClass().equals(InjectedBean.class)) {
                injectionPointTypes.add(event.getInjectionPoint().getType());
            }
        }
    }

    public static class ClassVariableExtension<T> extends RecordingExtension {
        void observe(@Observes ProcessInjectionPoint<?, T> event) {
            record(event);
        }
    }

    public static class MethodVariableExtension<T> extends RecordingExtension {
        <S extends T> void observe(@Observes ProcessInjectionPoint<?, S> event) {
            record(event);
        }
    }

    public static class StringClassVariableExtension extends ClassVariableExtension<String> {
    }

    public static class StringMethodVariableExtension extends MethodVariableExtension<String> {
    }

    public static class NumberMethodVariableExtension extends MethodVariableExtension<Number> {
    }

    public static class NumberBoundExtension extends RecordingExtension {
        <S extends Number> void observe(@Observes ProcessInjectionPoint<?, S> event) {
            record(event);
        }
    }

    public static class InjectedBean {
        @Inject
        public InjectedBean(@TestValue String string, @TestValue Integer integer) {
        }
    }

    public static class Producers {
        @Produces
        @TestValue
        String string = "value";

        @Produces
        @TestValue
        Integer integer = 1;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestValue {
    }
}
