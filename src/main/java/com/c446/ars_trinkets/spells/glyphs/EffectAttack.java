package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class EffectAttack extends AbstractEffect implements IDamageEffect {

    public static final EffectAttack INSTANCE = new EffectAttack(ArsTrinkets.prefix("glyph_attack"), "Damages the target as if holding the first 'sword' item in your hotbar.");

    public EffectAttack(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    protected int getDefaultManaCost() {
        return 250;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);

        var entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && shooter.canAttack(livingEntity)) {
            if (shooter instanceof ServerPlayer sp) {
                AtomicReference<ItemStack> firstSword = new AtomicReference<>();

                for (int i = 0; i < 9; i++) {
                    var item = sp.getInventory().items.get(i);

                    var key = Registries.ITEM;
                    var reg = world.registryAccess().registryOrThrow(key);


                    reg.getHolder(BuiltInRegistries.ITEM.getResourceKey(item.getItem()).orElseThrow()).ifPresent(holder -> {
                        if (holder.is(ItemTags.SWORDS)) {
                            firstSword.set(item);
                        }
                    });

                }
                sp.attack(livingEntity);
            } else {

                double damage = getAttributeValueOrZero(shooter, Attributes.ATTACK_DAMAGE);
                double cricChance = getAttributeValueOrZero(shooter, ALObjects.Attributes.CRIT_CHANCE);
                double critDamage = getAttributeValueOrZero(shooter, ALObjects.Attributes.CRIT_DAMAGE);
                boolean isCriticalHit = entity.level().getRandom().nextDouble() < cricChance;
                if (isCriticalHit) damage *= critDamage;
                attemptDamage(world, shooter, spellStats, spellContext, resolver, livingEntity, DamageUtil.source(world, DamageTypesRegistry.GENERIC_SPELL_DAMAGE), (float) damage);
            }
        }
    }

    protected double getAttributeValueOrZero(LivingEntity l, Holder<Attribute> a) {
        if (l.getAttributes().hasAttribute(a)) if (l.getAttribute(a) != null) return l.getAttributeValue(a);
        return 0;
    }
}
