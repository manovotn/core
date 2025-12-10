/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.tests.interceptors.lhotse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.interceptors.lhotse.fst.TDAO;
import org.jboss.weld.tests.interceptors.lhotse.fst.TxInterceptor;
import org.jboss.weld.tests.interceptors.lhotse.snd.CDAO;
import org.jboss.weld.tests.interceptors.lhotse.snd.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ExtendWith(ArquillianExtension.class)
@Tag("Integration")
public class SplitInterceptorsTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive web = ShrinkWrap
                .create(WebArchive.class, Utils.getDeploymentNameAsHash(SplitInterceptorsTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addPackage(SplitInterceptorsTest.class.getPackage());

        BeanArchive fst = ShrinkWrap.create(BeanArchive.class).intercept(TxInterceptor.class)
                .beanDiscoveryMode(BeanDiscoveryMode.ALL);
        fst.addPackage(TDAO.class.getPackage());
        web.addAsLibrary(fst);

        JavaArchive snd = ShrinkWrap.create(JavaArchive.class).addAsManifestResource(
                new BeansXml(BeanDiscoveryMode.ALL).interceptors(TxInterceptor.class), ArchivePaths.create("beans.xml"));
        snd.addPackage(CDAO.class.getPackage());
        web.addAsLibrary(snd);

        return web;
    }

    @Test
    public void testInterceptors(CDAO cdao) throws Exception {
        TxInterceptor.used = false;

        Client c = new Client();
        Assertions.assertTrue(cdao.save(c));
        Assertions.assertTrue(TxInterceptor.used);
    }
}
