<!--
  File: docs/protocol/flows/structure.md
  Purpose: Bookkeeping for folder moves, scope changes, and decisions
  Audience: Agents and humans
  Update when: A structure flow changes
-->

# Structure flows

These algorithms keep the map honest when the tree or the scope changes. A move that is not followed by the entrance update is a lost file. The next agent will recreate it under the old path.

## Restructure

**When.** A folder or a major document is created, moved, or renamed.

**Before.** [../environment/phase.md](../environment/phase.md) allows the move. In alpha, it does. The new path does not break the depth rule: three levels under `docs/`, or four only under `docs/architecture/`.

**Steps.**

1. Create or move the files.
2. Put a `README.md` on any landmark folder that does not have one, from the readme blueprint. A landmark is a folder the entrance expects an agent to open. Build output and intermediate namespace segments are not landmarks.
3. Update `docs/navigation.md` so every moved path resolves, and update `docs/README.md` if a top folder appeared or disappeared.
4. Append a Structure line to `docs/paperwork/changelog.md`: date, what moved, why a reader should care.
5. If a reader would not have guessed the new path, run **Decide** and point the changelog line at that ADR.
6. Search the remaining documents for the old path and fix the links you find. A link to a deleted file is a broken door.

**Done.** From the entrance, every new path is one click away, and the old path is not linked.

**Not done.** The files moved and `docs/navigation.md` still lists the old folder.

## Scope

**When.** The human wants the product to include or exclude something the scope document does not currently say.

**Before.** You can say the new in-scope or out-of-scope sentence.

**Steps.**

1. Edit `docs/project/project.md` first. The work that needs the new scope does not start in the same Step before this edit is in the tree.
2. If the change is technical (a language, a module split, a new kind of program), run **Decide**.
3. Append a changelog line under the scope heading if the file has one, otherwise under Structure, naming the scope change.

**Done.** An agent that reads only the scope document will allow the new work and refuse the excluded work.

**Not done.** The feature exists and the scope document still lists it as out of scope.

## Decide

**When.** A technical or structural choice should still be visible after this chat, or **Restructure** / **Scope** called this algorithm.

**Before.** The next ADR number is one higher than the last row on `docs/paperwork/decisions.md`.

**Steps.**

1. Write `docs/paperwork/decisions/ADR-0xx-<slug>.md` from the decision blueprint, and add a row to the index. Status, date, the decision in sentences, why, and what it amends or supersedes. Do not rewrite an old ADR. If this one amends an older file, add an “Amended by” line on that older file.
2. In the paper or the scope page a reader will open, add a pointer to that ADR id. The paper states what is true now. The ADR states why and when. Do not make the paper carry the whole argument, and do not make the ADR the only place that says what the code does.

**Done.** A reader who disagrees with the choice can find the reason without reading the chat.

**Not done.** The reason is in the chat and the decisions log ends at the previous number.
