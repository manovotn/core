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
package org.jboss.weld.tests.event.async.context.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutionException;

import jakarta.ejb.EJBAccessException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Testcase for WELD-1977
 *
 * @author Jozef Hartinger
 *
 */
@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class SecurityContextPropagationTest {

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SecurityContextPropagationTest.class))
                .addPackage(SecurityContextPropagationTest.class.getPackage());
    }

    @Test
    public void testPositive(Student student) throws InterruptedException, ExecutionException {
        Spreadsheet spreadsheet = new Spreadsheet();
        assertEquals(spreadsheet, student.print(spreadsheet));
    }

    @Test
    public void testNegative(Stranger stranger) throws InterruptedException, ExecutionException {
        try {
            stranger.print(new Spreadsheet());
            fail();
        } catch (ExecutionException expected) {
            // CompletableFuture unwraps CompletionException automatically
            assertTrue(expected.getCause() instanceof EJBAccessException);
        }
    }
}
