package com.c446.ars_trinkets.datagen;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AttributeTagsProviders extends TagsProvider<Attribute> {
    protected AttributeTagsProviders(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.ATTRIBUTE, pLookupProvider, ArsTrinkets.MODID, existingFileHelper);
    }

    public static final TagKey<Attribute> DIVINITY_BLACKLIST = TagKey.create(Registries.ATTRIBUTE, ArsTrinkets.prefix("divinity_blacklist"));
    public static final TagKey<Attribute> DIVINITY_NO_INCREASE_IF_NEGATIVE = TagKey.create(Registries.ATTRIBUTE, ArsTrinkets.prefix("divinity_no_boost_if_negative"));
    public static final TagKey<Attribute> DIVINITY_NO_INCREASE_IF_POSITIVE = TagKey.create(Registries.ATTRIBUTE, ArsTrinkets.prefix("divinity_no_boost_if_positive"));


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(DIVINITY_BLACKLIST)
                .add(Objects.requireNonNull(Attributes.GRAVITY.getKey()))
                .add(Objects.requireNonNull(Attributes.SCALE.getKey()))
                .add(Objects.requireNonNull(Attributes.SNEAKING_SPEED.getKey()))
                .add(Objects.requireNonNull(Attributes.FALL_DAMAGE_MULTIPLIER.getKey()))
                .add(Objects.requireNonNull(Attributes.JUMP_STRENGTH.getKey()))
                .add(Attributes.SNEAKING_SPEED.getKey())
                .add(PerkAttributes.DRYGMY.getKey())
                .add(PerkAttributes.WIXIE.getKey())
                .add(PerkAttributes.FEATHER.getKey())
                .add(PerkAttributes.WARDING.getKey())
                .add(PerkAttributes.WHIRLIESPRIG.getKey())
                .add(PerkAttributes.WEIGHT.getKey())
                .add(AttributeRegistry.ALL.getKey())
                .addOptional(ResourceLocation.parse("caelus:fall_flying"));

        tag(DIVINITY_NO_INCREASE_IF_NEGATIVE)
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.air_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.abjuration_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.earth_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.elemental_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.fire_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.necromancy_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.manipulation_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.summon_resistance"))
                .addOptional(ResourceLocation.parse("ars_nouveau:sauce.perk.water_resistance"));
    }
}
