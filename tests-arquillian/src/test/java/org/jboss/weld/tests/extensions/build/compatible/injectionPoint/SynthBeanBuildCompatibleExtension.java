package org.jboss.weld.tests.extensions.build.compatible.injectionPoint;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.Synthesis;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.junit.Assert;

public class SynthBeanBuildCompatibleExtension implements BuildCompatibleExtension {

    @Synthesis
    public void addBeanProducer(SyntheticComponents syntheticComponents) {
        syntheticComponents.addBean(String.class).type(String.class).scope(Dependent.class).createWith(StringBeanCreator.class);
    }

    public static class StringBeanCreator implements SyntheticBeanCreator<String> {

        public String create(Instance<Object> lookup, Parameters params) {
            Instance<InjectionPoint> instance = lookup.select(InjectionPoint.class);
            Assert.assertTrue(instance.isResolvable());
            InjectionPoint ip = instance.get();
            if (ip.getBean().getBeanClass().equals(DummyBean.class)) {
                return "foo";
            } else {
                return "bar";
            }
        }

    }
}
