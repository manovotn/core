/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.contexts.session.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Inject
    SessionScopedBean sessionScopedBean;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean get = Boolean.valueOf(req.getParameter("get"));
        boolean set = Boolean.valueOf(req.getParameter("set"));
        boolean print = Boolean.valueOf(req.getParameter("print"));

        if (set) {
            sessionScopedBean.setVersion("Version 2");
            resp.getWriter().print(req.getSession().getId());
        }

        if (get) {
            resp.getWriter().print(req.getSession().getId());
        }
        if (print) {
            resp.getWriter().print(sessionScopedBean.getVersion());
        }
    }

}



