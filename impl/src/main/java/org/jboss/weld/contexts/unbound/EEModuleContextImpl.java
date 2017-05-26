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
package org.jboss.weld.contexts.unbound;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.jboss.weld.bean.RIBean;
import org.jboss.weld.bootstrap.BeanDeploymentModules;
import org.jboss.weld.manager.BeanManagerImpl;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class EEModuleContextImpl implements EEModuleContext {

//    private final ThreadLocal<Map<Contextual<?>, ContextualInstance<?>>> currentContext = new ThreadLocal<>();
    // String is a BeanManager's ID which them maps to instances created by that module
    private Map<String, Map<Contextual<?>, ContextualInstance<?>>> contextMap = new HashMap<>();

    private BeanDeploymentModules bdms;

    @Override
    public void destroy(Contextual<?> contextual) {
        Map<Contextual<?>, ContextualInstance<?>> ctx = contextMap.get(contextualToModuleId(contextual));
        if (ctx == null) {
            return;
        }
        ctx.remove(contextual);
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        // use this to get proper BM -> ((RIBean) contextual).getBeanManager()
        // will only work for "our" beans, not custom
        String contextualBmId = contextualToModuleId(contextual);
        System.out.println("This Module's ID is: " + contextualBmId);
        Map<Contextual<?>, ContextualInstance<?>> ctx = contextMap.get(contextualBmId);
        if (ctx == null) {
            contextMap.put(contextualToModuleId(contextual), new HashMap<>());
            ctx = contextMap.get(contextualBmId);
        }

        ContextualInstance<T> instance = (ContextualInstance<T>) ctx.get(contextual);

        if (instance == null && creationalContext != null) {
            instance = new ContextualInstance<>(contextual.create(creationalContext), creationalContext, contextual);
            ctx.put(contextual, instance);
        }
        System.out.println("EEModuleContext#get invoked!!");
        System.out.println("Map has this many keys: ");
        System.out.println(ctx == null ? 0 : ctx.keySet().size());
        return instance != null ? instance.get() : null;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return EEModuleScoped.class;
    }

    @Override
    public void invalidate() {
        // this should probably operate globally, e.g. destroy all existing maps
        for (Map<Contextual<?>, ContextualInstance<?>> ctx : contextMap.values()) {
            if (ctx == null) {
                return;
            }
            for (ContextualInstance<?> ctxInstance : ctx.values()) {
                ctxInstance.destroy();
            }
            ctx.clear();
        }
        contextMap.clear();
    }

    @Override
    public boolean isActive() {
        // TODO, probably stays active all the time as AppScope
        return true;
    }

    private String contextualToModuleId(Contextual contextual) {
        BeanManagerImpl bmImpl = ((RIBean) contextual).getBeanManager();
        if (bdms == null) {
            bdms = bmImpl.getServices().get(BeanDeploymentModules.class);
        }
        return bdms.getModule(bmImpl).getId();
    }

    static final class ContextualInstance<T> {

        private final T value;

        private final CreationalContext<T> creationalContext;

        private final Contextual<T> contextual;

        /**
         *
         * @param instance
         * @param creationalContext
         * @param contextual
         */
        ContextualInstance(T instance, CreationalContext<T> creationalContext, Contextual<T> contextual) {
            this.value = instance;
            this.creationalContext = creationalContext;
            this.contextual = contextual;
        }

        T get() {
            return value;
        }

        Contextual<T> getContextual() {
            return contextual;
        }

        void destroy() {
            contextual.destroy(value, creationalContext);
        }

    }

}
