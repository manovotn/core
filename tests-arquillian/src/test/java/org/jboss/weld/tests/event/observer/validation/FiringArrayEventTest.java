package org.jboss.weld.tests.event.observer.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.ObserverMethod;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:borisha.zivkovic@gmail.com">Borisa Zivkovic</a>
 */
@ExtendWith(ArquillianExtension.class)
public class FiringArrayEventTest {

    @Inject
    private BeanManager manager;

    @Inject
    private ArrayObserverBean observerBean;

    @Inject
    private StringListObserverBean stringListObserverBean;

    @Inject
    private StringListArrayObserverBean stringListArrayObserverBean;

    @Inject
    private Event<int[]> arrayEvent;

    @Inject
    private Event<List<String>> stringListEvent;

    @Inject
    private Event<ArrayList<String>[]> stringListArrayEvent;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "weld_events.jar")
                .addAsManifestResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml")
                .addClasses(ArrayObserverBean.class, StringListObserverBean.class, StringListArrayObserverBean.class);
    }

    @BeforeEach
    public void setup() {
        this.observerBean.reset();
    }

    @Test
    public void testResolverArray() {

        final Set<ObserverMethod<? super int[]>> observers = this.manager.resolveObserverMethods(new int[] {});

        Assertions.assertEquals(1, observers.size(), "should have one observer");
        Assertions.assertFalse(this.observerBean.isReceivedUpdate(), "should have not received update");
        Assertions.assertNull(this.observerBean.getData(), "should have not received update");

        int[] data = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };

        for (final ObserverMethod<? super int[]> observer : observers) {
            observer.notify(data);
        }

        Assertions.assertTrue(this.observerBean.isReceivedUpdate(), "should have received update");
        Assertions.assertArrayEquals(this.observerBean.getData(), data, "should have received update");

    }

    @Test
    public void testEventArray() {

        Assertions.assertFalse(this.observerBean.isReceivedUpdate(), "should have not received update");
        Assertions.assertNull(this.observerBean.getData(), "should have not received update");

        int[] data = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };

        this.arrayEvent.fire(data);
        // should not fail, this test should behave same as test_resolver_array()
        Assertions.assertTrue(this.observerBean.isReceivedUpdate(), "should have received update");
        Assertions.assertArrayEquals(this.observerBean.getData(), data, "should have received update");

    }

    @Test
    public void testStringListEvent() {

        Assertions.assertFalse(this.stringListObserverBean.isReceivedUpdate(), "should have not received update");
        Assertions.assertNull(this.stringListObserverBean.getData(), "should have not received update");

        ArrayList<String> data = new ArrayList<String>();

        this.stringListEvent.fire(data);
        // should not fail, this test should behave same as test_resolver_array()
        Assertions.assertTrue(this.stringListObserverBean.isReceivedUpdate(), "should have received update");
        Assertions.assertEquals(this.stringListObserverBean.getData(), data, "should have received update");
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testStringArrayListArrayEvent() {

        stringListArrayObserverBean.reset();

        ArrayList<String>[] data = new ArrayList[0];

        this.stringListArrayEvent.fire(data);
        // should not fail, this test should behave same as test_resolver_array()
        Assertions.assertTrue(this.stringListArrayObserverBean.isReceivedUpdate(), "should have received update");
        Assertions.assertArrayEquals(this.stringListArrayObserverBean.getData(), data, "should have received update");
    }

}
