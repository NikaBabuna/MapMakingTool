<!--
  File: docs/product/journeys.md
  Purpose: What a person does with the product, and what they see
  Audience: Humans and agents
  Update when: A person-facing sequence changes
-->

# Journeys

What a person does with Aethelgard, step by step, and what they see at each step. These are not the rules of the world. Those live in the [wiki](wiki/README.md). Controls are named with the word on the screen, as the [style guide](style-guide.md) sets it out.

## Watch a world

Watching is the heart of the studio today: the person runs the world forward and sees the plates build it.

1. The person opens Aethelgard. The map is 1920 by 1080 cells, paused at step 0, showing Elevation. It looks flat, because a new world is all ocean.
2. They press Play. The world advances step after step. Plates drift, rifts open, and coasts and ranges move. The Step count on the World rail climbs.
3. They choose a speed: 1x, 2x, 4x, or Fastest. Steps come more often. What one step does is the same at every speed.
4. They press Pause. The map holds.
5. They press A, or choose Advance one step from the Simulation menu. The world takes exactly one step. While that step is working, the World rail reads Working, and another Advance waits.
6. They type a number into Steps on the World rail and press Advance ×N. The world takes that many steps, then stops.

## Look at a place

Looking is how the person reads the world: which plate is where, how high the land stands, and where the boundaries run.

1. They switch to Plates or Overlay with the buttons at the top left of the map, or the keys 1, 2, and 3. The world does not advance. Plates shows each plate in gray with a dark line along its edges. Overlay draws that line over the height.
2. They pan by dragging. East runs into west, so they can drag around the world without end. They cannot drag past the top or the bottom.
3. They zoom by scrolling. The map zooms towards the pointer. They cannot zoom out past the whole map.
4. They click a cell. Inspect shows where it is, its height, which plate it belongs to, and which way that plate is moving.
5. The legend matches the view they have open.
6. They press Reset view, or R. The whole map fits the frame again. The world does not change.

## Start again

Starting again is how the person tries another world, or grows a world they liked a second time.

1. They type a seed into the Seed field on the World rail and press Enter, or press Reset world. If the world has already moved, they confirm first. A new world grows from step 0.
2. They press Random to pick a random seed, or Copy to put the current seed on the clipboard, ready to share.
3. The same seed grows the same world, step for step.
4. From the Simulation menu, Restart engine starts the same world again from step 0. Restart UI reloads the studio and leaves the world as it was.

## Ask in words

The terminal is for questions the panels do not answer: what the world holds, what it is running, and how long it is taking.

1. They focus the terminal, with the ` key or C, or by clicking it. The prompt reads `aethelgard>`.
2. They type `help` and press Enter. The list of nouns and verbs, with examples, appears above the prompt.
3. They type a command, such as `session get` or `session advance 10`, and press Enter. The answer appears above the prompt. A command that advances the world moves the map too.
4. They press ↑ and ↓ to walk back through the lines they typed, up to the last 32.
5. An unknown command answers that it failed. Clear empties the terminal's text. The world stays.

## Arrange the studio

The studio can give the map more room, or show more about the running world.

1. They press P or D, or use the View menu, to hide or show the Perf rail and the World rail. The map takes the room.
2. They drag the edge of a rail, or of the terminal, to resize it. The studio keeps that layout the next time it opens.
3. They choose Reset layout from the View menu. The rails and the terminal go back to their first sizes.
4. They press ? to see every key the studio knows.

## Reconnect

The map is drawn by a world running behind the studio. If the studio loses that world, it says so rather than showing a stale map.

1. The dot beside the name turns red, and a banner appears with Retry.
2. They press Retry. When the world answers, the dot turns green and the map comes back.

## Not built

| Journey | What it will be |
|---------|-----------------|
| Explore | Roll worlds until one feels right, then take it |
| Guide | Nudge one feature and let the rest of the world follow |
| Timeline | Scrub from the first motion of the plates to the present, and read why a place looks as it does |

A person cannot do these three yet.
