/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.executor;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.security.GetContextClassLoaderAction;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
final class SecurityActions {

    private SecurityActions() {

    }

    static ClassLoader getTCCL() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(GetContextClassLoaderAction.INSTANCE);
        } else {
            return GetContextClassLoaderAction.INSTANCE.run();
        }
    }

    static void setTCCL(ClassLoader cl) {
        if (System.getSecurityManager() != null) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
                    Thread.currentThread().setContextClassLoader(cl);
                    return null;
                });
            } catch (PrivilegedActionException e) {
                throw new WeldException(e.getCause());
            }
        } else {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
}
