<!--
  File: docs/architecture/cli/README.md
  Purpose: Level 3 — one run of the command-line program, in the order Main.main and CliRunner.run take it
  Audience: Agents and humans
  Update when: Main.main, CliRunner.run, or the order of an invocation changes
-->

# One run of the CLI

The command-line program runs a world with no window: it builds one session, runs the lines it was given in the shared command language, prints what they return, and exits with a status code. The entry point is `Main.main` in [`Main.java`](../../../cli/src/main/java/com/aethelgard/cli/Main.java). One rule holds across the level: an invocation owns exactly one session, and every step and every read goes through the same command language that the studio's terminal uses.

$$\mathrm{run}(\mathrm{argv}) \;=\; \mathrm{Exit} \circ \mathrm{Print} \circ \mathrm{Dispatch} \circ \mathrm{Session} \circ \mathrm{Parse}\,(\mathrm{argv})$$

1. **Parse.** `CliRunner.run` decides between flag mode and verb mode, and in flag mode parses `--seed`, `--steps`, and repeated `-c` lines into `CliOptions`. [Runner](runner.md).
2. **Session.** It creates one `ProductSession` on the $8 \times 8$ map, with the given seed in flag mode or seed 0 in verb mode. [Runner](runner.md).
3. **Dispatch.** It runs the lines through `CommandDispatch.execute`: in flag mode an optional `session advance N`, then each `-c` line, or `session get dump` when there is none; in verb mode the words of the command line as one line. [Language](language.md).
4. **Print.** `Main.main` prints the `CliResult` output and ends it with a newline. [Runner](runner.md).
5. **Exit.** The process exits with the result's code: 0 on success, 2 on a refused line or argument. [Runner](runner.md).

The coarser level: [../program.md](../program.md). The session it drives: [../session/run.md](../session/run.md). The studio's terminal, which speaks the same language: [../studio/web/terminal.md](../studio/web/terminal.md).
