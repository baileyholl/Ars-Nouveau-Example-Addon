package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.item.EssenceLotus;
import com.c446.ars_trinkets.item.EssenceRing;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.c446.ars_trinkets.ArsTrinkets.MODID;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);

    static Item.Properties tierEpicCurioProperties;
    static Item.Properties tierRareCurioProperties;
    static Item.Properties tierGenericCurioProperties;

    static {
        tierEpicCurioProperties = new Item.Properties().fireResistant().stacksTo(1).setNoRepair();
        tierRareCurioProperties = new Item.Properties().fireResistant().stacksTo(1);
        tierGenericCurioProperties = new Item.Properties().stacksTo(1);
    }

    static final public DeferredHolder<Item, EssenceLotus> Lotus10 = ITEMS.register("essence_lotus_10", () -> new EssenceLotus(tierEpicCurioProperties, 1024d, 256));
    static final public DeferredHolder<Item, EssenceLotus> Lotus9 = ITEMS.register("essence_lotus_9", () -> new EssenceLotus(tierEpicCurioProperties, 512d, 128));
    static final public DeferredHolder<Item, EssenceLotus> Lotus8 = ITEMS.register("essence_lotus_8", () -> new EssenceLotus(tierEpicCurioProperties, 256d, 64));
    static final public DeferredHolder<Item, EssenceLotus> Lotus7 = ITEMS.register("essence_lotus_7", () -> new EssenceLotus(tierEpicCurioProperties, 128d, 32));
    static final public DeferredHolder<Item, EssenceLotus> Lotus6 = ITEMS.register("essence_lotus_6", () -> new EssenceLotus(tierEpicCurioProperties, 64d, 0));
    static final public DeferredHolder<Item, EssenceLotus> Lotus5 = ITEMS.register("essence_lotus_5", () -> new EssenceLotus(tierEpicCurioProperties, 32d, 0));
    static final public DeferredHolder<Item, EssenceLotus> Lotus4 = ITEMS.register("essence_lotus_4", () -> new EssenceLotus(tierEpicCurioProperties, 16d, 0));
    static final public DeferredHolder<Item, EssenceLotus> Lotus3 = ITEMS.register("essence_lotus_3", () -> new EssenceLotus(tierEpicCurioProperties, 8d, 0));

    static final public DeferredHolder<Item, EssenceRing> Ring10 = ITEMS.register("essence_ring_10", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring9 = ITEMS.register("essence_ring_9", () -> new EssenceRing(tierEpicCurioProperties,  1024, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring8 = ITEMS.register("essence_ring_8", () -> new EssenceRing(tierEpicCurioProperties, 512, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring7 = ITEMS.register("essence_ring_7", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring6 = ITEMS.register("essence_ring_6", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring5 = ITEMS.register("essence_ring_5", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring4 = ITEMS.register("essence_ring_4", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));
    static final public DeferredHolder<Item, EssenceRing> Ring3 = ITEMS.register("essence_ring_3", () -> new EssenceRing(tierEpicCurioProperties, 2048, 0));


}
