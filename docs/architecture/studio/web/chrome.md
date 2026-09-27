<!--
  File: docs/architecture/studio/web/chrome.md
  Purpose: Chrome — the declared menus, rail panels, resizable layout, and shortcut list, and the components that render them
  Audience: Agents and humans
  Update when: A menu, panel, layout region, clamp, storage key, or shortcut changes, or how the menus, panels, or shortcut list render changes
-->

# Chrome

Everything around the map is declared as data: the menus and their items, the panels in each side rail, the sizes of the resizable regions, and the shortcut list. Components render that data, so a new panel or menu item is a line in a list, not new screen code. Sizes and open-or-closed flags are remembered in the browser.

## What it reads

The declarations below; clicks and keys on the menu bar and panel headers; and the browser's local storage.

## What it writes

Rendered menus, panels, and the shortcut dialog; menu actions passed to the root ([tool](tool.md)); and local-storage entries for rail visibility, panel visibility, and region sizes.

## Model

**Menus.** Five menus, each a list of items with an id, a label, an optional shortcut label, an optional action, and whether it is enabled. An item without an action is a disabled stub.

| Menu | Enabled items (action) | Stubs |
|------|------------------------|-------|
| File | New world (`world.new`), Random seed (`world.randomSeed`) | Open world…, Save world…, Export map image…, Quit |
| Edit | Copy seed (`seed.copy`) | Undo, Redo, Preferences… |
| View | Perf rail (`view.leftRail`, checkable), World rail (`view.rightRail`, checkable), Focus terminal, Reset map view, Reset layout | — |
| Simulation | Play, Pause, Advance one step, Speed 1x, 2x, 4x, Fastest, Restart UI, Restart engine | Record history… |
| Help | Shortcuts… (`help.shortcuts`) | Documentation…, About Aethelgard |

**Panels.** Each panel is $(\mathrm{id}, \mathrm{title}, \mathrm{dock}, \mathrm{order}, \mathrm{defaultOpen}, \mathrm{collapsible})$:

| Id | Rail | Order | Collapsible |
|----|------|-------|-------------|
| `perf` | left | 10 | yes |
| `world` | right | 10 | no |
| `inspect` | right | 20 | yes |
| `legend` | right | 30 | yes |

A rail shows its panels sorted by order, and all four are open by default.

**Layout.** Three regions with a default and bounds, in CSS pixels:

$$\mathrm{clampSize}(r, x) = \begin{cases} \min\bigl(\mathrm{max}_r,\; \max(\mathrm{min}_r,\; \operatorname{round} x)\bigr) & x \text{ finite} \\ \mathrm{default}_r & \text{otherwise,} \end{cases}$$

with the left rail at 220 (168–420), the right rail at 280 (208–520), and the terminal at 176 (96–520). A drag on a splitter adds the pointer's travel since the press: $x - x_0$ for the left rail, $x_0 - x$ for the right rail, and $y_0 - y$ for the terminal.

**Storage keys.** `aethelgard.layout.<region>` holds a size; `aethelgard.rail.left.open` and `aethelgard.rail.right.open` hold rail flags; `aethelgard.panel.<id>.open` holds a panel flag; a flag is `"1"` or `"0"`.

## Procedure

1. The menus are declared as data, in the shape of the Model.
2. The menu bar renders the menu titles and, for the open menu, its items. A click toggles a menu; hovering another title while one is open switches to it; the arrow keys move between menus; a click outside or Esc closes it. A choice calls the action and closes the menu, and a checkable item shows a tick when it is checked.
3. The panels are declared as data. One rail's panels are listed in order, each panel has its storage key, and the initial flags come from the declarations.
4. A panel renders its header and, when it is open or not collapsible, its body; a collapsible panel has a `−` or `+` toggle.
5. The regions, their bounds, and their storage keys are declared as data. The stored sizes are read and clamped, written, and cleared. On the server, where there is no window, reading gives the defaults and writing does nothing.
6. The eleven shortcut rows are declared as data, and the shortcut dialog shows them; a click outside, or Close, dismisses it. The key handler that performs them is the root's ([tool](tool.md)).

## What is true afterwards

Every region size on screen lies within its bounds, and reloading the page restores the last sizes, rails, and panels. A stub menu item is visible and inert. The shortcut list and the key handler are two separate declarations that currently agree.

Code: [lib/](../../../../ui/web/src/lib/README.md) · [components/](../../../../ui/web/src/components/README.md)
Parent: [web front](README.md).
