/*
 * File: product/src/main/java/com/aethelgard/product/DiagnosticsHub.java
 * Purpose: Controllable session diagnostics registry (G-009 / F-042)
 * Audience: ProductSession / CLI / MapController
 * Update when: Hub control / report API changes
 */

package com.aethelgard.product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Session-owned diagnostics facility: named collectors, enable/disable, bounded history. Never
 * writes Pool fields.
 */
public final class DiagnosticsHub {

  public static final int DEFAULT_CAPACITY = 64;

  private final Map<String, DiagnosticCollector> collectors = new LinkedHashMap<>();

  /** Hub with built-in advance / heap / paint collectors. */
  public static DiagnosticsHub withDefaults() {
    DiagnosticsHub hub = new DiagnosticsHub();
    hub.register(new RingDiagnosticCollector(DiagnosticIds.ADVANCE_WALL, DEFAULT_CAPACITY));
    hub.register(new RingDiagnosticCollector(DiagnosticIds.HEAP_USED, DEFAULT_CAPACITY));
    hub.register(new RingDiagnosticCollector(DiagnosticIds.HEAP_MAX, DEFAULT_CAPACITY));
    hub.register(new RingDiagnosticCollector(DiagnosticIds.PAINT_WALL, DEFAULT_CAPACITY));
    return hub;
  }

  public synchronized void register(DiagnosticCollector collector) {
    Objects.requireNonNull(collector, "collector");
    String id = collector.id();
    if (collectors.containsKey(id)) {
      throw new IllegalArgumentException("collector already registered: " + id);
    }
    collectors.put(id, collector);
  }

  public synchronized List<String> ids() {
    return List.copyOf(collectors.keySet());
  }

  public synchronized DiagnosticCollector get(String id) {
    DiagnosticCollector c = collectors.get(id);
    if (c == null) {
      throw new IllegalArgumentException("unknown collector: " + id);
    }
    return c;
  }

  public synchronized boolean has(String id) {
    return collectors.containsKey(id);
  }

  /** Record when the collector exists and is enabled; no-op if missing (host may race). */
  public synchronized void record(String id, long value) {
    DiagnosticCollector c = collectors.get(id);
    if (c != null) {
      c.record(value);
    }
  }

  public synchronized void setEnabled(String id, boolean enabled) {
    get(id).setEnabled(enabled);
  }

  public synchronized void clear(String id) {
    get(id).clear();
  }

  public synchronized void clearAll() {
    for (DiagnosticCollector c : collectors.values()) {
      c.clear();
    }
  }

  /** Multi-line summary of all collectors. */
  public synchronized String report() {
    StringBuilder sb = new StringBuilder();
    for (DiagnosticCollector c : collectors.values()) {
      sb.append(c.summaryLine()).append('\n');
    }
    return sb.toString();
  }

  /** Multi-line list: id, enabled, size/capacity. */
  public synchronized String listReport() {
    StringBuilder sb = new StringBuilder();
    for (DiagnosticCollector c : collectors.values()) {
      sb.append(c.id())
          .append(" enabled=")
          .append(c.enabled())
          .append(" n=")
          .append(c.size())
          .append('/')
          .append(c.capacity())
          .append('\n');
    }
    return sb.toString();
  }

  /** Snapshot of collector ids for tests. */
  public synchronized List<DiagnosticCollector> collectors() {
    return new ArrayList<>(collectors.values());
  }
}
