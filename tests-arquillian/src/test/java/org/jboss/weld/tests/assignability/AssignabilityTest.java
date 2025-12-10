package org.jboss.weld.tests.assignability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.util.TypeLiteral;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 */
@SuppressWarnings("serial")
@ExtendWith(ArquillianExtension.class)
public class AssignabilityTest {

    @Inject
    private BeanManager beanManager;

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(AssignabilityTest.class))
                .addPackage(AssignabilityTest.class.getPackage());
    }

    @Test
    public void testAssignability1() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Dao<Order>>() {
        }.getType());

        Assertions.assertEquals(1, beans.size());
    }

    @Test
    public void testAssignability2() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Dao<User>>() {
        }.getType());
        Assertions.assertEquals(2, beans.size());
    }

    @Test
    public void testAssignability3() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Dao<?>>() {
        }.getType());
        System.err.println("beans = " + beans);
        Assertions.assertEquals(2, beans.size());
    }

    @Test
    public void testAssignability4() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Dao<? extends Persistent>>() {
        }.getType());
        Assertions.assertEquals(2, beans.size());
    }

    @Test
    public <X extends Persistent> void testAssignability5() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Dao<X>>() {
        }.getType());
        Assertions.assertEquals(1, beans.size());
    }

    @Test
    public void testAssignability6() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Dao<? extends User>>() {
        }.getType());
        Assertions.assertEquals(2, beans.size());
    }

    @Test
    public void testAssignability7() {
        Set<Bean<?>> beans = beanManager.getBeans(Dao.class);
        Assertions.assertEquals(0, beans.size());
    }

    /*
     * A raw bean type is considered assignable to a parameterized required type if the raw types are identical and all type
     * parameters of the required type are either unbounded type variables or java.lang.Object.
     */

    @Test
    public void testAssignability8() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Animal<Object, Object, Object>>() {
        }.getType());
        assertEquals(1, beans.size());
        assertEquals("zebra", beans.iterator().next().getName());
    }

    @Test
    public <T> void testAssignability9() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Animal<Object, T, Object>>() {
        }.getType());
        assertEquals(1, beans.size());
        assertEquals("zebra", beans.iterator().next().getName());
    }

    @Test
    public <T extends Number> void testAssignability10() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Animal<Object, T, Object>>() {
        }.getType());
        assertEquals(0, beans.size());
    }

    @Test
    public void testAssignability11() {
        Set<Bean<?>> beans = beanManager.getBeans(new TypeLiteral<Animal<Object, Object, String>>() {
        }.getType());
        assertEquals(0, beans.size());
    }

    @Test
    public void testAssignability12() {
        Set<Bean<?>> beans = beanManager.getBeans(Animal.class);
        assertEquals(1, beans.size());
        assertEquals("zebra", beans.iterator().next().getName());
    }
}
