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

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test trying to invoke non-public method in no-interface view EJB - illegal according to EJB spec.
 * Weld should throw EJB exception.
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class NoInterfaceNonPublicMethodTest {

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(NoInterfaceNonPublicMethodTest.class))
                .addPackage(NoInterfaceNonPublicMethodTest.class.getPackage());
    }

    @Inject
    SomeOtherBean bean;

    @Test
    public void testExceptionThrown() {
        try {
            bean.doSomething();
            Assertions.fail("EJBException should be thrown!");
        } catch (EJBException e) {
            // expected
        }
    }
}
