<!--
  File: docs/protocol/blueprints/source.md
  Purpose: Shape of the header on a new source file
  Audience: Agents and humans
  Update when: The source-header shape changes
-->

# Source header

This is not a documentation file. It is the comment at the top of a new source file, so a reader who opened the file from a search knows whether it is the one they wanted before they read the body. The body is the program. It is not specified here. The behavior is specified in the implementation paper and the spec.

**Write it when.** **Record source** adds a file.

## What each line is for

| Line | Why it is there |
|------|-----------------|
| File | The path, so a copied fragment can be found |
| Purpose | One sentence. What this file is responsible for. Not a list of recent edits |
| Audience | Who is expected to change it |
| Update when | The event that should cause this file’s purpose sentence to change |

## Skeleton

```text
File: <path>
Purpose: <one sentence>
Audience: <who edits it>
Update when: <the event>
```

The comment markers are whatever the language uses. The four lines are the protocol.

## Keep out

The Active Goal sentence. A changelog of the file. A door: if the file created a landmark folder, that folder still gets a `README.md` from the readme blueprint. The header does not replace the door.
