package com.c446.ars_trinkets.datagen;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.glyphs.AirSwordEffect;
import com.c446.ars_trinkets.glyphs.SunFlare;
import com.c446.ars_trinkets.glyphs.WaterSpear;
import com.c446.ars_trinkets.glyphs.filters.RandomCancel;
import com.c446.ars_trinkets.glyphs.forms.AuraForm;
import com.c446.ars_trinkets.registry.ItemRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ModModelsProvider extends ItemModelProvider {
    public ModModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ArsTrinkets.MODID, existingFileHelper);
    }

    public ItemModelBuilder handHeld(Item item) {
        return handHeld(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }



    public ItemModelBuilder handHeld(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder glyph(AbstractSpellPart glyph){
        return basicItem(glyph.glyphItem);
    }

    @Override
    protected void registerModels() {
        glyph(AirSwordEffect.INSTANCE);
        glyph(SunFlare.INSTANCE);
        glyph(WaterSpear.INSTANCE);
        //glyph(RandomCancel.HALF);
        //glyph(RandomCancel.QUARTER);
        //glyph(RandomCancel.THREE_FOURTHS);
        glyph(AuraForm.INSTANCE);

        basicItem(ItemRegistry.Lotus3.getId());
        basicItem(ItemRegistry.Lotus4.getId());
        basicItem(ItemRegistry.Lotus5.getId());
        basicItem(ItemRegistry.Lotus6.getId());
        basicItem(ItemRegistry.Lotus7.getId());
        basicItem(ItemRegistry.Lotus8.getId());
        basicItem(ItemRegistry.Lotus9.getId());
        basicItem(ItemRegistry.Lotus10.getId());

        basicItem(ItemRegistry.Ring3.getId());
        basicItem(ItemRegistry.Ring4.getId());
        basicItem(ItemRegistry.Ring5.getId());
        basicItem(ItemRegistry.Ring6.getId());
        basicItem(ItemRegistry.Ring7.getId());
        basicItem(ItemRegistry.Ring8.getId());
        basicItem(ItemRegistry.Ring9.getId());
        basicItem(ItemRegistry.Ring10.getId());

        basicItem(ItemRegistry.Essence1.getId());
        basicItem(ItemRegistry.Essence2.getId());
        basicItem(ItemRegistry.Essence3.getId());
        basicItem(ItemRegistry.Essence4.getId());
        basicItem(ItemRegistry.Essence5.getId());
        basicItem(ItemRegistry.Essence6.getId());
        basicItem(ItemRegistry.Essence7.getId());
        basicItem(ItemRegistry.Essence8.getId());
        basicItem(ItemRegistry.Essence9.getId());
        basicItem(ItemRegistry.Essence10.getId());

        basicItem(ItemRegistry.MAGE_RUNE_1.getId());
        basicItem(ItemRegistry.MAGE_RUNE_2.getId());
        basicItem(ItemRegistry.MAGE_RUNE_3.getId());

        basicItem(ItemRegistry.WARRIOR_RUNE_1.getId());
        basicItem(ItemRegistry.WARRIOR_RUNE_2.getId());
        basicItem(ItemRegistry.WARRIOR_RUNE_3.getId());

        basicItem(ItemRegistry.LIFE_RUNE_1.getId());
        basicItem(ItemRegistry.LIFE_RUNE_2.getId());
        basicItem(ItemRegistry.LIFE_RUNE_3.getId());

        basicItem(ItemRegistry.DEATH_RUNE_1.getId());
        basicItem(ItemRegistry.DEATH_RUNE_2.getId());
        basicItem(ItemRegistry.DEATH_RUNE_3.getId());

        basicItem(ItemRegistry.DIVINITY.getId());
    }
}
