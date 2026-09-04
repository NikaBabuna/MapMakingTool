/*
 * File: engine/src/test/java/com/aethelgard/engine/pool/PoolStepLoopTest.java
 * Purpose: F-002 witness — Pool update, Step 0 config, advance N, determinism
 * Audience: Agents / CI
 * Update when: F-002 FRs change
 */

package com.aethelgard.engine.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PoolStepLoopTest {

  @Test
  @DisplayName("FR-1: run is created from a caller-supplied config object")
  void createFromCallerConfig() {
    EngineConfig config = new EngineConfig(10L);
    Engine engine = Engine.create(config);
    assertNotNull(engine);
    // Step 0 applied config: initial 10, one update → value 11
    assertEquals(11L, engine.settled().value());
  }

  @Test
  @DisplayName("FR-2: after create, stepIndex is 0 and Step 0 used the config")
  void stepZeroUsesConfig() {
    Engine engine = Engine.create(new EngineConfig(5L));
    assertEquals(0, engine.stepIndex());
    assertEquals(1, engine.settled().updateCount());
    assertEquals(6L, engine.settled().value());
  }

  @Test
  @DisplayName("FR-3: each Step invokes Pool update exactly once")
  void updateOncePerStep() {
    Engine engine = Engine.create(new EngineConfig(0L));
    assertEquals(1, engine.settled().updateCount());

    engine.advance();
    assertEquals(2, engine.settled().updateCount());

    engine.advance(3);
    assertEquals(5, engine.settled().updateCount());
    assertEquals(5, engine.stepIndex() + 1);
  }

  @Test
  @DisplayName("FR-4: advance N; stepIndex is last completed Step index")
  void advanceNObservesStepIndex() {
    Engine engine = Engine.create(new EngineConfig(0L));
    assertEquals(0, engine.stepIndex());

    engine.advance(4);
    assertEquals(4, engine.stepIndex());
    assertEquals(5, engine.settled().updateCount());
  }

  @Test
  @DisplayName("FR-4: negative advance is rejected")
  void negativeAdvanceRejected() {
    Engine engine = Engine.create(new EngineConfig(0L));
    assertThrows(IllegalArgumentException.class, () -> engine.advance(-1));
  }

  @Test
  @DisplayName("FR-5: settled snapshot is readable after each completed Step")
  void settledSnapshotAfterEachStep() {
    Engine engine = Engine.create(new EngineConfig(100L));
    PoolSnapshot after0 = engine.settled();
    assertEquals(101L, after0.value());
    assertEquals(1, after0.updateCount());

    engine.advance();
    PoolSnapshot after1 = engine.settled();
    assertEquals(102L, after1.value());
    assertEquals(2, after1.updateCount());
    // prior snapshot unchanged (immutable)
    assertEquals(101L, after0.value());
  }

  @Test
  @DisplayName("FR-6: same config + same N advances → same settled state")
  void trivialDeterminism() {
    EngineConfig config = new EngineConfig(7L);
    Engine a = Engine.create(config);
    a.advance(3);
    Engine b = Engine.create(config);
    b.advance(3);

    assertEquals(a.stepIndex(), b.stepIndex());
    assertEquals(a.settled(), b.settled());
  }
}
