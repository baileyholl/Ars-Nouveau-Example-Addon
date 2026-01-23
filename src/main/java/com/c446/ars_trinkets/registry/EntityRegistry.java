package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.entities.red_lightning.RedLightning;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.c446.ars_trinkets.ArsTrinkets.MODID;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RedLightning>> RED_LIGHTNING =
            registerEntity(
                    "red_lightning",
                    EntityType.Builder.of(RedLightning::new, MobCategory.AMBIENT)
                            .noSave()
                            .fireImmune()
            );

    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(MODID + ":" + name));
    }
}
