/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.resolution;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.jboss.weld.logging.ReflectionLogger;
import org.jboss.weld.util.Types;
import org.jboss.weld.util.reflection.HierarchyDiscovery;
import org.jboss.weld.util.reflection.Reflections;
import org.jboss.weld.util.reflection.TypeResolver;

/**
 * This class implements Section 10.3.1 of the CDI specification.
 *
 * @author Jozef Hartinger
 *
 */
public class EventTypeAssignabilityRules extends AbstractAssignabilityRules {

    private static final AssignabilityRules INSTANCE = new EventTypeAssignabilityRules();

    public static AssignabilityRules instance() {
        return INSTANCE;
    }

    private final TypeResolver typeResolver;

    private EventTypeAssignabilityRules() {
        this.typeResolver = null;
    }

    /**
     * Creates observer matching rules which resolve variable bounds in the declaring bean's class hierarchy.
     * The resolver substitutes class variables, while method variables retain their upper-bound semantics.
     */
    public EventTypeAssignabilityRules(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    private Type[] getObservedBounds(TypeVariable<?> variable) {
        if (typeResolver == null) {
            return getUppermostTypeVariableBounds(variable);
        }
        Type resolved = typeResolver.resolveType(variable);
        if (!(resolved instanceof TypeVariable<?>)) {
            return new Type[] { resolved };
        }
        Type[] bounds = ((TypeVariable<?>) resolved).getBounds();
        return resolveObservedBounds(bounds);
    }

    private Type[] resolveObservedBounds(Type[] bounds) {
        if (bounds.length == 0) {
            return bounds;
        }
        if (bounds[0] instanceof TypeVariable<?>) {
            return getObservedBounds((TypeVariable<?>) bounds[0]);
        }
        Type[] resolvedBounds = new Type[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            resolvedBounds[i] = typeResolver.resolveType(bounds[i]);
        }
        return resolvedBounds;
    }

    @Override
    protected boolean boundsMatch(Type[] upperBounds, Type[] stricterUpperBounds) {
        if (typeResolver == null) {
            return super.boundsMatch(upperBounds, stricterUpperBounds);
        }
        return super.boundsMatch(resolveObservedBounds(upperBounds), resolveObservedBounds(stricterUpperBounds));
    }

    @Override
    public boolean matches(Type observedType, Type eventType) {
        return matchesNoBoxing(Types.boxedType(observedType), Types.boxedType(eventType));
    }

    public boolean matchesNoBoxing(Type observedType, Type eventType) {
        /*
         * Special handling for array event types as eventType closure does not contain the type closure of array component type
         * this is here for backwards compatibility - see
         * ObserverMethodWithParametertizedTypeTest.testObserverMethodCanObserveArrayWildcard()
         */
        if (Types.isArray(observedType) && Types.isArray(eventType)) {
            final Type observedComponentType = Types.getArrayComponentType(observedType);
            for (Type type : new HierarchyDiscovery(Types.getArrayComponentType(eventType)).getTypeClosure()) {
                if (matchesNoBoxing(observedComponentType, type)) {
                    return true;
                }
            }
            return false;
        }

        if (observedType instanceof TypeVariable<?>) {
            /*
             * An event type is considered assignable to a type variable if the event type is assignable to the
             * upper bound, if any.
             */
            return matches((TypeVariable<?>) observedType, eventType);
        }
        if (observedType instanceof Class<?> && eventType instanceof ParameterizedType) {
            /*
             * A parameterized event type is considered assignable to a raw observed event type if the raw
             * types are identical.
             */
            return observedType.equals(Reflections.getRawType(eventType));
        }
        if (observedType instanceof ParameterizedType && eventType instanceof ParameterizedType) {
            /*
             * A parameterized event type is considered assignable to a parameterized observed event type if
             * they have identical raw type and for each parameter:
             */
            return matches((ParameterizedType) observedType, (ParameterizedType) eventType);
        }
        /*
         * Not explicitly said in the spec but obvious.
         */
        if (observedType instanceof Class<?> && eventType instanceof Class<?>) {
            return observedType.equals(eventType);
        }
        return false;
    }

    private boolean matches(TypeVariable<?> observedType, Type eventType) {
        for (Type bound : getObservedBounds(observedType)) {
            if (!CovariantTypes.isAssignableFrom(bound, eventType)) {
                return false;
            }
        }
        return true;
    }

    private boolean matches(ParameterizedType observedType, ParameterizedType eventType) {
        if (!observedType.getRawType().equals(eventType.getRawType())) {
            return false;
        }
        if (observedType.getActualTypeArguments().length != eventType.getActualTypeArguments().length) {
            throw ReflectionLogger.LOG.invalidTypeArgumentCombination(observedType, eventType);
        }
        for (int i = 0; i < observedType.getActualTypeArguments().length; i++) {
            if (!parametersMatch(observedType.getActualTypeArguments()[i], eventType.getActualTypeArguments()[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * A parameterized event type is considered assignable to a parameterized observed event type if
     * they have identical raw type and for each parameter:
     */
    private boolean parametersMatch(Type observedParameter, Type eventParameter) {
        if (Types.isActualType(observedParameter) && Types.isActualType(eventParameter)) {
            /*
             * the observed event type parameter is an actual type with identical raw type to the event type
             * parameter, and, if the type is parameterized, the event type parameter is assignable to the
             * observed event type parameter according to these rules, or
             */
            return matches(observedParameter, eventParameter);
        }
        if (observedParameter instanceof WildcardType && eventParameter instanceof WildcardType) {
            /*
             * both the observed event type parameter and the event type parameter are wildcards, and the event type parameter
             * is assignable to the observed event
             * type
             */
            return CovariantTypes.isAssignableFrom(observedParameter, eventParameter);
        }
        if (observedParameter instanceof WildcardType) {
            /*
             * the observed event type parameter is a wildcard and the event type parameter is assignable
             * to the upper bound, if any, of the wildcard and assignable from the lower bound, if any, of the
             * wildcard, or
             */
            return parametersMatch((WildcardType) observedParameter, eventParameter);
        }
        if (observedParameter instanceof TypeVariable<?>) {
            /*
             * the observed event type parameter is a type variable and the event type parameter is assignable
             * to the upper bound, if any, of the type variable.
             */
            return parametersMatch((TypeVariable<?>) observedParameter, eventParameter);
        }
        return false;
    }

    private boolean parametersMatch(TypeVariable<?> observedParameter, Type eventParameter) {
        for (Type bound : getObservedBounds(observedParameter)) {
            if (!CovariantTypes.isAssignableFrom(bound, eventParameter)) {
                return false;
            }
        }
        return true;
    }

    private boolean parametersMatch(WildcardType observedParameter, Type eventParameter) {
        return (lowerBoundsOfWildcardMatch(eventParameter, observedParameter)
                && upperBoundsOfWildcardMatch(observedParameter, eventParameter));

    }
}
