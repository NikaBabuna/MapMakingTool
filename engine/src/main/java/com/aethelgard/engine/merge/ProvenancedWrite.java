/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/ProvenancedWrite.java
 * Purpose: System-attributed field write for typed merge
 * Audience: Agents implementing merge
 * Update when: Provenance shape changes
 */

package com.aethelgard.engine.merge;

import java.util.Objects;

/**
 * One System's contribution to a field in the Step output buffer.
 *
 * @param systemId producing System id (provenance)
 * @param value written value
 */
public record ProvenancedWrite(String systemId, long value) {

  public ProvenancedWrite {
    Objects.requireNonNull(systemId, "systemId");
  }
}
