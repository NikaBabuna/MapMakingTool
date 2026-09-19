/*
 * File: product/src/main/java/com/aethelgard/product/DiagnosticCollector.java
 * Purpose: Pluggable named sample collector for DiagnosticsHub
 * Audience: DiagnosticsHub
 * Update when: Collector contract changes
 */

package com.aethelgard.product;

/**
 * One named metric stream. Does not affect Pool state. Hub calls {@link #record(long)} only when
 * enabled.
 */
public interface DiagnosticCollector {

  String id();

  boolean enabled();

  void setEnabled(boolean enabled);

  int capacity();

  int size();

  void record(long value);

  void clear();

  /** Latest sample, or empty if none. */
  Long latest();

  /** Oldest→newest copy of retained samples. */
  long[] samples();

  /** One-line summary for reports. */
  String summaryLine();
}
