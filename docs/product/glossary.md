<!--
  File: docs/product/glossary.md
  Purpose: Words a person uses for this world and its studio
  Audience: Humans and agents
  Update when: A domain word is introduced or its meaning changes
-->

# Glossary

Words for the world and for the studio that shows it. The rule each word points at lives on the wiki page that owns it. The last column names the word or idea it is most easily mistaken for.

## The map

| Word | Meaning | Not |
|------|---------|-----|
| Cell | The smallest place in the world. The map is a grid of 1920 by 1080 of them | A pixel. Zoomed in, one cell covers many pixels |
| Seed | The number that chooses which world grows: how many plates, where they begin, and how they first drift. The same seed grows the same world | A saved drawing of a finished map |
| Step | One tick of the world. Every plate moves once and every rule is applied once | A frame on the screen. The speed setting changes how often steps happen, not what a step does |
| Pole | The top or the bottom edge of the map. The world crosses it as it would cross the pole of a globe | A wall. Only the view stops there |

## Plates and crust

| Word | Meaning | Not |
|------|---------|-----|
| Plate | A piece of the crust that moves together | A single cell, or a continent. A continent is thickness, not a plate |
| Drift | The direction a plate is moving: standing still, or one of the eight compass directions, at most one cell per step | The speed setting, which only changes how often steps come |
| Crust | The world's material, measured as a whole-number thickness | The height drawn on the map |
| Thickness | How much crust there is in a place. New ocean is 8, continents start at 16, and arcs and sutures stop at 32 | Height. Height is thickness minus 8 |
| Ocean | Crust thinner than land. New ocean starts at thickness 8 | The blue paint on the screen |
| Continent | Crust at least 16 thick | A plate. Plates do not merge when continents meet |
| Boundary | Where two plates meet. They pull apart, collide, or slide past each other | The name of a menu, or a line drawn for decoration |
| Rift | A boundary where two plates pull apart | A fission, where one plate splits because its pieces stopped touching |
| Ridge | Where plates pull apart and new, thin ocean opens | A collision, which thickens or consumes crust |
| Trough | The dip in the ocean beside a rift: 4 thick on the rift, back to 8 by eight cells away | Open ocean, which stands level at 8 |
| Subduction | Ocean crust consumed where it is driven against thicker crust | A continent disappearing because its plate is the smaller one |
| Arc | Land raised where two oceans collide. One meeting can lift the winner to thickness 16 | A suture, which is two continents thickening |
| Suture | Two continents pressed together. Each thickens. Neither is consumed | A joining of the two plates into one plate |
| Cap | The thickness past which arcs and sutures add no more crust: 32 | A limit on how large a plate may grow |
| Fission | A plate splitting because its cells no longer touch. Each piece becomes a plate of its own | A rift, which opens new ocean between two plates |

## Height

| Word | Meaning | Not |
|------|---------|-----|
| Elevation | How far the crust stands above the ocean baseline, and the view that colours it | The thickness number itself |
| Isostasy | Thick crust floating higher than thin crust. It is why height follows thickness | A rule that moves crust. It only reads it |
| Baseline | Height 0, where new ocean crust stands | The edge of a continent, which starts at height 8 |

## The studio

| Word | Meaning | Not |
|------|---------|-----|
| View | One of three pictures of the same world: Elevation, Plates, or Overlay | A change to the world. Switching views changes nothing in it |
| Overlay | The view that draws those boundaries on the plates | The height view |
| Inspect | The panel that describes the cell a person clicked: where it is, how high it stands, which plate carries it, and which way that plate drifts | A tool that changes the cell |
| Legend | The key to the colours of the view that is open | A list of places |
| Terminal | The strip along the bottom where a person asks the running world questions in words | A place to change the world's rules |

## Ways of working

| Word | Meaning | Not |
|------|---------|-----|
| Explore | Rolling worlds until one feels right | Guide. Explore is not something a person can do yet |
| Guide | Nudging one feature and letting the rest of the world follow | Explore. Guide is not something a person can do yet |
| Timeline | Moving through the history of how the world formed | Play, which only runs the world forward. A timeline is not something a person can do yet |
