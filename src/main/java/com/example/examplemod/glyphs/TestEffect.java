package com.example.examplemod.glyphs;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class TestEffect extends AbstractEffect {

    public static TestEffect INSTANCE = new TestEffect("test", "Test");

    public TestEffect(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext);
        System.out.println("Hello from my resolve!");
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }
}
