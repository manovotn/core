/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.specialization.weld2241;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@RunWith(Arquillian.class)
public class SpecialSpecializationTest {
    
    @Deployment
    public static Archive<?> createArchive() {
        return ShrinkWrap.create(WebArchive.class)
            .addAsWebInfResource(SpecialSpecializationTest.class.getPackage(), "beans.xml", "beans.xml")
            .addPackage(SpecialSpecializationTest.class.getPackage());
    }

    // withut any code changes, this will be AMBIGUOUS
    // SpecializedFoo and FooImpl both fit here
    @Inject
    AbstractFoo foo;
    
    @Test
    public void testGenericBeanSpecialization(){
        assertNotNull(foo);
    }
}
