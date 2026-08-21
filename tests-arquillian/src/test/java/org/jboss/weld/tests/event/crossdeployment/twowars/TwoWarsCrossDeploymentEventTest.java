package org.jboss.weld.tests.event.crossdeployment.twowars;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.tests.category.Integration;
import org.jboss.weld.tests.event.crossdeployment.common.Ping;
import org.jboss.weld.tests.event.crossdeployment.common.War1Observer;
import org.jboss.weld.tests.event.crossdeployment.common.War2Observer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Two independently deployed WARs, each bundling its own copy of the (otherwise shared) event type and an application
 * scoped observer of it. This mirrors the scenario from issue #3484 where "both war-files contain a shared library".
 * <p>
 * Each top-level WAR deployment bootstraps its own Weld container (its own {@code contextId}, root
 * {@code BeanManagerImpl} and global observer notifier), so the two are fully isolated. An event fired from the first WAR
 * can only reach observers of that same deployment; the observer in the second WAR is never notified.
 *
 * @see org.jboss.weld.tests.event.crossdeployment.ear.EarCrossDeploymentEventTest for the single-EAR counterpart
 */
@RunWith(Arquillian.class)
@Category(Integration.class)
public class TwoWarsCrossDeploymentEventTest {

    @Deployment(name = "war1", order = 1)
    public static WebArchive war1() {
        return ShrinkWrap.create(WebArchive.class, "war1.war")
                .addClasses(Ping.class, War1Observer.class, TwoWarsCrossDeploymentEventTest.class)
                .addAsWebInfResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml");
    }

    @Deployment(name = "war2", order = 2, testable = false)
    public static WebArchive war2() {
        return ShrinkWrap.create(WebArchive.class, "war2.war").addClasses(Ping.class, War2Observer.class)
                .addAsWebInfResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml");
    }

    @Inject
    Event<Ping> event;

    @Test
    @OperateOnDeployment("war1")
    public void testEventIsNotDeliveredToObserverInSeparateWar() {
        Ping ping = new Ping();
        event.fire(ping);
        Assert.assertEquals("Event must not cross a separate WAR deployment boundary: " + ping.getVisitedBy(), 1,
                ping.getVisitedBy().size());
        Assert.assertTrue(ping.getVisitedBy().contains("war1"));
        Assert.assertFalse(ping.getVisitedBy().contains("war2"));
    }
}
