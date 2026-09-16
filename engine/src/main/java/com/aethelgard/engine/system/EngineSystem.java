/*
 * File: engine/src/main/java/com/aethelgard/engine/system/EngineSystem.java
 * Purpose: Claiming System that runs Sub-Systems to produce OUT_SYS
 * Audience: Agents / callers / tests
 * Update when: System execution model changes
 */

package com.aethelgard.engine.system;

import com.aethelgard.engine.event.EventClaimer;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Independent System: claims by category ancestry, runs Sub-Systems, emits OUT_SYS field map.
 *
 * <p>Does not read other Systems' outputs in the same Step.
 */
public final class EngineSystem {

  private final SystemConfig config;
  private final EventClaimer claimer;

  public EngineSystem(SystemConfig config) {
    this.config = Objects.requireNonNull(config, "config");
    this.claimer = new EventClaimer(config.id(), config.assignedCategory());
  }

  public String id() {
    return config.id();
  }

  public SystemConfig config() {
    return config;
  }

  /** Claim identity used by {@link com.aethelgard.engine.event.EventClaiming}. */
  public EventClaimer claimer() {
    return claimer;
  }

  /**
   * Runs Sub-Systems against a Pool snapshot; returns OUT_SYS (field → value).
   *
   * <p>Disjoint write-ranges: registration order (outcome order-independent). Overlapping ranges:
   * {@link ConflictResolutionSubSystem} supplies deterministic order for the conflict set; remaining
   * disjoint Sub-Systems keep registration order around that.
   */
  public Map<String, Object> run(PoolSnapshot snapshot) {
    Objects.requireNonNull(snapshot, "snapshot");
    List<SubSystem> ordered = orderSubSystems(config.subSystems(), config.conflictResolver());
    Map<String, Object> staging = new LinkedHashMap<>();
    for (SubSystem sub : ordered) {
      SubSystemIo io = new SubSystemIo(snapshot, staging, sub.writeRanges());
      sub.execute(io);
    }
    return Map.copyOf(staging);
  }

  static List<SubSystem> orderSubSystems(
      List<SubSystem> subs, ConflictResolutionSubSystem resolver) {
    Set<SubSystem> overlapping = findOverlapping(subs);
    if (overlapping.isEmpty()) {
      return List.copyOf(subs);
    }
    if (resolver == null) {
      throw new IllegalStateException(
          "overlapping write-ranges require a ConflictResolutionSubSystem; conflict set="
              + overlapping.stream().map(SubSystem::id).toList());
    }
    List<SubSystem> conflictOrdered = resolver.resolveOrder(List.copyOf(overlapping));
    if (conflictOrdered.size() != overlapping.size()
        || !new HashSet<>(conflictOrdered).equals(overlapping)) {
      throw new IllegalStateException(
          "conflict resolver must return each conflicting Sub-System exactly once");
    }
    // Preserve registration order for non-conflicting; splice conflict order in place of first
    // conflict member.
    List<SubSystem> result = new ArrayList<>();
    boolean spliced = false;
    for (SubSystem sub : subs) {
      if (overlapping.contains(sub)) {
        if (!spliced) {
          result.addAll(conflictOrdered);
          spliced = true;
        }
      } else {
        result.add(sub);
      }
    }
    return List.copyOf(result);
  }

  /** Sub-Systems that share a write-range with at least one other. */
  static Set<SubSystem> findOverlapping(List<SubSystem> subs) {
    Map<String, List<SubSystem>> byField = new LinkedHashMap<>();
    for (SubSystem sub : subs) {
      for (String field : sub.writeRanges()) {
        byField.computeIfAbsent(field, k -> new ArrayList<>()).add(sub);
      }
    }
    Set<SubSystem> overlapping = new LinkedHashSet<>();
    for (List<SubSystem> writers : byField.values()) {
      if (writers.size() > 1) {
        overlapping.addAll(writers);
      }
    }
    return overlapping;
  }
}
