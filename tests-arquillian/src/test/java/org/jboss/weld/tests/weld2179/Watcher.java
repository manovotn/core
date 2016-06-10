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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@ApplicationScoped
public class Watcher implements CommonInterface{

    public static boolean MAGIC_NUMBER_EQUALS_TO_FIVE = false;
    public static boolean WATCHER_WORKING = false;
    
    private int magicNumber = 5;
    
    @Override
    public void doWork() {
        WATCHER_WORKING = true;
        // do nothing
    }
    
    // private shoul fail, protected should work
    private void observer (@Observes Payload payload) {
        //in SE you cannot see the same number here
        if (this.magicNumber == 5) {
            MAGIC_NUMBER_EQUALS_TO_FIVE = true;
        }
    }
    
}
