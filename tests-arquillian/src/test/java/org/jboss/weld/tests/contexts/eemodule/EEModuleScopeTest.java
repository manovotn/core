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
package org.jboss.weld.tests.contexts.eemodule;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.Integration;
import org.jboss.weld.tests.specialization.modular.EarSpecializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@RunWith(Arquillian.class)
@Category(Integration.class)
public class EEModuleScopeTest {
    
    @Deployment
    public static EnterpriseArchive getDeployment() {
        WebArchive war1 = ShrinkWrap.create(WebArchive.class, "first.war").addClasses(FirstObserver.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        WebArchive war2 = ShrinkWrap.create(WebArchive.class, "second.war").addClasses(SecondObserver.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        JavaArchive lib = ShrinkWrap.create(BeanArchive.class).addClasses(EEScopedBean.class, EventRepository.class, AbstractObserver.class, EEModuleScopeTest.class);
        return ShrinkWrap.create(EnterpriseArchive.class, Utils.getDeploymentNameAsHash(EarSpecializationTest.class, Utils.ARCHIVE_TYPE.EAR)).addAsModules(war1, war2).addAsLibraries(lib);
    }
    
    @Test
    public void testBothBeansAreDifferent() {
        // it is a set -> if they are different, it simply has to have two elements
        Assert.assertEquals(2, EventRepository.STORED_VALUES.size());
    }
}
