/*
 * File: engine/src/test/java/com/aethelgard/engine/TestSystems.java
 * Purpose: Builds the small systems and sub-systems the engine tests run
 * Audience: Agents / CI
 * Update when: The system or sub-system API changes
 */

package com.aethelgard.engine;

import com.aethelgard.engine.events.CategoryTree;
import com.aethelgard.engine.systems.ConflictResolutionSubSystem;
import com.aethelgard.engine.systems.EngineSystem;
import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import com.aethelgard.engine.systems.SystemConfig;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class TestSystems {

  private TestSystems() {}

  /** A sub-system with the given id and write ranges that runs {@code body}. */
  public static SubSystem sub(String id, Set<String> writeRanges, Consumer<SubSystemIo> body) {
    return new SubSystem() {
      @Override
      public String id() {
        return id;
      }

      @Override
      public Set<String> writeRanges() {
        return writeRanges;
      }

      @Override
      public void execute(SubSystemIo io) {
        body.accept(io);
      }
    };
  }

  /** A system claiming {@code category} whose one sub-system writes {@code value} to {@code field}. */
  public static EngineSystem writing(
      CategoryTree tree, String category, String systemId, String field, Object value) {
    return system(
        tree, category, systemId, null, sub("w", Set.of(field), io -> io.write(field, value)));
  }

  /** A system claiming {@code category} that runs the given sub-systems. */
  public static EngineSystem system(
      CategoryTree tree,
      String category,
      String systemId,
      ConflictResolutionSubSystem resolver,
      SubSystem... subs) {
    return new EngineSystem(new SystemConfig(systemId, tree.get(category), List.of(subs), resolver));
  }
}
