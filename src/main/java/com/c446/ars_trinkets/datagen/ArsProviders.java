package com.c446.ars_trinkets.datagen;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.glyphs.AirSwordEffect;
import com.c446.ars_trinkets.glyphs.filters.RandomCancel;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.c446.ars_trinkets.datagen.Setup.provider;
import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ArsProviders {

    static String root = ArsTrinkets.MODID;

    public static class GlyphProvider extends GlyphRecipeProvider {

        public GlyphProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            Path output = this.generator.getPackOutput().getOutputFolder();

            recipes.add(get(AirSwordEffect.instance).withItem(Items.NETHERITE_BLOCK,3).withItem(ItemsRegistry.AIR_ESSENCE, 3));
            recipes.add(get(RandomCancel.QUARTER).withItem(Items.COMPARATOR));
            recipes.add(get(RandomCancel.HALF).withItem(RandomCancel.QUARTER.glyphItem).withItem(Items.COMPARATOR));
            recipes.add(get(RandomCancel.THREE_FOURTHS).withItem(RandomCancel.HALF.glyphItem).withItem(Items.COMPARATOR));
            for (GlyphRecipe recipe : recipes) {
                Path path = getScribeGlyphPath(output, recipe.output.getItem());
                saveStable(cache, GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow(), path);
            }
        }

        protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
            return pathIn.resolve("data/" + root + "/recipe/" + getRegistryName(glyph).getPath() + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Example Glyph Recipes";
        }
    }

    public static class EnchantingAppProvider extends ApparatusRecipeProvider {

        public EnchantingAppProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {
            //example of an apparatus recipe
            /*
            recipes.add(builder()
                    .withReagent(ItemsRegistry.SOURCE_GEM)
                    .withPedestalItem(4, Recipes.SOURCE_GEM)
                    .withResult(ItemsRegistry.BUCKET_OF_SOURCE)
                    .withSource(100)
                    .build()
            );
             */

            Path output = this.generator.getPackOutput().getOutputFolder();
            for (ApparatusRecipeBuilder.RecipeWrapper<? extends EnchantingApparatusRecipe> g : recipes) {
                if (g != null) {
                    Path path = getRecipePath(output, g.id().getPath());
                    saveStable(cache, g.serialize(), path);
                }
            }

        }

        protected static Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipe/" + str + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Example Apparatus";
        }
    }

    public static class ImbuementProvider extends ImbuementRecipeProvider {

        public ImbuementProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
            collectJsons(pOutput);
            List<CompletableFuture<?>> futures = new ArrayList<>();
            return provider.thenCompose((registry) -> {
                for (ImbuementRecipe g : recipes) {
                    Path path = getRecipePath(output, g.id.getPath());
                    futures.add(DataProvider.saveStable(pOutput, registry, ImbuementRecipe.CODEC, g, path));
                }
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            });
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            /*
            recipes.add(new ImbuementRecipe("example_focus", Ingredient.of(Items.AMETHYST_SHARD), new ItemStack(ItemsRegistry.SUMMONING_FOCUS, 1), 5000)
                    .withPedestalItem(ItemsRegistry.WILDEN_TRIBUTE)
            );
            */
        }

        protected Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipe/" + str + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Example Imbuement";
        }

    }

//    public static class PatchouliProvider extends com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider {
//
//        public PatchouliProvider(DataGenerator generatorIn) {
//            super(generatorIn);
//        }
//
//        @Override
//        public void collectJsons(CachedOutput cache) {
//
//            for (AbstractSpellPart spell : ArsNouveauRegistry.registeredSpells) {
//                addGlyphPage(spell);
//            }
//
//            //check the superclass for examples
//
//            for (PatchouliPage patchouliPage : pages) {
//                DataProvider.saveStable(cache, patchouliPage.build(), patchouliPage.path());
//            }
//        }
//
//        @Override
//        public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
//            PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
//                    .withIcon(item.asItem())
//                    .withPage(new TextPage(root + ".page." + getRegistryName(item.asItem()).getPath()))
//                    .withPage(recipePage);
//            var page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
//            this.pages.add(page);
//            return page;
//        }
//
//        public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
//            PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity." + root + "." + familiarHolder.getRegistryName().getPath())
//                    .withIcon(root + ":" + familiarHolder.getRegistryName().getPath())
//                    .withTextPage(root + ".familiar_desc." + familiarHolder.getRegistryName().getPath())
//                    .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
//            this.pages.add(new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath())));
//        }
//
//        public void addRitualPage(AbstractRitual ritual) {
//            PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item." + root + '.' + ritual.getRegistryName().getPath())
//                    .withIcon(ritual.getRegistryName().toString())
//                    .withTextPage(ritual.getDescriptionKey())
//                    .withPage(new CraftingPage(root + ":tablet_" + ritual.getRegistryName().getPath()));
//
//            this.pages.add(new PatchouliPage(builder, getPath(RITUALS, ritual.getRegistryName().getPath())));
//        }
//
//        public void addGlyphPage(AbstractSpellPart spellPart) {
//            ResourceLocation category = switch (spellPart.defaultTier().value) {
//                case 1 -> GLYPHS_1;
//                case 2 -> GLYPHS_2;
//                default -> GLYPHS_3;
//            };
//            PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
//                    .withName(root + ".glyph_name." + spellPart.getRegistryName().getPath())
//                    .withIcon(spellPart.getRegistryName().toString())
//                    .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
//                    .withPage(new TextPage(root + ".glyph_desc." + spellPart.getRegistryName().getPath()))
//                    .withPage(new GlyphScribePage(spellPart));
//            this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
//        }
//
//        /**
//         * Gets a name for this provider, to use in logging.
//         */
//        @Override
//        public @NotNull String getName() {
//            return "Example Patchouli Datagen";
//        }
//
//        @Override
//        public Path getPath(ResourceLocation category, String fileName) {
//            return this.generator.getPackOutput().getOutputFolder().resolve("data/" + root + "/patchouli_books/example/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
//        }
//
//        ImbuementPage ImbuementPage(ItemLike item) {
//            return new ImbuementPage(root + ":imbuement_" + getRegistryName(item.asItem()).getPath());
//        }
//
//    }

}
