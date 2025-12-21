package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class PlayerLevelHandling {
    //TODO: stop using the old hacky way and use AttributeFix to override default maximum value.
    @SubscribeEvent
    public static void addMana(MaxManaCalcEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.hasData(CapabilityRegistry.LEVEL_CAP)) {
                var cap = p.getData(CapabilityRegistry.LEVEL_CAP);

                if (cap.level == 0) return;

                else {
                    e.setMax(e.getMax() + cap.getBonusMana());
                }
            }
        }
    }

    //TODO: patchouli book, add titles back in, on level-up broadcasting etc...
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre d){
        if (d.getSource().getEntity() instanceof Player attacker && attacker.hasData(CapabilityRegistry.LEVEL_CAP)){
            d.setNewDamage((float) (d.getNewDamage() * attacker.getData(CapabilityRegistry.LEVEL_CAP).getDamageMult()));
        }

        if (d.getEntity() instanceof Player victim) {
            d.setNewDamage((float) (d.getNewDamage() / victim.getData(CapabilityRegistry.LEVEL_CAP).getDamageMult()));
        }
    }
}
