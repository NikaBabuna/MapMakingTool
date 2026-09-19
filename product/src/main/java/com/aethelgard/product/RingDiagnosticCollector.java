/*
 * File: product/src/main/java/com/aethelgard/product/RingDiagnosticCollector.java
 * Purpose: Bounded ring-buffer diagnostic collector
 * Audience: DiagnosticsHub
 * Update when: Buffer / summary format changes
 */

package com.aethelgard.product;

import java.util.Objects;

/** Fixed-capacity ring of {@code long} samples. */
public final class RingDiagnosticCollector implements DiagnosticCollector {

  private final String id;
  private final long[] ring;
  private boolean enabled = true;
  private int size;
  private int next;

  public RingDiagnosticCollector(String id, int capacity) {
    this.id = Objects.requireNonNull(id, "id");
    if (capacity < 1) {
      throw new IllegalArgumentException("capacity must be >= 1, was " + capacity);
    }
    this.ring = new long[capacity];
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public synchronized boolean enabled() {
    return enabled;
  }

  @Override
  public synchronized void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public int capacity() {
    return ring.length;
  }

  @Override
  public synchronized int size() {
    return size;
  }

  @Override
  public synchronized void record(long value) {
    if (!enabled) {
      return;
    }
    ring[next] = value;
    next = (next + 1) % ring.length;
    if (size < ring.length) {
      size++;
    }
  }

  @Override
  public synchronized void clear() {
    size = 0;
    next = 0;
  }

  @Override
  public synchronized Long latest() {
    if (size == 0) {
      return null;
    }
    int idx = Math.floorMod(next - 1, ring.length);
    return ring[idx];
  }

  @Override
  public synchronized long[] samples() {
    long[] out = new long[size];
    int start = size < ring.length ? 0 : next;
    for (int i = 0; i < size; i++) {
      out[i] = ring[(start + i) % ring.length];
    }
    return out;
  }

  @Override
  public synchronized String summaryLine() {
    Long last = latest();
    return id
        + " enabled="
        + enabled
        + " n="
        + size
        + "/"
        + ring.length
        + " last="
        + (last == null ? "-" : last);
  }
}
