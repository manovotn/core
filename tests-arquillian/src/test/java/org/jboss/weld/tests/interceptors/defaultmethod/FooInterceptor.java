package org.jboss.weld.tests.interceptors.defaultmethod;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@FooInterceptorBinding
@Interceptor
@Priority(1000)
public class FooInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        return true;
    }
}
