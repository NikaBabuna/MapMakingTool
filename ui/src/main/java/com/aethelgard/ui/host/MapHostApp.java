/*
 * File: ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java
 * Purpose: CLI entry to run the localhost map host (F-024)
 * Audience: Dev / Tauri sidecar later
 * Update when: Host launch flags change
 */

package com.aethelgard.ui.host;

import com.aethelgard.product.WorldSpec;
import java.io.IOException;

/**
 * Starts {@link MapHost} on loopback. Default port {@code 7420}, {@link WorldSpec#VIEW}.
 *
 * <p>Usage: {@code MapHostApp [port]}
 */
public final class MapHostApp {

  public static final int DEFAULT_PORT = 7420;

  private MapHostApp() {}

  public static void main(String[] args) throws IOException, InterruptedException {
    int port = DEFAULT_PORT;
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
    }
    MapHost host = MapHost.start(WorldSpec.VIEW, port);
    System.out.println("Aethelgard map host " + host.baseUrl());
    Runtime.getRuntime().addShutdownHook(new Thread(host::close));
    Thread.currentThread().join();
  }
}
