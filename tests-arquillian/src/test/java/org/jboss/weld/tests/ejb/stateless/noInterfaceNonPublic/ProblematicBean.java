/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.ejb.stateless.noInterfaceNonPublic;

import javax.ejb.Stateless;

/**
 * This is no interface view EJB bean. Only public methods are allowed to be invoked (by spec).
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@Stateless
public class ProblematicBean {
    
    String ping(){
        return ProblematicBean.class.getSimpleName();
    }
}
