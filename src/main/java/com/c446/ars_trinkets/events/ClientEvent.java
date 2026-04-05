package com.c446.ars_trinkets.events;

import com.c446.ars_trinkets.tooltips.FlamingClientTooltipComponent;
import com.c446.ars_trinkets.tooltips.SpinningClientTooltipComponent;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();

        for (int i = 0; i < elements.size(); i++) {
            var element = elements.get(i);
            final int index = i;

            element.left().ifPresent(formattedText -> {
                String rawText = formattedText.getString();

                if (rawText.contains(ArtefactStyles.MARKER)) {
                    int first = rawText.indexOf(ArtefactStyles.MARKER);
                    int last = rawText.lastIndexOf(ArtefactStyles.MARKER);

                    if (first != -1 && last != -1 && first != last) {
                        String type = rawText.substring(first + 1, first + 2);
                        String content = rawText.substring(first + 2, last);

                        String prefix = rawText.substring(0, first);
                        String suffix = rawText.substring(last + 1);

                        // --- STYLE EXTRACTION ---
                        // Since FormattedText doesn't have .getStyle(), we check if it's a Component.
                        // If it's not, we fall back to Style.EMPTY.
                        Style extractedStyle = Style.EMPTY;
                        if (formattedText instanceof Component comp) {
                            extractedStyle = comp.getStyle();
                        }

                        elements.remove(index);
                        int currentPos = index;

                        if (!prefix.isEmpty()) {
                            elements.add(currentPos++, Either.left(Component.literal(prefix).withStyle(extractedStyle)));
                        }

                        // Reconstruct with the extracted style
                        Component styledContent = Component.literal(content).withStyle(extractedStyle);

                        if (type.equals(ArtefactStyles.TYPE_FLAME)) {
                            elements.add(currentPos++, Either.right(new FlamingClientTooltipComponent.FlamingTooltipData(styledContent)));
                        } else if (type.equals(ArtefactStyles.TYPE_GRAVITY)){
                            elements.add(currentPos++, Either.right(new SpinningClientTooltipComponent.SpinningTooltipData(styledContent)));
                        }

                        if (!suffix.isEmpty()) {
                            elements.add(currentPos, Either.left(Component.literal(suffix).withStyle(extractedStyle)));
                        }
                    }
                }
            });
        }
    }

    private static class ArtefactStyles {
        // Using Private Use Area (PUA) to avoid mod conflicts
        public static final String MARKER = "\uE446";
        public static final String TYPE_GRAVITY = "G";
        public static final String TYPE_FLAME = "F";

        public static MutableComponent gravity(String text) {
            return Component.literal(MARKER + TYPE_GRAVITY + text + MARKER);
        }

        public static MutableComponent flaming(String text) {
            return Component.literal(MARKER + TYPE_FLAME + text + MARKER);
        }
    }
}
