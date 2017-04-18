/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.util.bytecode;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.jboss.classfilewriter.ClassFile;
import org.jboss.weld.dummy.DummyClass;

/**
 * Utility class for loading a ClassFile into a classloader. This borrows
 * heavily from javassist
 *
 * @author Stuart Douglas
 */
public class ClassFileUtils {
    //    private static java.lang.reflect.Method defineClass1;
    private static MethodHandles.Lookup lookup;

    private ClassFileUtils() {
    }

    static {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    //                    Class<?> cl = Class.forName("java.lang.invoke.MethodHandles.Lookup");
                    //                    final String name = "defineClass";

                    lookup = MethodHandles.publicLookup();
                    //                    defineClass1 = cl.getDeclaredMethod(name, new Class[] { byte[].class });
                    //                    defineClass1.setAccessible(true);
                    //
                    //                    defineClass2 = cl.getDeclaredMethod(name, new Class[]{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
                    //                    defineClass2.setAccessible(true);
                    return null;
                }
            });
        } catch (PrivilegedActionException pae) {
            throw new RuntimeException("cannot initialize ClassPool", pae.getException());
        }
    }

    /**
     * Converts the class to a <code>java.lang.Class</code> object. Once this
     * method is called, further modifications are not allowed any more.
     * <p>
     * <p>
     * The class file represented by the given <code>CtClass</code> is loaded by
     * the given class loader to construct a <code>java.lang.Class</code> object.
     * Since a private method on the class loader is invoked through the
     * reflection API, the caller must have permissions to do that.
     * <p>
     * <p>
     * An easy way to obtain <code>ProtectionDomain</code> object is to call
     * <code>getProtectionDomain()</code> in <code>java.lang.Class</code>. It
     * returns the domain that the class belongs to.
     * <p>
     * <p>
     * This method is provided for convenience. If you need more complex
     * functionality, you should write your own class loader.
     *
     * @param loader the class loader used to load this class. For example, the
     *               loader returned by <code>getClassLoader()</code> can be used for
     *               this parameter.
     * @param domain the protection domain for the class. If it is null, the
     *               default domain created by <code>java.lang.ClassLoader</code> is
     */
    public static Class<?> toClass(ClassFile ct, ClassLoader loader, Class<?> beanType) {
        try {
            byte[] b = ct.toBytecode();
            java.lang.reflect.Method method;
            Object[] args;
            //if (domain == null) {
            //            method = defineClass1;
            args = new Object[] { b };
            //            } else {
            //                method = defineClass2;
            //                args = new Object[]{ct.getName(), b, 0, b.length, domain};
            //            }
            
            // NOTE!!! We entirely IGNORE given classloader - we are not supposed to make use of it, instead we go via MethodHandles.Lookup
            System.out.println("BEAN TYPE CLASS " + beanType.getPackageName() + " " + ct.getName());

            MethodHandles.Lookup in = MethodHandles.privateLookupIn(beanType, MethodHandles.lookup()).dropLookupMode(MethodHandles.Lookup.PRIVATE);

            return in.defineClass(b);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
//            return null;
        }
    }

    private static synchronized Class<?> toClass2(Method method, ClassLoader loader, Object[] args) throws Exception {
        Class<?> clazz = Class.class.cast(method.invoke(loader, args));
        return clazz;
    }

}
