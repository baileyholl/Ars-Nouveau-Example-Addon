package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArsTrinkets.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THINGS = CREATIVE_MOD_TABS.register("ironbound_artefacts", () ->
                    CreativeModeTab.builder()
                            .withTabsAfter(com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry.GLYPHS.getKey())
                            .title(Component.translatable("tab.ars_trinkets.main"))
                            .icon(() -> new ItemStack(ItemRegistry.Lotus9))
                            .displayItems((enabledFeatures, entries) -> {
//                            //entries.accept(ItemRegistry.DEATH_AMULET.get());
//                            //entries.accept(ItemRegistry.DEVILS_FINGER.get());
//                            //entries.accept(ItemRegistry.MAGICIANS_MONOCLE.get());
//                            //entries.accept(ItemRegistry.JUDGEMENT_SCALE.get());
//                            //entries.accept(ItemRegistry.LICH_HAND.get());
//                            //entries.accept(ItemRegistry.LICH_CROWN.get());
//                            entries.accept(ItemRegistry.HERMIT_EYE.get());
//                            entries.accept(ItemRegistry.LICH_CROWN.get());
//                            //entries.accept(ItemRegistry.STOPWATCH.get());
//
//                            entries.accept(ItemRegistry.MAGIC_DEFENSE_RING.get());
//                            entries.accept(ItemRegistry.PROTECTION_RING.get());
//                            entries.accept(ItemRegistry.GREATER_SPELL_SLOT_UPGRADE.get());
//                            entries.accept(ItemRegistry.AMULET_OF_HOLDING.get());
//                            //entries.accept(ItemRegistry.DECK_OF_ALL_THINGS.get());
//                            //entries.accept(ItemRegistry.STAFF_OF_POWER.get());
//                            //entries.accept(ItemRegistry.ARCHMAGE_SPELLBOOK.get());
//
//                            entries.accept(ItemRegistry.ARCANE_PROTECTION_CLOAK.get());
//                            entries.accept(ItemRegistry.ELVEN_CHAINS.get());
//                            entries.accept(ItemRegistry.STAFF_OF_POWER.get());
//                            entries.accept(ItemRegistry.STAFF_OF_MAGI.get());
//                            entries.accept(ItemRegistry.WEAVE_HELMET.get());
//                            entries.accept(ItemRegistry.WEAVE_CHEST_PLATE.get());
//                            entries.accept(ItemRegistry.WEAVE_LEGGINGS.get());
//                            entries.accept(ItemRegistry.WEAVE_BOOTS.get());
                                ItemRegistry.ITEMS.getEntries().forEach(i -> {
                                    if (!i.is(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS)) {
                                        entries.accept(i.get());
                                    }
                                });
                            })
                            .build()
    );

}
