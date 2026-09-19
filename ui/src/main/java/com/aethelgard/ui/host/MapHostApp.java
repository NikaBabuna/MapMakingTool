/*
 * File: ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java
 * Purpose: CLI entry to run the localhost map host (F-024/F-026)
 * Audience: Dev / Tauri shell
 * Update when: Host launch flags change
 */

package com.aethelgard.ui.host;

import com.aethelgard.product.WorldSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Starts {@link MapHost} on loopback. Default port {@code 7420}, {@link WorldSpec#VIEW}.
 *
 * <p>Writes {@code aethelgard-maphost.pid} under the system temp dir so the Tauri shell can stop
 * this process on quit.
 *
 * <p>Usage: {@code MapHostApp [port]}
 */
public final class MapHostApp {

  public static final int DEFAULT_PORT = 7420;
  public static final String PID_FILE_NAME = "aethelgard-maphost.pid";

  private MapHostApp() {}

  public static Path pidFile() {
    return Path.of(System.getProperty("java.io.tmpdir"), PID_FILE_NAME);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    int port = DEFAULT_PORT;
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
    }
    Path pid = pidFile();
    Files.writeString(pid, Long.toString(ProcessHandle.current().pid()));
    MapHost host = MapHost.start(WorldSpec.VIEW, port);
    System.out.println("Aethelgard map host " + host.baseUrl());
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  host.close();
                  try {
                    Files.deleteIfExists(pid);
                  } catch (IOException ignored) {
                    // best-effort
                  }
                }));
    Thread.currentThread().join();
  }
}
