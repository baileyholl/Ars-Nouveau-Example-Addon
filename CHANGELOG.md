# Ars Trinkets 3.0.0

A major progression, combat, and spell-system update.

## New Content

- Added the **Devour Soul** glyph.
  - Consumes a configurable percentage of the caster's souls.
  - Damage scales with the number of souls consumed.
  - Includes a new recipe, model, particles, and configuration options.
- Added scalable mob progression.
  - Mobs now receive levels and cores based on their base stats.
  - Mob health, attack damage, and armor can scale independently.
  - Mob difficulty can be capped relative to nearby players.
- Added mob soul inspection.
  - Inspect Soul can reveal mob progression information.
  - Mob name tags can display their level and core rank.
- Added level-based bonus glyph slots.
  - Bonus slots can be configured per player level.
  - Supports stacking with Ars Nouveau's Infinite Spells system.
  - Server-side spell size enforcement prevents oversized spells.
- Expanded Omnipotence Crown functionality.
  - Grants flight while equipped.
  - Adds a visible extra-life HUD.
  - Crown lives now synchronize correctly between server and client.
  - Added configurable HUD positioning.

## Tribulations and Combat

- Added extensive configuration for mob scaling and combat normalization.
- Improved roaming mob difficulty scaling.
- Roaming health, attack, and resistance scaling can now be configured separately.
- Improved tribulation boss enhancements and resistance calculations.
- Player and mob damage scaling now account for levels and cores more consistently.
- Improved cleanup and state handling for player abilities, crown lives, and level bonuses.

## Technical Improvements

- Migrated the project from NeoGradle to ModDevGradle.
- Updated NeoForge and dependency versions for Minecraft 1.21.1.
- Improved mod metadata generation and packaging.
- Added networking support for client-side progression and HUD information.
- Improved `/ars-trinkets` administration commands and error handling.
