package org.jboss.weld.tests.enterprise.lifecycle;

import static org.jboss.weld.test.util.Utils.getActiveContext;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.test.util.Utils;

@WebServlet("/")
public class RemoteClient extends HttpServlet {

    @Inject
    BeanManagerImpl beanManager;

    @Inject
    GrossStadt frankfurt;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        // return on empty path request
        if (pathInfo == null)
            return;

        try {
            RequestContext requestContext = getActiveContext(beanManager, RequestContext.class);
            Bean<KleinStadt> stadtBean = Utils.getBean(beanManager, KleinStadt.class);
            if (pathInfo.equals("/request1")) {
                assertNotNull(stadtBean, "Expected a bean for stateful session bean Kassel");
                CreationalContext<KleinStadt> creationalContext = beanManager.createCreationalContext(stadtBean);
                KleinStadt kassel = requestContext.get(stadtBean, creationalContext);
                stadtBean.destroy(kassel, creationalContext);

                assertTrue(frankfurt.isKleinStadtDestroyed(), "Expected SFSB bean to be destroyed");
                return;
            } else if (pathInfo.equals("/request2")) {
                KleinStadt kassel = requestContext.get(stadtBean);
                assertNull(kassel, "SFSB bean should not exist after being destroyed");
                return;
            }
        } catch (AssertionError e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
