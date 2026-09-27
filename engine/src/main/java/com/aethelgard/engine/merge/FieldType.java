/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/FieldType.java
 * Purpose: Default FieldMergeType implementations
 * Audience: Agents / callers / schema wiring
 * Update when: Default merge types change (Delete Request later)
 */

package com.aethelgard.engine.merge;

import java.util.List;

/**
 * Built-in {@link FieldMergeType}s. Custom types implement {@link FieldMergeType} directly — do not
 * add enum constants for product rules.
 */
public enum FieldType implements FieldMergeType {
  /** Exactly one value wins — deterministic pick-one by system id. */
  STATIC {
    @Override
    public Object merge(Object standing, List<ProvenancedWrite> writers) {
      return TypedMerge.pickOne(writers).value();
    }
  },

  /** All conflicting Long values sum (commutative). */
  INCREMENT {
    @Override
    public Object merge(Object standing, List<ProvenancedWrite> writers) {
      long base = standing == null ? 0L : requireLong(standing, "standing");
      long sum = 0L;
      for (ProvenancedWrite w : writers) {
        sum += requireLong(w.value(), "write from " + w.systemId());
      }
      return base + sum;
    }
  },

  /** New writes are no-ops; standing Pool value kept. */
  CONSTANT {
    @Override
    public Object merge(Object standing, List<ProvenancedWrite> writers) {
      if (standing == null) {
        throw new IllegalStateException("CONSTANT field has no standing value");
      }
      return standing;
    }
  },

  /** Exactly one write valid per Step — deterministic pick-one by system id. */
  DESTRUCTIVE {
    @Override
    public Object merge(Object standing, List<ProvenancedWrite> writers) {
      return TypedMerge.pickOne(writers).value();
    }
  };

  private static long requireLong(Object value, String label) {
    if (value instanceof Long l) {
      return l;
    }
    if (value instanceof Integer i) {
      return i.longValue();
    }
    throw new IllegalArgumentException(
        "INCREMENT requires Long values; " + label + " was " + typeName(value));
  }

  private static String typeName(Object value) {
    return value == null ? "null" : value.getClass().getName();
  }
}
