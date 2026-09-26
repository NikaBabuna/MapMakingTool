<!--
  File: docs/product/concept.md
  Purpose: What Aethelgard is for, what it includes, and what it refuses
  Audience: Humans and agents
  Update when: The product's purpose changes, or something enters or leaves its scope
-->

# Aethelgard

A world that remembers how it was made.

Procedural fantasy world generator — simulate tectonics, climate, and terrain so maps stay physically consistent, with a scrubbable history of how the world formed.

Hand-designing a fantasy map is a pleasure until the map stops holding together. A river runs uphill. A desert sits on the wet side of a range. Fixing that means stopping the work to become a geography teacher. Leaving it means a map that fails a second look.

A noise picture can look like terrain and still have no reason for any of it. A paint program can put a mountain wherever you like and will not keep the climate honest. Aethelgard does neither of those as its way of making a world. It grows the world by the processes that make geography, so a desert is there because the mountains are there, and the mountains are there because two plates met.

## The world

Nothing on the map is placed by hand. A world begins as one unbroken ocean of thin crust, cut into a dozen or more plates that each drift in their own direction. Where two plates pull apart, new ocean opens between them. Where they push together, ocean crust is drawn down beneath the other side, or the crust on both sides is squeezed thicker. Crust that grows thick enough stands out of the sea as land, and that is how continents appear. Height is never painted on afterwards. It is read from how thick the crust is, so every hill has a cause.

That chain is meant to go further. Wind follows from where the land and the sea lie, rain from the wind, temperature from latitude and height, and biomes from all of them together. Those layers are part of the promise, and they are not in the world yet.

The rules behind each link of the chain are in the [wiki](wiki/README.md).

## The history

The map is a timeline, not a finished picture. Every step the world takes follows from the one before it, so a world has a past as well as a present. A person should be able to move from the first drift of the plates to the present, rewind a coastline, and point at a range and hear what had to happen before it could stand there. Today a person can only run the world forward, one step or many, and start it again from the beginning. The scrubbable history is part of the promise, and it is not something a person can do yet.

## The person

The person is making a world that has to survive close inspection: a campaign, a novel, a map of their own. They enjoy the designing. They do not want the physics to be the fight.

Two ways of working are both part of the promise. In the first, Explore, they roll worlds until one feels right and take it as it is, or as a foundation to build on. In the second, Guide, they nudge the thing they care about, a range or a dry coast, and the rest of the world stays consistent with that nudge. Neither way is available yet.

## What a person can do today

Aethelgard today is a desktop studio for watching a world form. A person opens it on a world of 1920 by 1080 cells, presses Play, and watches the plates drift, ocean open along the rifts, and land rise where plates collide. They can switch between a view of height, a view of the plates, and a view of the boundaries where plates meet. They can pan and zoom around the map and click any cell to see how high it stands and which plate carries it. A terminal along the bottom answers questions about the running world in words. The same seed always grows the same world, so a world worth keeping can be grown again. Each of these is set out step by step in [journeys](journeys.md).

## In scope

This list is binding. Work on anything that is not in it does not start until the list has been changed to include it.

| Area | What it includes |
|------|------------------|
| **The world** | A world grown by simulated geology: plates that move; crust that is made, consumed, and thickened where plates meet; and height read from that crust. Later, the rest of the same chain: wind, rain, temperature, and biomes. |
| **The history** | Moving through the formation of a world, from the first drift of the plates to the present, and finding out why a place looks as it does. |
| **Ways of working** | Explore, rolling worlds until one feels right. Guide, nudging one feature while the rest of the world stays consistent. |
| **The studio** | The desktop application a person uses: the map and its views, the controls that run the world, the panels that describe it, and a terminal that questions the running world in words. |
| **The command line** | Running a world without a window, to check it or to script it, in the same command language the studio's terminal speaks. |
| **The engine** | The general step-by-step simulation core the world runs on. It is kept apart from the world's rules, so it can run them without knowing what they are. |
| **How it is made** | Development by a person working with AI agents, under the protocol this repository keeps. |

## What it is not

Aethelgard is not a noise generator with a map painted on top. It is not a freehand terrain editor. It is not a picture that only has to look right once. The refusals below are binding in the same way as the list above.

- Noise, or painting by hand, as the way the shape of the world is decided.
- Several people working in one world, a hosted server, or user accounts.
- The engine published as a library or package of its own.
