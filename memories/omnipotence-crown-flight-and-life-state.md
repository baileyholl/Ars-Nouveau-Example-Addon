---
name: omnipotence-crown-flight-and-life-state
summary: The crown uses Caelus fall-flying plus mayfly, tracks lives across a clientbound HUD payload, and resets only after an unprevented death respawns.
tags: [#omnipotence-crown, #elytra, #flight, #extra-lives, #caelus, #hud, #networking]
impact: normal
commit: c04995e (dirty)
date: 2026-07-18
created_at: 2026-07-18T17:10:00+02:00
scope: src/main/java/com/c446/ars_trinkets/item/OmnipotenceRune.java
---

## Problem
The crown did not grant creative flight or Elytra flight. Its extra-life map was reset on every respawn, so a respawn unrelated to a true death could restore a partially depleted stock.

## Cause
Flight abilities were never added by the crown. Life state had no pending-true-death marker; `onRespawn` unconditionally reset the cycle.

## Resolution
The crown now grants `mayfly` while equipped and adds Caelus's fall-flying attribute modifier. `ExtraLifeTracker` consumes lives on prevented death, marks exhausted deaths as awaiting respawn, and resets only when that true death's respawn occurs. A clientbound payload blits the Heart of Blasphemy texture's painted 9x9 region (source offset 3,3 in its 16x16 canvas) directly above the armor bar, with vanilla's 8-pixel slot spacing; X/Y offsets are configurable. Prevented lethal hits play `ENCHANTMENT_TABLE_USE`. Regression tests cover life initialization/decrement/reset/cleanup and HUD layout.

## Notes
Caelus is declared as a required mod dependency because the crown's Elytra behavior uses its public API.

The client HUD must not resolve `DeferredHolder.get()` from a static initializer. Resolve the Heart of Blasphemy item lazily during rendering; automatic subscriber discovery loads client classes before item registration completes.
