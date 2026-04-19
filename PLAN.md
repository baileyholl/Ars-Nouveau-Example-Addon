Plan: Addon Bonus Glyph Slots Strategy
Design an addon-safe BONUS_GLYPH_SLOTS pipeline that extends Ars Nouveau without hard forking core classes. The plan maps all current slot-limit touchpoints (GUI, paste/import cap, and cast-time policy), then introduces a player/item-aware slot provider exposed as an attribute/capability bridge. Prefer Mixins for stable interception points and keep reflection as fallback. This avoids server-global-only behavior while preserving compatibility with upstream updates and other addons.
Steps
Map slot-limit call sites around GuiSpellBook#getExtraGlyphSlots in GuiSpellBook.java and validator flow in ArsNouveauAPI.java, StandardSpellValidator.java.
Define an addon API contract (BONUS_GLYPH_SLOTS) with resolution order (player attribute -> caster/item bonus -> server config fallback) anchored to AbstractCaster#getBonusGlyphSlots in AbstractCaster.java.
Choose interception boundaries: inject GUI-visible limit (getExtraGlyphSlots, paste cap, slot window) and cast-time enforcement hook (new validator layer near ISpellValidator) to prevent client-only desync.
Draft Mixin-first implementation plan (target signatures, @Inject/@ModifyReturnValue points, fail-soft guards) with reflection fallback only for optional compatibility paths; mirror existing mixin layout from ars_nouveau.mixins.json.
Define compatibility and migration policy: feature-flag the addon behavior, avoid replacing whole method bodies when an additive return-value patch works, and pin version checks around changed signatures between Ars Nouveau releases.
Further Considerations
Attribute scope: should BONUS_GLYPH_SLOTS be per-player, per-caster-item, or both with max/stacking rules? Option A player-only / Option B item-only / Option C hybrid.
Enforcement model: do you want hard server rejection for over-limit spells at cast time, or only authoring-time UI limits? Option A strict server / Option B UI only / Option C configurable.
Mixin granularity: should we patch only GuiSpellBook paths first, or include validator/cast pipeline in v1 to avoid edge cases (clipboard/import/networked spell writes)?