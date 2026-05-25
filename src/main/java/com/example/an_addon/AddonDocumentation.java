package com.example.an_addon;
import com.example.an_addon.datagen.Setup;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.GlyphEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry.*;

@EventBusSubscriber(modid = ExampleANAddon.MODID)
public class AddonDocumentation {
    public static final String root = ExampleANAddon.MODID;
    @SubscribeEvent
    public static void addPages(ReloadDocumentationEvent.AddEntries event) {
        for (AbstractSpellPart glyph : ArsNouveauRegistry.registeredSpells) {
            var entry = addPage(EntryBuilder.of(glyph)
                    .withName(AddonDocumentation.root + ".glyph_name." + glyph.getRegistryName().getPath())
                    .withIcon(glyph.glyphItem)
                    .withPage(GlyphEntry.create(glyph))
                    .withCraftingPages(glyph.glyphItem));

            entry.withSearchTag(Component.translatable("ars_nouveau.keyword.glyph"));

            for (SpellSchool school : glyph.spellSchools) {
                entry.withSearchTag(school.getTextComponent());
                for (SpellSchool subschool : school.getSubSchools()) {
                    entry.withSearchTag(subschool.getTextComponent());
                }
            }
        }
    }

    private static DocEntry block(BlockRegistryWrapper<? extends Block> block) {
        return getEntry(BuiltInRegistries.BLOCK.getKey(block.get()));
    }

    private static DocEntry item(ItemRegistryWrapper<? extends Item> item) {
        return getEntry(BuiltInRegistries.ITEM.getKey(item.get()));
    }

    private static DocEntry glyph(AbstractSpellPart item) {
        return getEntry(item.getRegistryName());
    }

    private static DocEntry addPage(DocEntryBuilder builder) {
        return DocumentationRegistry.registerEntry(builder.category, builder.build());
    }

    static class EntryBuilder extends DocEntryBuilder {
        public static EntryBuilder of(DocCategory category, String name) {
            return of(category, name, ExampleANAddon.prefix(name));
        }

        public static EntryBuilder of(DocCategory category, String name, ResourceLocation entryId) {
            return new EntryBuilder(category, name.contains(".") ? name : AddonDocumentation.root + ".page." + name, entryId);
        }

        public static EntryBuilder of(DocCategory category, ItemRegistryWrapper<? extends Item> item) {
            return of(category, item.get().getDescriptionId(), BuiltInRegistries.ITEM.getKey(item.get()));
        }

        public static EntryBuilder of(DocCategory category, ItemLike item) {
            return of(category, item.asItem().getDescriptionId(), BuiltInRegistries.ITEM.getKey(item.asItem()));
        }

        public static EntryBuilder of(AbstractSpellPart glyph) {
            return of(Documentation.glyphCategory(glyph.getConfigTier()), glyph.getRegistryName().getPath());
        }

        public static EntryBuilder of(AbstractRitual glyph) {
            return of(RITUAL_INDEX, glyph.getRegistryName().getPath());
        }

        public static EntryBuilder of(DocCategory category, BlockRegistryWrapper<? extends Block> block) {
            return of(category, block.get().getDescriptionId(), BuiltInRegistries.BLOCK.getKey(block.get()));
        }

        private EntryBuilder(DocCategory category, String name, ResourceLocation entryId) {
            super(category, name.contains(".") ? name : AddonDocumentation.root + ".page." + name, entryId);
        }

        private EntryBuilder(DocCategory category, ItemLike itemLike) {
            super(category, itemLike);
        }
    }
}