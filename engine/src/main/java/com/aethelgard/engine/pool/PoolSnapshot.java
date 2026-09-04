/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/PoolSnapshot.java
 * Purpose: Immutable settled Pool state after a completed Step
 * Audience: Agents / callers / tests
 * Update when: Observable Pool fields change
 */

package com.aethelgard.engine.pool;

/**
 * Settled view of the Pool after a Step completes. Callers never observe a half-applied Step.
 *
 * @param value current trivial Pool value
 * @param updateCount how many times {@link Pool#update()} has run (equals completed Steps)
 */
public record PoolSnapshot(long value, int updateCount) {}
