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
package org.jboss.weld.tests.weld2179;

import static org.junit.Assert.assertTrue;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@RunWith(Arquillian.class)
public class PrivateObserverTest {
    
    @Deployment
    public static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class, Utils.getDeploymentNameAsHash(PrivateObserverTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addPackage(PrivateObserverTest.class.getPackage());
    }
    
    @Inject
    private Event<Payload> event;
    
    @Inject
    private Watcher watcher;
    
    @Test
    public void testPrivateObserverHasCorrectInstance() {
        watcher.doWork();
        assertTrue(Watcher.WATCHER_WORKING);
        assertTrue(Decorator.DECORATOR_INVOKED == 1);
        
        // fire event
        event.fire(new Payload());
        assertTrue(Watcher.MAGIC_NUMBER_EQUALS_TO_FIVE);
    }
}
