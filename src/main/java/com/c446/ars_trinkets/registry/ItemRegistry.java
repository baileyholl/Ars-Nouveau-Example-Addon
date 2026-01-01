package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.item.EssenceConsumable;
import com.c446.ars_trinkets.item.EssenceLotus;
import com.c446.ars_trinkets.item.EssenceRing;
import com.c446.ars_trinkets.item.OmnipotenceRune;
import com.c446.ars_trinkets.item.runes.DeathRune;
import com.c446.ars_trinkets.item.runes.LifeRune;
import com.c446.ars_trinkets.item.runes.MageRune;
import com.c446.ars_trinkets.item.runes.WarriorRune;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.c446.ars_trinkets.ArsTrinkets.MODID;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);

    static Item.Properties tierEpicCurioProperties;
    static Item.Properties tierRareCurioProperties;
    static Item.Properties tierUncommonCurioProperties;
    static Item.Properties tierCommonCurioProperties;

    static Item.Properties manaCoreProperties = new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().fast().nutrition(0).saturationModifier(0).build()).stacksTo(16);

    static {
        tierEpicCurioProperties = new Item.Properties().fireResistant().stacksTo(1).setNoRepair().rarity(Rarity.EPIC);
        tierRareCurioProperties = new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.RARE);
        tierUncommonCurioProperties = new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.UNCOMMON);
        tierCommonCurioProperties = new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.COMMON);
    }

    static final public DeferredHolder<Item, EssenceLotus> Lotus10 = ITEMS.register("essence_lotus_10", () -> new EssenceLotus(tierEpicCurioProperties, 1024d, 256));
    static final public DeferredHolder<Item, EssenceLotus> Lotus9 = ITEMS.register("essence_lotus_9", () -> new EssenceLotus(tierEpicCurioProperties, 512d, 128));
    static final public DeferredHolder<Item, EssenceLotus> Lotus8 = ITEMS.register("essence_lotus_8", () -> new EssenceLotus(tierRareCurioProperties, 256d, 64));
    static final public DeferredHolder<Item, EssenceLotus> Lotus7 = ITEMS.register("essence_lotus_7", () -> new EssenceLotus(tierRareCurioProperties, 128d, 32));
    static final public DeferredHolder<Item, EssenceLotus> Lotus6 = ITEMS.register("essence_lotus_6", () -> new EssenceLotus(tierUncommonCurioProperties, 64d, 0));
    static final public DeferredHolder<Item, EssenceLotus> Lotus5 = ITEMS.register("essence_lotus_5", () -> new EssenceLotus(tierUncommonCurioProperties, 32d, 0));
    static final public DeferredHolder<Item, EssenceLotus> Lotus4 = ITEMS.register("essence_lotus_4", () -> new EssenceLotus(tierCommonCurioProperties, 16d, 0));
    static final public DeferredHolder<Item, EssenceLotus> Lotus3 = ITEMS.register("essence_lotus_3", () -> new EssenceLotus(tierCommonCurioProperties, 8d, 0));

    static final public DeferredHolder<Item, EssenceRing> Ring10 = ITEMS.register("essence_ring_10", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring9 = ITEMS.register("essence_ring_9", () -> new EssenceRing(tierEpicCurioProperties, 1536, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring8 = ITEMS.register("essence_ring_8", () -> new EssenceRing(tierRareCurioProperties, 1024, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring7 = ITEMS.register("essence_ring_7", () -> new EssenceRing(tierRareCurioProperties, 768, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring6 = ITEMS.register("essence_ring_6", () -> new EssenceRing(tierUncommonCurioProperties, 512, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring5 = ITEMS.register("essence_ring_5", () -> new EssenceRing(tierUncommonCurioProperties, 384, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring4 = ITEMS.register("essence_ring_4", () -> new EssenceRing(tierCommonCurioProperties, 256, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring3 = ITEMS.register("essence_ring_3", () -> new EssenceRing(tierCommonCurioProperties, 128, 0));

    static final public Item.Properties essenceProperties = new Item.Properties().fireResistant();

    static final public DeferredHolder<Item, EssenceConsumable> Essence1 = ITEMS.register("essence_1", () -> new EssenceConsumable(essenceProperties, 1, 25));
    static final public DeferredHolder<Item, EssenceConsumable> Essence2 = ITEMS.register("essence_2", () -> new EssenceConsumable(essenceProperties, 2, 50));
    static final public DeferredHolder<Item, EssenceConsumable> Essence3 = ITEMS.register("essence_3", () -> new EssenceConsumable(essenceProperties, 3, 75));
    static final public DeferredHolder<Item, EssenceConsumable> Essence4 = ITEMS.register("essence_4", () -> new EssenceConsumable(essenceProperties, 4, 100));
    static final public DeferredHolder<Item, EssenceConsumable> Essence5 = ITEMS.register("essence_5", () -> new EssenceConsumable(essenceProperties, 5, 150));
    static final public DeferredHolder<Item, EssenceConsumable> Essence6 = ITEMS.register("essence_6", () -> new EssenceConsumable(essenceProperties, 6, 200));
    static final public DeferredHolder<Item, EssenceConsumable> Essence7 = ITEMS.register("essence_7", () -> new EssenceConsumable(essenceProperties, 7, 250));
    static final public DeferredHolder<Item, EssenceConsumable> Essence8 = ITEMS.register("essence_8", () -> new EssenceConsumable(essenceProperties, 8, 300));
    static final public DeferredHolder<Item, EssenceConsumable> Essence9 = ITEMS.register("essence_9", () -> new EssenceConsumable(essenceProperties, 9, 400));
    static final public DeferredHolder<Item, EssenceConsumable> Essence10 = ITEMS.register("essence_10", () -> new EssenceConsumable(essenceProperties, 10, 500));

    static final public DeferredHolder<Item, MageRune> MAGE_RUNE_1 = ITEMS.register("mage_rune_lesser", () -> new MageRune(tierEpicCurioProperties, 1));
    static final public DeferredHolder<Item, MageRune> MAGE_RUNE_2 = ITEMS.register("mage_rune", () -> new MageRune(tierEpicCurioProperties, 2));
    static final public DeferredHolder<Item, MageRune> MAGE_RUNE_3 = ITEMS.register("mage_rune_greater", () -> new MageRune(tierEpicCurioProperties, 3));

    static final public DeferredHolder<Item, WarriorRune> WARRIOR_RUNE_1 = ITEMS.register("warrior_rune_lesser", () -> new WarriorRune(tierEpicCurioProperties, 1));
    static final public DeferredHolder<Item, WarriorRune> WARRIOR_RUNE_2 = ITEMS.register("warrior_rune", () -> new WarriorRune(tierEpicCurioProperties, 2));
    static final public DeferredHolder<Item, WarriorRune> WARRIOR_RUNE_3 = ITEMS.register("warrior_rune_greater", () -> new WarriorRune(tierEpicCurioProperties, 3));

    static final public DeferredHolder<Item, LifeRune> LIFE_RUNE_1 = ITEMS.register("life_rune_lesser", () -> new LifeRune(tierEpicCurioProperties, 1));
    static final public DeferredHolder<Item, LifeRune> LIFE_RUNE_2 = ITEMS.register("life_rune", () -> new LifeRune(tierEpicCurioProperties, 2));
    static final public DeferredHolder<Item, LifeRune> LIFE_RUNE_3 = ITEMS.register("life_rune_greater", () -> new LifeRune(tierEpicCurioProperties, 3));

    static final public DeferredHolder<Item, DeathRune> DEATH_RUNE_1 = ITEMS.register("death_rune_lesser", () -> new DeathRune(tierEpicCurioProperties, 1));
    static final public DeferredHolder<Item, DeathRune> DEATH_RUNE_2 = ITEMS.register("death_rune", () -> new DeathRune(tierEpicCurioProperties, 2));
    static final public DeferredHolder<Item, DeathRune> DEATH_RUNE_3 = ITEMS.register("death_rune_greater", () -> new DeathRune(tierEpicCurioProperties, 3));

    static final public DeferredHolder<Item, OmnipotenceRune> DIVINITY = ITEMS.register("omnipotence_crown", () -> new OmnipotenceRune(tierEpicCurioProperties, 5));
}
