/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.extensions.annotatedType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.extensions.annotatedType.EcoFriendlyWashingMachine.EcoFriendlyWashingMachineLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("Integration")
@ExtendWith(ArquillianExtension.class)
public class AnnotatedTypeExtensionTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(AnnotatedTypeExtensionTest.class))
                .beanDiscoveryMode(BeanDiscoveryMode.ALL)
                .addPackage(AnnotatedTypeExtensionTest.class.getPackage())
                .addClass(Utils.class)
                .addAsServiceProvider(Extension.class, AnnotatedTypeExtension.class);
    }

    @Inject
    private BeanManagerImpl beanManager;

    @Test
    public void testMultipleBeansOfSameType(Laundry laundry) {
        Assertions.assertNotNull(laundry.ecoFriendlyWashingMachine);
        Assertions.assertNotNull(laundry.fastWashingMachine);
    }

    /*
     * description = "WELD-371"
     */
    @Test
    public void testAnnotationsAreOverridden() {
        Bean<WashingMachine> bean = Utils.getBean(beanManager, WashingMachine.class, EcoFriendlyWashingMachineLiteral.INSTANCE);
        Assertions.assertTrue(Utils.annotationSetMatches(bean.getQualifiers(), Any.class, EcoFriendlyWashingMachine.class));

        // Verify overriding the class structure works
        Clothes.reset();
        TumbleDryer tumbleDryer = Utils.getReference(beanManager, TumbleDryer.class);
        Bean<TumbleDryer> tumbleDryerBean = Utils.getBean(beanManager, TumbleDryer.class);
        Assertions.assertNotNull(tumbleDryer);

        Assertions.assertFalse(containsConstructor(tumbleDryerBean.getInjectionPoints(), SerialNumber.class));
        Assertions.assertTrue(containsConstructor(tumbleDryerBean.getInjectionPoints(), Clothes.class));
        Assertions.assertNull(tumbleDryer.getSerialNumber());
        Assertions.assertNotNull(tumbleDryer.getClothes());
        Assertions.assertFalse(Clothes.getInjectionPoint().getAnnotated().isAnnotationPresent(Original.class));
        AnnotatedConstructor<?> clothesConstructor = getConstructor(tumbleDryerBean.getInjectionPoints(), Clothes.class);
        Assertions.assertTrue(clothesConstructor.getParameters().get(0).isAnnotationPresent(Special.class));
        Assertions.assertFalse(clothesConstructor.getParameters().get(0).isAnnotationPresent(Original.class));

        Assertions.assertTrue(containsField(tumbleDryerBean.getInjectionPoints(), "plug"));
        Assertions.assertFalse(containsField(tumbleDryerBean.getInjectionPoints(), "coins"));
        Assertions.assertNotNull(tumbleDryer.getPlug());
        Assertions.assertNull(tumbleDryer.getCoins());

        Assertions.assertTrue(containsMethod(tumbleDryerBean.getInjectionPoints(), "setRunningTime", RunningTime.class));
        Assertions.assertFalse(containsMethod(tumbleDryerBean.getInjectionPoints(), "setHotAir", HotAir.class));
        Assertions.assertNotNull(tumbleDryer.getRunningTime());
        Assertions.assertNull(tumbleDryer.getHotAir());
        AnnotatedMethod<?> runningTimeMethod = getMethod(tumbleDryerBean.getInjectionPoints(), "setRunningTime",
                RunningTime.class);
        Assertions.assertTrue(runningTimeMethod.getParameters().get(0).isAnnotationPresent(Special.class));
        Assertions.assertFalse(runningTimeMethod.getParameters().get(0).isAnnotationPresent(Original.class));
    }

    private static boolean containsField(Set<InjectionPoint> injectionPoints, String name) {
        for (InjectionPoint ip : injectionPoints) {
            if (ip.getAnnotated() instanceof AnnotatedField<?>) {
                AnnotatedField<?> field = (AnnotatedField<?>) ip.getAnnotated();
                if (field.getJavaMember().getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsConstructor(Set<InjectionPoint> injectionPoints, Class<?>... parameters) {
        return getConstructor(injectionPoints, parameters) != null;
    }

    private static AnnotatedConstructor<?> getConstructor(Set<InjectionPoint> injectionPoints, Class<?>... parameters) {
        for (InjectionPoint ip : injectionPoints) {
            if (ip.getAnnotated() instanceof AnnotatedParameter<?>) {
                AnnotatedParameter<?> param = (AnnotatedParameter<?>) ip.getAnnotated();
                if (param.getDeclaringCallable() instanceof AnnotatedConstructor<?>) {
                    Class<?>[] parameterTypes = ((Constructor<?>) param.getDeclaringCallable().getJavaMember())
                            .getParameterTypes();
                    if (Arrays.equals(parameters, parameterTypes)) {
                        return (AnnotatedConstructor<?>) param.getDeclaringCallable();
                    }
                }
            }
        }
        return null;
    }

    private static boolean containsMethod(Set<InjectionPoint> injectionPoints, String name, Class<?>... parameters) {
        return getMethod(injectionPoints, name, parameters) != null;
    }

    private static AnnotatedMethod<?> getMethod(Set<InjectionPoint> injectionPoints, String name, Class<?>... parameters) {
        for (InjectionPoint ip : injectionPoints) {
            if (ip.getAnnotated() instanceof AnnotatedParameter<?>) {
                AnnotatedParameter<?> param = (AnnotatedParameter<?>) ip.getAnnotated();
                if (param.getDeclaringCallable() instanceof AnnotatedMethod<?>) {
                    Class<?>[] parameterTypes = ((Method) param.getDeclaringCallable().getJavaMember()).getParameterTypes();
                    String methodName = param.getDeclaringCallable().getJavaMember().getName();
                    if (Arrays.equals(parameters, parameterTypes) && methodName.equals(name)) {
                        return (AnnotatedMethod<?>) param.getDeclaringCallable();
                    }
                }
            }
        }
        return null;
    }
}
