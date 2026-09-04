/*
 * File: engine/src/main/java/com/aethelgard/engine/user/RecordingUserView.java
 * Purpose: Test sink that records settled snapshots without UI toolkits
 * Audience: Tests
 * Update when: Recording shape changes
 */

package com.aethelgard.engine.user;

import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.ArrayList;
import java.util.List;

/** Captures each settled frame for assertions. */
public final class RecordingUserView implements UserView {

  private final List<PoolSnapshot> frames = new ArrayList<>();

  @Override
  public void onSettled(PoolSnapshot settled) {
    frames.add(settled);
  }

  public List<PoolSnapshot> frames() {
    return List.copyOf(frames);
  }

  public PoolSnapshot lastFrame() {
    if (frames.isEmpty()) {
      throw new IllegalStateException("no frames recorded");
    }
    return frames.getLast();
  }
}
