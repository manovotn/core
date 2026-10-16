/*
 * Copyright The Weld Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.weld.event;

import java.lang.reflect.Type;
import java.util.Set;

import jakarta.enterprise.inject.spi.ObserverMethod;

import org.jboss.weld.resolution.AssignabilityRules;

/**
 * Observer matching rules which can retain the declaring class's type arguments when observer metadata is copied.
 */
public interface TypeSafeObserverMethod<T> extends ObserverMethod<T> {

    AssignabilityRules getEventTypeAssignabilityRules();

    default boolean isObservedTypeAssignableFrom(Set<? extends Type> eventTypes) {
        return getEventTypeAssignabilityRules().matches(getObservedType(), eventTypes);
    }
}
