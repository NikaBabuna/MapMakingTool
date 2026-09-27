/*
 * File: product/src/main/java/com/aethelgard/product/session/diagnostics/TimingSubSystem.java
 * Purpose: Sub-System wrapper that records wall-time into PhaseTiming
 * Audience: ProductHost tectonics wiring
 * Update when: Phase id mapping changes
 */

package com.aethelgard.product.session.diagnostics;

import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import java.util.Objects;
import java.util.Set;

/** Delegates to a Sub-System and records elapsed nanos under a diagnostic phase id. */
public final class TimingSubSystem implements SubSystem {

  private final SubSystem delegate;
  private final String phaseId;

  public TimingSubSystem(SubSystem delegate, String phaseId) {
    this.delegate = Objects.requireNonNull(delegate, "delegate");
    this.phaseId = Objects.requireNonNull(phaseId, "phaseId");
  }

  @Override
  public String id() {
    return delegate.id();
  }

  @Override
  public Set<String> writeRanges() {
    return delegate.writeRanges();
  }

  @Override
  public void execute(SubSystemIo io) {
    long t0 = System.nanoTime();
    try {
      delegate.execute(io);
    } finally {
      PhaseTiming.record(phaseId, System.nanoTime() - t0);
    }
  }
}
