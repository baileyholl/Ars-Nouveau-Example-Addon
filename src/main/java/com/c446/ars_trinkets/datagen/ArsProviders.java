package com.c446.ars_trinkets.datagen;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.spells.glyphs.*;
import com.c446.ars_trinkets.spells.glyphs.filters.IsNotSelf;
import com.c446.ars_trinkets.spells.glyphs.filters.IsSelf;
import com.c446.ars_trinkets.spells.glyphs.forms.AuraForm;
import com.c446.ars_trinkets.registry.ItemRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGrow;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLightning;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.c446.ars_trinkets.datagen.Setup.provider;
import static com.c446.ars_trinkets.registry.ItemRegistry.*;
import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;
import static net.minecraft.world.item.Items.*;

public class ArsProviders {

    static String root = ArsTrinkets.MODID;

    public static class GlyphProvider extends GlyphRecipeProvider {

        public GlyphProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            Path output = this.generator.getPackOutput().getOutputFolder();

            recipes.add(get(EffectAirSword.INSTANCE).withItem(NETHERITE_INGOT, 3).withItem(ItemsRegistry.AIR_ESSENCE, 3));
            recipes.add(get(EffectWaterSpear.INSTANCE).withItem(NETHERITE_INGOT, 3).withItem(ItemsRegistry.WATER_ESSENCE, 3));
            recipes.add(get(EffectSonicBoom.INSTANCE).withItem(NETHERITE_INGOT, 3).withItem(ItemsRegistry.AIR_ESSENCE, 3));
            recipes.add(get(EffectSunFlare.INSTANCE).withItem(NETHERITE_INGOT, 3).withItem(ItemsRegistry.FIRE_ESSENCE, 3));
            recipes.add(get(IsSelf.INSTANCE).withItem(Items.LAPIS_LAZULI, 3).withItem(ItemsRegistry.MANIPULATION_ESSENCE, 3));
            recipes.add(get(IsNotSelf.INSTANCE).withItem(Items.REDSTONE, 3).withItem(IsSelf.INSTANCE.getGlyph().asItem()));
            //recipes.add(get(Inversion.INSTANCE).withItem(Items.REDSTONE, 3).withItem(EffectExchange.INSTANCE.getGlyph().asItem()).withItem(Items.NETHERITE_INGOT, 3));
            recipes.add(get(AuraForm.INSTANCE).withItem(MethodSelf.INSTANCE.getGlyph().asItem()).withItem(Items.NETHERITE_BLOCK, 3));
            recipes.add(get(EffectAncientLightningBolt.INSTANCE).withItem(EffectLightning.INSTANCE.getGlyph().asItem()).withItem(Essence10.get(), 6));
            recipes.add(get(EffectAdvancedGrowth.INSTANCE).withItem(EffectGrow.INSTANCE.getGlyph().asItem()).withItem(Essence6.get(), 5));
            recipes.add(get(EffectInspectSoul.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.NETHERITE_INGOT, 1));
            recipes.add(get(EffectDevourSoul.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.NETHERITE_BLOCK, 1));

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

            // ESSENCES
     /*       {
                // copper essence
                recipes.add(builder()
                        .withReagent(Items.COPPER_INGOT)
                        .withPedestalItem(4, ItemsRegistry.SOURCE_GEM)
                        .withPedestalItem(4, Items.COPPER_INGOT)
                        .withResult(new ItemStack(ItemRegistry.Essence1, 5))
                        .withSourceCost(100)
                        .build()
                );

                // iron essence
                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence1.get())
                        .withPedestalItem(4, ItemRegistry.Essence1.get())
                        .withPedestalItem(4, Items.IRON_INGOT)
                        .withResult(new ItemStack(ItemRegistry.Essence2, 2))
                        .withSourceCost(200)
                        .build()
                );

                // silver essence
                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence2.get())
                        .withPedestalItem(4, ItemRegistry.Essence2.get())
                        .withPedestalItem(4, Items.IRON_BLOCK)
                        .withResult(new ItemStack(ItemRegistry.Essence3, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence3.get())
                        .withPedestalItem(4, ItemRegistry.Essence3.get())
                        .withPedestalItem(4, Items.GOLD_INGOT)
                        .withResult(new ItemStack(ItemRegistry.Essence4, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence4.get())
                        .withPedestalItem(4, ItemRegistry.Essence4.get())
                        .withPedestalItem(4, Items.GOLD_BLOCK)
                        .withResult(new ItemStack(ItemRegistry.Essence5, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence5.get())
                        .withPedestalItem(8, ItemRegistry.Essence5.get())
                        .withResult(new ItemStack(ItemRegistry.Essence6, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence6.get())
                        .withPedestalItem(8, ItemRegistry.Essence6.get())
                        .withResult(new ItemStack(ItemRegistry.Essence7, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence7.get())
                        .withPedestalItem(8, ItemRegistry.Essence7.get())
                        .withResult(new ItemStack(ItemRegistry.Essence8, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence8.get())
                        .withPedestalItem(8, ItemRegistry.Essence8.get())
                        .withResult(new ItemStack(ItemRegistry.Essence9, 2))
                        .withSourceCost(1000)
                        .build()
                );

                recipes.add(builder()
                        .withReagent(ItemRegistry.Essence9.get())
                        .withPedestalItem(8, ItemRegistry.Essence9.get())
                        .withResult(new ItemStack(ItemRegistry.Essence10, 2))
                        .withSourceCost(1000)
                        .build()
                );
            }*/

            // CURIOS
            {
                //LOTUSES
                {
                    recipes.add(builder()
                            .withReagent(Items.SUNFLOWER)
                            .withPedestalItem(8, ItemRegistry.Essence3.get())
                            .withResult(ItemRegistry.Lotus3.get())
                            .withSourceCost(1500)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus3.get())
                            .withPedestalItem(8, ItemRegistry.Essence4.get())
                            .withResult(ItemRegistry.Lotus4.get())
                            .withSourceCost(2000)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus4.get())
                            .withPedestalItem(8, ItemRegistry.Essence5.get())
                            .withResult(ItemRegistry.Lotus5.get())
                            .withSourceCost(2500)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus5.get())
                            .withPedestalItem(8, ItemRegistry.Essence6.get())
                            .withResult(ItemRegistry.Lotus6.get())
                            .withSourceCost(3000)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus6.get())
                            .withPedestalItem(8, ItemRegistry.Essence7.get())
                            .withResult(ItemRegistry.Lotus7.get())
                            .withSourceCost(3500)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus7.get())
                            .withPedestalItem(8, ItemRegistry.Essence8.get())
                            .withResult(ItemRegistry.Lotus8.get())
                            .withSourceCost(4000)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus8.get())
                            .withPedestalItem(8, ItemRegistry.Essence9.get())
                            .withResult(ItemRegistry.Lotus9.get())
                            .withSourceCost(4500)
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Lotus9.get())
                            .withPedestalItem(8, ItemRegistry.Essence10.get())
                            .withResult(ItemRegistry.Lotus10.get())
                            .withSourceCost(5000)
                            .build()
                    );
                }

                //RING
                {
                    recipes.add(builder()
                            .withReagent(ItemsRegistry.RING_OF_POTENTIAL)
                            .withPedestalItem(8, ItemRegistry.Essence3.get())
                            .withResult(ItemRegistry.Ring3.get())
                            .withSourceCost(1500) // 500 * 3
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring3.get())
                            .withPedestalItem(8, ItemRegistry.Essence4.get())
                            .withResult(ItemRegistry.Ring4.get())
                            .withSourceCost(2000) // 500 * 4
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring4.get())
                            .withPedestalItem(8, ItemRegistry.Essence5.get())
                            .withResult(ItemRegistry.Ring5.get())
                            .withSourceCost(2500) // 500 * 5
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring5.get())
                            .withPedestalItem(8, ItemRegistry.Essence6.get())
                            .withResult(ItemRegistry.Ring6.get())
                            .withSourceCost(3000) // 500 * 6
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring6.get())
                            .withPedestalItem(8, ItemRegistry.Essence7.get())
                            .withResult(ItemRegistry.Ring7.get())
                            .withSourceCost(3500) // 500 * 7
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring7.get())
                            .withPedestalItem(8, ItemRegistry.Essence8.get())
                            .withResult(ItemRegistry.Ring8.get())
                            .withSourceCost(4000) // 500 * 8
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring8.get())
                            .withPedestalItem(8, ItemRegistry.Essence9.get())
                            .withResult(ItemRegistry.Ring9.get())
                            .withSourceCost(4500) // 500 * 9
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemRegistry.Ring9.get())
                            .withPedestalItem(8, ItemRegistry.Essence10.get())
                            .withResult(ItemRegistry.Ring10.get())
                            .withSourceCost(5000) // capped at 5000
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ENCHANTED_GOLDEN_APPLE)
                            .withPedestalItem(4, Essence6.get())
                            .withResult(OBLIVION.get())
                            .build()
                    );

                    // T10 ring & lotus + netherite block at center + "foci"

                    recipes.add(builder()
                            .withReagent(NETHERITE_INGOT)
                            .withPedestalItem(2, Ring10.get())
                            .withPedestalItem(2, Lotus10.get())
                            .withPedestalItem(4, ItemsRegistry.FIRE_ESSENCE)
                            .withSourceCost(1000)
                            .withResult(WARRIOR_RUNE_1.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_INGOT)
                            .withPedestalItem(2, Ring10.get())
                            .withPedestalItem(2, Lotus10.get())
                            .withPedestalItem(4, ItemsRegistry.WATER_ESSENCE)
                            .withSourceCost(1000)
                            .withResult(MAGE_RUNE_1.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_INGOT)
                            .withPedestalItem(2, Ring10.get())
                            .withPedestalItem(2, Lotus10.get())
                            .withPedestalItem(4, ItemsRegistry.EARTH_ESSENCE)
                            .withSourceCost(1000)
                            .withResult(LIFE_RUNE_1.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_INGOT)
                            .withPedestalItem(2, Ring10.get())
                            .withPedestalItem(2, Lotus10.get())
                            .withPedestalItem(4, ItemsRegistry.AIR_ESSENCE)
                            .withSourceCost(1000)
                            .withResult(DEATH_RUNE_1.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, WARRIOR_RUNE_1.get())
                            .withSourceCost(1000)
                            .withResult(WARRIOR_RUNE_2.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, MAGE_RUNE_1.get())
                            .withSourceCost(1000)
                            .withResult(MAGE_RUNE_2.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, LIFE_RUNE_1.get())
                            .withSourceCost(1000)
                            .withResult(LIFE_RUNE_2.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, DEATH_RUNE_1.get())
                            .withSourceCost(1000)
                            .withResult(DEATH_RUNE_2.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, WARRIOR_RUNE_2.get())
                            .withSourceCost(1000)
                            .withResult(WARRIOR_RUNE_3.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, MAGE_RUNE_2.get())
                            .withSourceCost(1000)
                            .withResult(MAGE_RUNE_3.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, LIFE_RUNE_2.get())
                            .withSourceCost(1000)
                            .withResult(LIFE_RUNE_3.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(NETHERITE_BLOCK)
                            .withPedestalItem(2, DEATH_RUNE_2.get())
                            .withSourceCost(1000)
                            .withResult(DEATH_RUNE_3.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(ItemsRegistry.SOURCE_GEM)
                            .withPedestalItem(MAGE_RUNE_3.get())
                            .withPedestalItem(WARRIOR_RUNE_3.get())
                            .withPedestalItem(LIFE_RUNE_3.get())
                            .withPedestalItem(DEATH_RUNE_3.get())
                            .withResult(ETERNITY_RUNE.get())
                            .build()
                    );

                    recipes.add(builder()
                            .withReagent(GOLDEN_HELMET)
                            .withPedestalItem(8, ETERNITY_RUNE.get())
                                    .withResult(DIVINITY.get())
                            .build());

                    /*recipes.add(builder()
                            .withPedestalItem(8, ETERNITY_RUNE.get())
                            .withReagent(ETERNITY_RUNE.get())
                            .withResult(DIVINITY.get())
                            .build()
                    );*/


                }
            }


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
            recipes.add(new ImbuementRecipe("gold_to_copper", Ingredient.of(Items.GOLD_BLOCK), new ItemStack(COPPER_BLOCK), 10000)
                    .withPedestalItem(Essence1.get())
                    .withPedestalItem(Essence1.get())
                    .withPedestalItem(ItemRegistry.Essence4.get())
                    .withPedestalItem(ItemRegistry.Essence4.get())
                    .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
            );

            recipes.add(new ImbuementRecipe("essence_copper", Ingredient.of(COPPER_BLOCK), new ItemStack(Essence1.get()), 10)
                    .withPedestalItem(COPPER_INGOT)
                    .withPedestalItem(COPPER_INGOT)
            );
            recipes.add(new ImbuementRecipe("essence_iron", Ingredient.of(Essence1.get()), new ItemStack(Essence2.get()), 20)
                    .withPedestalItem(IRON_INGOT)
                    .withPedestalItem(IRON_INGOT)

            );
            recipes.add(new ImbuementRecipe("essence_silver", Ingredient.of(Essence2.get()), new ItemStack(Essence3.get()), 40)
                    .withPedestalItem(Essence2.get())
                    .withPedestalItem(Essence2.get())
                    .withPedestalItem(Essence2.get())
                    .withPedestalItem(Essence2.get())
            );
            recipes.add(new ImbuementRecipe("essence_gold", Ingredient.of(Essence3.get()), new ItemStack(Essence4.get()), 80)
                    .withPedestalItem(Essence3.get())
                    .withPedestalItem(Essence3.get())
                    .withPedestalItem(Essence3.get())
                    .withPedestalItem(Essence3.get())
                    .withPedestalItem(GOLD_INGOT)
                    .withPedestalItem(GOLD_INGOT)
            );

            recipes.add(new ImbuementRecipe("essence_crystal", Ingredient.of(Essence4.get()), new ItemStack(Essence5.get()), 160)
                    .withPedestalItem(Essence4.get())
                    .withPedestalItem(Essence4.get())
                    .withPedestalItem(Essence4.get())
                    .withPedestalItem(Essence4.get())
                    .withPedestalItem(Essence4.get())
                    .withPedestalItem(Essence4.get())
                    .withPedestalItem(AMETHYST_BLOCK)
                    .withPedestalItem(AMETHYST_BLOCK)
            );

            recipes.add(new ImbuementRecipe("essence_green", Ingredient.of(Essence5.get()), new ItemStack(Essence6.get()), 150)
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
                    .withPedestalItem(Essence5.get())
            );

            recipes.add(new ImbuementRecipe("essence_red", Ingredient.of(Essence6.get()), new ItemStack(Essence7.get()), 300)
                    .withPedestalItem(Essence6.get())
                    .withPedestalItem(Essence6.get())
                    .withPedestalItem(Essence6.get())
            );

            recipes.add(new ImbuementRecipe("essence_white", Ingredient.of(Essence7.get()), new ItemStack(Essence8.get()), 900)
                    .withPedestalItem(Essence7.get())
                    .withPedestalItem(Essence7.get())
                    .withPedestalItem(Essence7.get())
                    .withPedestalItem(Essence7.get())
            );

            recipes.add(new ImbuementRecipe("essence_yellow", Ingredient.of(Essence8.get()), new ItemStack(Essence9.get()), 900)
                    .withPedestalItem(Essence8.get())
                    .withPedestalItem(Essence8.get())
                    .withPedestalItem(Essence8.get())
                    .withPedestalItem(Essence8.get())
                    .withPedestalItem(Essence8.get())
                    .withPedestalItem(Essence8.get())
            );

            recipes.add(new ImbuementRecipe("essence_purple", Ingredient.of(Essence9.get()), new ItemStack(Essence10.get()), 1500)
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
                    .withPedestalItem(Essence9.get())
            );

            // Gold to Iron recipe.
            recipes.add(new ImbuementRecipe("gold_to_iron", Ingredient.of(GOLD_BLOCK), new ItemStack(IRON_BLOCK), 450)
                    .withPedestalItem(GOLD_INGOT)
                    .withPedestalItem(GOLD_BLOCK)
                    .withPedestalItem(GOLD_NUGGET)
            );

            // Iron to Copper recipe
            recipes.add(new ImbuementRecipe("iron_to_copper", Ingredient.of(IRON_BLOCK), new ItemStack(COPPER_BLOCK), 450)
                    .withPedestalItem(IRON_INGOT)
                    .withPedestalItem(IRON_BLOCK)
                    .withPedestalItem(IRON_BLOCK)
            );

            // Copper to Gold recipe
            recipes.add(new ImbuementRecipe("copper_to_gold", Ingredient.of(COPPER_BLOCK), new ItemStack(GOLD_BLOCK), 450)
                    .withPedestalItem(COPPER_INGOT)
                    .withPedestalItem(COPPER_BLOCK)
                    .withPedestalItem(COPPER_BLOCK)
            );

            //todo : iron to copper and copper to gold



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
