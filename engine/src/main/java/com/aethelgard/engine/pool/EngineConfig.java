/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EngineConfig.java
 * Purpose: Caller-supplied Step 0 seed for the Pool (ADR-005)
 * Audience: Agents / callers (tests, later CLI/UI)
 * Update when: Config shape for bootstrap changes
 */

package com.aethelgard.engine.pool;

/**
 * Starting configuration for a run. Sole seed for Step 0 — no hidden globals.
 *
 * @param initialValue trivial Pool seed used by the F-002 heartbeat (domain fields come later)
 */
public record EngineConfig(long initialValue) {}
