package org.jboss.weld.tests.event.crossdeployment.ear;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.Integration;
import org.jboss.weld.tests.event.crossdeployment.common.Ping;
import org.jboss.weld.tests.event.crossdeployment.common.War1Observer;
import org.jboss.weld.tests.event.crossdeployment.common.War2Observer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Two web modules packaged as a single EAR share the event type from the EAR library. Each module holds an application
 * scoped observer of that event.
 * <p>
 * The two WARs form a single Weld deployment (a single {@code contextId} / {@code Container}), and a plain
 * {@code Event#fire(Object)} is dispatched through the deployment-wide global observer notifier, which is not restricted
 * by bean archive accessibility. Therefore an event fired from the first WAR is also delivered to the observer in the
 * second WAR.
 *
 * @see org.jboss.weld.tests.event.crossdeployment.twowars.TwoWarsCrossDeploymentEventTest for the isolated counterpart
 */
@RunWith(Arquillian.class)
@Category(Integration.class)
public class EarCrossDeploymentEventTest {

    @Deployment
    public static EnterpriseArchive deploy() {
        // shared library placed in EAR/lib - contains only the event type, visible to both modules as a single class
        JavaArchive sharedLibrary = ShrinkWrap.create(JavaArchive.class, "shared-events.jar").addClass(Ping.class);

        WebArchive war1 = Testable.archiveToTest(ShrinkWrap.create(WebArchive.class, "war1.war")
                .addClasses(War1Observer.class, EarCrossDeploymentEventTest.class)
                .addAsWebInfResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml"));

        WebArchive war2 = ShrinkWrap.create(WebArchive.class, "war2.war").addClass(War2Observer.class)
                .addAsWebInfResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml");

        return ShrinkWrap
                .create(EnterpriseArchive.class,
                        Utils.getDeploymentNameAsHash(EarCrossDeploymentEventTest.class, Utils.ARCHIVE_TYPE.EAR))
                .addAsModules(war1, war2).addAsLibrary(sharedLibrary);
    }

    @Inject
    Event<Ping> event;

    @Test
    public void testEventIsDeliveredToObserverInAnotherWarOfSameEar() {
        Ping ping = new Ping();
        event.fire(ping);
        Assert.assertEquals("Event should reach observers in both modules of the EAR: " + ping.getVisitedBy(), 2,
                ping.getVisitedBy().size());
        Assert.assertTrue(ping.getVisitedBy().contains("war1"));
        Assert.assertTrue(ping.getVisitedBy().contains("war2"));
    }
}
