<!--
  File: docs/architecture/cli/runner.md
  Purpose: Runner — how the command line becomes one session, some dispatched lines, printed output, and an exit code
  Audience: Agents and humans
  Update when: How an invocation is parsed, run, printed, or ended changes
-->

# Runner

The program accepts two shapes of command line. With flags, it can seed the world, advance it, and run several command lines in a row. Without flags, the words themselves are one command line. Either way it prints what the commands return and exits with success or failure.

## What it reads

The argument vector. Flags: `--seed S` (a 64-bit integer, default 0), `--steps N` (a 32-bit integer, default 0), and `-c LINE` or `--command LINE`, repeatable.

## What it writes

Text on standard output and a process exit code. The map is always the $8 \times 8$ size of `WorldSpec.DEFAULT`; only the seed changes. A refused argument or line prints `error: <message>` and exits with 2.

## Model

Flag mode holds when the vector is empty or any argument begins with `-`:

$$\mathrm{flagMode}(a_1..a_m) \iff m = 0 \;\vee\; \exists i:\; a_i \text{ starts with } \texttt{-} .$$

Verb mode runs the single line $a_1 \,\texttt{" "}\, a_2 \dots a_m$ on the seed-0 session. Flag mode parses $\mathrm{argv}$ into $(s, \mathit{steps}, \mathit{specified}, (\ell_1..\ell_r))$, with $\mathit{specified}$ also true for an empty vector, and runs, with $\mathrm{Dispatch}$ the dispatcher and $\oplus$ joining outputs with a newline:

$$\text{if } r = 0:\;\; [\,\mathrm{Dispatch}(\texttt{session advance } \mathit{steps}) \text{ when specified and } \mathit{steps} > 0\,];\; \mathrm{Dispatch}(\texttt{session get dump}),$$

$$\text{if } r > 0:\;\; o = [\,\mathrm{Dispatch}(\texttt{session advance } \mathit{steps})\,] \oplus \mathrm{Dispatch}(\ell_1) \oplus \dots \oplus \mathrm{Dispatch}(\ell_r),$$

stopping at the first failure, which returns the outputs so far, then the failure, with the failure's code. The exit code is 0 when every line succeeded.

## Procedure

1. The entry point runs the arguments, prints the output, adds a newline when the output is not empty and does not end with one, and exits with the result's code.
2. The run treats a null vector as empty and chooses between flag mode and verb mode. It turns any `IllegalArgumentException` into the result `(2, "error: <message>")`.
3. The flags are read left to right, each value taken and converted to a number. An unknown argument, a missing value, a non-number, and a negative step count are refused, and an empty vector marks the steps as given.
4. `CliOptions` holds the seed, the steps, whether steps were given, and an immutable copy of the lines. The default options are seed 0, no steps, steps given, no lines.
5. With the options, a negative step count is refused, the session is created on the $8 \times 8$ map with the given seed, and the lines run as in the Model.
6. `CliResult` pairs an exit code with a non-null output, and it is a success for code 0.

## What is true afterwards

Every invocation builds exactly one world and exits. With no arguments at all, it prints the dump of the seed-0 world at step 0. The runner catches only `IllegalArgumentException`: any other failure inside a step ends the process with the Java runtime's uncaught-exception report and a non-zero exit code.

Code: [cli/](../../../cli/src/main/java/com/aethelgard/cli/README.md)  
Parent: [one run of the CLI](README.md). Why the CLI and the studio share one language: [ADR-012](../../paperwork/decisions/ADR-012-simulation-runner.md).
