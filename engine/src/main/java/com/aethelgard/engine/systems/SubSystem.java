/*
 * File: engine/src/main/java/com/aethelgard/engine/systems/SubSystem.java
 * Purpose: Modular computation block with declared write-ranges
 * Audience: Agents / tests building Systems
 * Update when: Sub-System contract changes
 */

package com.aethelgard.engine.systems;

import java.util.Set;

/**
 * Reusable block inside a System. Declares which output fields it may write.
 *
 * <p>Reads Pool snapshot / prior staging via {@link SubSystemIo}; writes only into declared ranges.
 */
public interface SubSystem {

  String id();

  /** Field names this Sub-System may write. */
  Set<String> writeRanges();

  void execute(SubSystemIo io);
}
