/*
 * File: product/src/main/java/com/aethelgard/product/session/diagnostics/PhaseTiming.java
 * Purpose: Thread-local hub binding so Sub-Systems can record phase samples
 * Audience: ProductSession / TimingSubSystem
 * Update when: Phase recording wiring changes
 */

package com.aethelgard.product.session.diagnostics;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Binds the session {@link DiagnosticsHub} for the duration of an advance so tectonics Sub-Systems
 * can record phase timings without engine changes.
 */
public final class PhaseTiming {

  private static final ThreadLocal<DiagnosticsHub> HUB = new ThreadLocal<>();

  private PhaseTiming() {}

  /** Runs {@code action} with {@code hub} visible to {@link #record(String, long)}. */
  public static <T> T withHub(DiagnosticsHub hub, Supplier<T> action) {
    Objects.requireNonNull(hub, "hub");
    Objects.requireNonNull(action, "action");
    DiagnosticsHub previous = HUB.get();
    HUB.set(hub);
    try {
      return action.get();
    } finally {
      if (previous == null) {
        HUB.remove();
      } else {
        HUB.set(previous);
      }
    }
  }

  /** Records when a hub is bound and the collector exists; otherwise no-op. */
  public static void record(String id, long value) {
    DiagnosticsHub hub = HUB.get();
    if (hub != null) {
      hub.record(id, value);
    }
  }
}
