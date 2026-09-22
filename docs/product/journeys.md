<!--
  File: docs/product/journeys.md
  Purpose: What a person does with the product, and what they see
  Audience: Humans and agents
  Update when: A person-facing sequence changes
-->

# Journeys

What a person does. These are not the rules of the world. Those live in the wiki.

## Watch a world

1. The person opens Aethelgard. The map is 1920 by 1080 cells, paused, showing Elevation.
2. They press Play. The map advances. Coasts and ranges move.
3. They press Pause. The map holds.
4. They press Advance. The world takes one step. While that step is working, another Advance waits.

## Look at a place

1. They switch to Plates or Overlay. The world does not advance.
2. They pan by dragging. East runs into west. They cannot drag past the top or the bottom.
3. They zoom by scrolling. They cannot zoom out past the whole map.
4. They click a cell. Inspect shows where it is, its height, which plate it belongs to, and which way that plate is moving.
5. The legend matches the view they have open.

## Start again

1. They change the seed, or they ask for a new world. If the world has already moved, they confirm.
2. The same seed grows the same world.
3. Reset view puts the map back in the frame. It does not rewind the world.

## Ask in words

1. They focus the terminal. The prompt reads `aethelgard>`.
2. They type a command and press Enter. The answer appears above the prompt.
3. An unknown command answers that it failed. Clearing the terminal clears the words on screen. The world stays.

## Not built

| Journey | What it will be |
|---------|-----------------|
| Explore | Roll worlds until one feels right, then take it |
| Guide | Nudge one feature and let the rest of the world follow |
| Timeline | Scrub from the first motion of the plates to the present, and read why a place looks as it does |

A person cannot do these three yet.
