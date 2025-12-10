/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.weld.tests.interceptors.interceptorsOrderWithEjbInterceptorOnClass;

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
 * @author Marius Bogoevici
 */
@ExtendWith(ArquillianExtension.class)
public class InterceptorOrderTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(InterceptorOrderTest.class))
                .intercept(CdiInterceptor.class)
                .addPackage(InterceptorOrderTest.class.getPackage());
    }

    @Test
    @Tag("Integration")
    public void testOrder(Processor processor) {
        Counter.count = 0;
        SimpleProcessor.count = 0;
        CdiInterceptor.count = 0;
        EjbInterceptor.count = 0;

        int sum = processor.add(8, 13);

        Assertions.assertEquals(21, sum);
        Assertions.assertEquals(1, EjbInterceptor.count);
        Assertions.assertEquals(2, CdiInterceptor.count);
        Assertions.assertEquals(3, SimpleProcessor.count);
    }

}
