package com.c446.ars_trinkets.util;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PhantomStateManager {
    public static final Map<UUID, PhantomData> ACTIVE_TRICKS = new ConcurrentHashMap<>();
    public static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static boolean isSpellContext() {
        return WALKER.walk(frames ->
            frames.anyMatch(frame -> {
                String className = frame.getDeclaringClass().getName();
                return className.startsWith("com.hollingsworth.arsnouveau.api.spell")
                    || className.startsWith("alexthw.not_enough_glyphs");
            })
        );
    }

    public static void register(UUID uuid, PhantomData data) {
        ACTIVE_TRICKS.put(uuid, data);
    }

    public static void unregister(UUID uuid) {
        ACTIVE_TRICKS.remove(uuid);
    }

    @Nullable
    public static PhantomData get(UUID uuid) {
        return ACTIVE_TRICKS.get(uuid);
    }

    public static class PhantomData {
        public @Nullable Vec3 look;
        public @Nullable Vec3 position;

        public PhantomData() {
        }

        public PhantomData withLook(Vec3 look) {
            this.look = look;
            return this;
        }

        public PhantomData withPosition(Vec3 position) {
            this.position = position;
            return this;
        }
    }
}
