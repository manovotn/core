/*
 * Copyright The Weld Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.weld.tests.unit.reflection.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.spi.ProcessInjectionPoint;
import jakarta.enterprise.util.TypeLiteral;

import org.jboss.weld.resolution.EventTypeAssignabilityRules;
import org.jboss.weld.util.reflection.HierarchyDiscovery;
import org.junit.Test;

public class ContextualEventTypeAssignabilityTest {

    @Test
    public void testWildcardBoundSubstitutionPreservesTypeEquality() throws Exception {
        assertResolvedType("upper", new TypeLiteral<List<? extends Number>>() {
        }.getType());
        assertResolvedType("lower", new TypeLiteral<List<? super Number>>() {
        }.getType());
        assertResolvedType("nested", new TypeLiteral<List<? extends List<Number[]>>>() {
        }.getType());
        Type unbounded = GenericObserver.class.getDeclaredField("unbounded").getGenericType();
        assertSame(unbounded, new HierarchyDiscovery(NumberObserver.class).resolveType(unbounded));
    }

    private void assertResolvedType(String field, Type expected) throws Exception {
        Type resolved = new HierarchyDiscovery(NumberObserver.class)
                .resolveType(GenericObserver.class.getDeclaredField(field).getGenericType());
        assertEquals(expected, resolved);
        assertEquals(resolved, expected);
        assertEquals(expected.hashCode(), resolved.hashCode());
    }

    @Test
    public void testChainedMethodVariables() throws Exception {
        assertMatches("chain", new TypeLiteral<ProcessInjectionPoint<?, Integer>>() {
        }.getType(), true);
        assertMatches("chain", new TypeLiteral<ProcessInjectionPoint<?, String>>() {
        }.getType(), false);
    }

    @Test
    public void testParameterizedBound() throws Exception {
        assertMatches("parameterized", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<Number>>>() {
        }.getType(), true);
        assertMatches("parameterized", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<Integer>>>() {
        }.getType(), false);
        assertMatches("parameterized", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<String>>>() {
        }.getType(), false);
    }

    @Test
    public void testWildcardInParameterizedBound() throws Exception {
        assertMatches("wildcard", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<Integer>>>() {
        }.getType(), true);
        assertMatches("wildcard", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<String>>>() {
        }.getType(), false);
    }

    @Test
    public void testLowerWildcardInParameterizedBound() throws Exception {
        assertMatches("lowerWildcard", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<Object>>>() {
        }.getType(), true);
        assertMatches("lowerWildcard", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<Integer>>>() {
        }.getType(), false);
    }

    @Test
    public void testWildcardBoundedByMethodVariable() throws Exception {
        assertMatches("observedWildcard", new TypeLiteral<ProcessInjectionPoint<?, Integer>>() {
        }.getType(), true);
        assertMatches("observedWildcard", new TypeLiteral<ProcessInjectionPoint<?, String>>() {
        }.getType(), false);
    }

    @Test
    public void testIntersectionAndRecursiveBounds() throws Exception {
        assertMatches("intersection", new TypeLiteral<ProcessInjectionPoint<?, NumericValue>>() {
        }.getType(), true);
        assertMatches("intersection", new TypeLiteral<ProcessInjectionPoint<?, TextValue>>() {
        }.getType(), false);
        assertMatches("intersection", new TypeLiteral<ProcessInjectionPoint<?, ArrayList<Number>>>() {
        }.getType(), false);
    }

    @Test
    public void testArrayComponentBound() throws Exception {
        assertMatches("array", new TypeLiteral<ProcessInjectionPoint<?, Integer[]>>() {
        }.getType(), true);
        assertMatches("array", new TypeLiteral<ProcessInjectionPoint<?, String[]>>() {
        }.getType(), false);
    }

    private void assertMatches(String method, Type eventType, boolean expected) throws Exception {
        HierarchyDiscovery hierarchy = new HierarchyDiscovery(NumberObserver.class);
        EventTypeAssignabilityRules rules = new EventTypeAssignabilityRules(hierarchy.getResolver());
        Type observedType = hierarchy.resolveType(GenericObserver.class
                .getDeclaredMethod(method, ProcessInjectionPoint.class).getGenericParameterTypes()[0]);
        if (expected) {
            assertTrue(method + " should match " + eventType, rules.matches(observedType, eventType));
        } else {
            assertFalse(method + " should not match " + eventType, rules.matches(observedType, eventType));
        }
    }

    static class GenericObserver<T> {
        List<? extends T> upper;
        List<? super T> lower;
        List<? extends List<T[]>> nested;
        List<?> unbounded;

        <U extends T, S extends U> void chain(ProcessInjectionPoint<?, S> event) {
        }

        <S extends List<T>> void parameterized(ProcessInjectionPoint<?, S> event) {
        }

        <S extends List<? extends T>> void wildcard(ProcessInjectionPoint<?, S> event) {
        }

        <S extends List<? super T>> void lowerWildcard(ProcessInjectionPoint<?, S> event) {
        }

        <S extends T> void observedWildcard(ProcessInjectionPoint<?, ? extends S> event) {
        }

        <S extends Comparable<S> & Iterable<T>> void intersection(ProcessInjectionPoint<?, S> event) {
        }

        <S extends T> void array(ProcessInjectionPoint<?, S[]> event) {
        }
    }

    static class NumberObserver extends GenericObserver<Number> {
    }

    interface NumericValue extends Comparable<NumericValue>, Iterable<Number> {
    }

    interface TextValue extends Comparable<TextValue>, Iterable<String> {
    }
}
