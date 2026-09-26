<!--
  File: docs/protocol/navigation/reading.md
  Purpose: How to take only the needed part of a file, so the context window holds answers and not whole files
  Audience: Agents
  Update when: A reading technique, a size threshold, or a search pattern changes
-->

# Reading

Everything an agent reads stays in its context for the rest of the chat. A file read once is paid for on every later turn. The aim is to answer the question in front of you with the fewest lines read. Locate first, then read the part, never the whole by default.

This page says **how** to read. [bounds.md](bounds.md) says **what** to open for each kind of turn. [pointers.md](pointers.md) says **which** page answers a question. The map of this repository, including its heavy places, is [../../navigation.md](../../navigation.md).

## The reading ladder

Climb one rung at a time. Stop at the first rung that answers the question.

| Rung | Action | What enters your context | Use it when |
|-----:|--------|--------------------------|-------------|
| 1 | Take the path from the map, a door, or a pointer | Nothing new | Always first. Never search for a path a door already gives |
| 2 | List a folder: names only, one level | One line per entry | You need to know what exists, not what it says |
| 3 | Count the file's lines | One number | Before opening any file you have not read in this chat |
| 4 | Read the header and the title: lines 1–8 | Eight lines | To confirm it is the right file. Every docs page starts with a header ([../blueprints/headers/document-header.md](../blueprints/headers/document-header.md)) |
| 5 | List the headings: search `^#{1,3} ` in that one file | One line per heading, with line numbers | To find the section that holds the answer |
| 6 | Search for a literal you know the answer contains | Matching lines only | You know an id, a type name, a status word, or a field label |
| 7 | Read a line range | That range | You know where the answer is, from rung 5 or 6 |
| 8 | Read the whole file | The whole file | The file is small (≤ 150 lines) and you need most of it |

## Size rules

| File size | First contact | Then |
|-----------|---------------|------|
| ≤ 150 lines (small) | May be read whole, if you need most of it | — |
| 151–500 lines (large) | Header and headings only (rungs 4–5) | Read the section's range (rung 7) |
| > 500 lines (huge) | Never read whole. Search inside it (rung 6) | Read only the range around the hit |

Exception: the flow page you are executing may be read whole once, because you follow all of it. A large flow page (the Step flow, the Goal flow) may instead be read one stage or algorithm at a time, as you reach it.

## Reading rules

| # | Rule |
|---|------|
| R1 | Before any read, know the question it answers. If you cannot state it in one sentence, do not read yet |
| R2 | Stop reading as soon as the question is answered. Do not read on "for context" |
| R3 | Before opening a file you have not read in this chat, know its line count (rung 3), and apply the size rules |
| R4 | Never re-read a file you already hold, unless it changed or has left your context (for example, after the chat was summarised). After your own edit, re-read only the edited range, and only if you must check it |
| R5 | Search in the narrowest folder that can hold the answer. Ask first for file names only, then for matching lines. Limit the number of results |
| R6 | Every search excludes the folders listed under **Heavy places** in [../../navigation.md](../../navigation.md) |
| R7 | Read a paperwork record by its id (`docs/paperwork/steps/F-0xx.md`), never by opening its folder. Read a field across records by searching that field (e.g. `^\*\*Status:\*\*`), never by opening every record |
| R8 | Read a blueprint in parts: the key block (lines 1–15), then only the operation section you will apply (`### <Operation>`), then **Check** when you verify. Read the **Skeleton** only when you are creating that file |
| R9 | Read source by member, not by file. Route: [code.md](code.md) |
| R10 | Never let a command print its full output into context. Send long output (a test run, a build, a log) to a scratch file outside the repository, then search that file for the summary and failure lines |
| R11 | Use git in summary form: subjects (`git log --format="%h %s"`), status (`git status --porcelain`), stats (`git diff --stat`), one path at a time (`git diff -- <path>`). Never an unbounded `git log -p`, or a whole-tree diff |
| R12 | A sweep of more than ten files (every link, every door, every record) is done by search or by a throwaway script outside the repository that prints only failures, not by reading each file |

## Search patterns

Each row gives the pattern, the narrowest place to search, and what comes back.

| To find | Search for | In | You get |
|---------|------------|----|---------|
| The Active Goal | the literal `**Active Goal:**` | `docs/paperwork/goals.md` | One line |
| The open Step | the literal `\| in progress \|` | `docs/paperwork/steps.md` | Zero or one line |
| Every Step's status | `^\*\*Status:\*\*` | `docs/paperwork/steps/` | One line per record |
| A page's sections | `^#{1,3} ` | That one file | One line per heading |
| A blueprint operation | `^### <Operation>` | That blueprint | Its line number, then read to the next `### ` |
| Which blueprint shapes a file | the file's path or name | `docs/protocol/blueprints/` (the group doors) | The group door row |
| Where a unit is declared | the pattern on the **Declarations** line of `docs/architecture/program.md`, with the unit's name | The folders that line names | One line |
| Who uses a unit | the unit's name as a whole word, file names only | The source folders, excluding tests | A file list |
| Tests of a unit | the file name the **Tests** line of `docs/architecture/program.md` gives for that unit | The test location that line names | A file list |
| The summary of a test run | the patterns on the **Output summary** line of `docs/architecture/program.md` | The scratch file holding the witness output (R10) | The summary and failure lines |
| Which Step a test witnesses | the test class name | `docs/paperwork/steps/` | The Step records that map it |
| Which page describes a type | the type name | `docs/architecture/` | The mechanism page |
| The safe point | the first subject matching `^Accept F-` or `^Seal: ` | `git log --format="%h %s"` | One commit |
| What is unsealed | — | `git status --porcelain` | One line per changed path |

## Keep out of your context

- Whole folders of numbered records.  
- Whole files of more than 500 lines.  
- Full command output.  
- Any page you opened only because its name looked relevant.
