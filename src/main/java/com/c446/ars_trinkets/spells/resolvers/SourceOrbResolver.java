//package com.c446.ars_trinkets.spells.resolvers;
//
//import com.c446.ars_trinkets.ArsTrinkets;
//import com.c446.ars_trinkets.datagen.ComponentRegistry;
//import com.c446.ars_trinkets.item.SourceOrb;
//import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
//import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
//import com.hollingsworth.arsnouveau.api.spell.SpellContext;
//import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.item.ItemStack;
//
//public class SourceOrbResolver extends SpellResolver {
//    public SourceOrbResolver(SpellContext spellContext) {
//        super(spellContext);
//        this.validator= ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
//
//    }
//
//    private final ISpellValidator validator;
//
//    @Override
//    protected boolean enoughMana(LivingEntity entity) {
//        if (entity instanceof ServerPlayer sp) {
//            ItemStack stk = null;
//            if (ArsTrinkets.SOURCE_ORB_CHECK.contains(sp.getUUID())) {
//                if (sp.getMainHandItem().getItem() instanceof SourceOrb) {
//                    stk= sp.getMainHandItem();
//                }
//                else if (sp.getOffhandItem().getItem() instanceof SourceOrb) {
//                    stk = sp.getOffhandItem();
//                }
//            }
//            if (stk==null) return false;
//            var data = stk.get(ComponentRegistry.SOURCE_ORB_COMPONENT);
//
//        }
//
//
//    }
//}
