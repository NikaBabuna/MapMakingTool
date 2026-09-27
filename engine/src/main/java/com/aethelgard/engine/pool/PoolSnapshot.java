/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/PoolSnapshot.java
 * Purpose: Immutable settled Pool state after a completed Step
 * Audience: Agents / callers / tests
 * Update when: Observable Pool fields change
 */

package com.aethelgard.engine.pool;

import java.util.Map;
import java.util.Objects;

/**
 * Settled view of the Pool after a Step completes. Callers never observe a half-applied Step.
 *
 * @param value current trivial Pool heartbeat value
 * @param updateCount how many times {@link Pool#update} has run (equals completed Steps)
 * @param fields typed field map after merge apply (empty when no schema); values are {@link Object}
 */
public record PoolSnapshot(long value, int updateCount, Map<String, Object> fields) {

  public PoolSnapshot {
    fields = Map.copyOf(Objects.requireNonNullElse(fields, Map.of()));
  }

  /** Convenience for snapshots with no typed fields. */
  public PoolSnapshot(long value, int updateCount) {
    this(value, updateCount, Map.of());
  }

  public Object field(String name) {
    Object v = fields.get(name);
    if (v == null) {
      throw new IllegalArgumentException("unknown or unset field: " + name);
    }
    return v;
  }

  /** Long field accessor; fails if missing or not a {@link Long}. */
  public long fieldLong(String name) {
    Object v = field(name);
    if (v instanceof Long l) {
      return l;
    }
    throw new IllegalArgumentException(
        "field '" + name + "' is not a Long: " + v.getClass().getName());
  }

  public long fieldOrZero(String name) {
    Object v = fields.get(name);
    if (v == null) {
      return 0L;
    }
    if (v instanceof Long l) {
      return l;
    }
    throw new IllegalArgumentException(
        "fieldOrZero requires Long; '" + name + "' was " + v.getClass().getName());
  }
}
