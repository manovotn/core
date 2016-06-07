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
package org.jboss.weld.tests.justtest;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.test.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@RunWith(Arquillian.class)
public class WTFTest {
    
    @Deployment
    public static WebArchive getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, Utils.getDeploymentNameAsHash(WTFTest.class, Utils.ARCHIVE_TYPE.WAR)).addPackage(WTFTest.class.getPackage());
        BeansXml beans = new BeansXml();
        beans.setBeanDiscoveryMode(BeanDiscoveryMode.ANNOTATED);
        war.addAsWebInfResource(beans, "beans.xml");
        return war;
    }

    // works
    @Inject
    @BeanBQualifier(BeanBQualifier.Type.PROD)
    BeanBInterface face;
    
    //unsatisfied deps
    @Inject
    @BeanBQualifier(BeanBQualifier.Type.PROD)
    BeanB bean;
    
    @Test
    public void testIt() throws Exception {
//        Assert.assertNotNull(bean);
        Assert.assertNotNull(face);
    }
}
