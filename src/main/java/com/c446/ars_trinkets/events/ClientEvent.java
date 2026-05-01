package com.c446.ars_trinkets.events;

import com.c446.ars_trinkets.registry.ItemRegistry;
import com.c446.ars_trinkets.tooltips.FlamingClientTooltipComponent;
import com.c446.ars_trinkets.tooltips.SpinningClientTooltipComponent;
import com.c446.ars_trinkets.tooltips.WiggleClientTooltipComponent;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvent {

    // Toggle this to enable/disable debug logging
    private static final boolean DEBUG_TOOLTIPS = true;

    private static void dbg(String msg) {
        if (DEBUG_TOOLTIPS) {
            System.out.println("[ARS-TRINKETS-TOOLTIP] " + msg);
        }
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        List<Either<FormattedText, TooltipComponent>> rebuilt = new ArrayList<>();
        List<SpiralLine> pendingSpiralBlock = new ArrayList<>();
        int groupCounter = 0;
        String phrase = event.getItemStack().getItem().equals(ItemRegistry.DIVINITY.get()) ? "Most Supreme..." : null;

        dbg("=== BEGIN TOOLTIP GATHER for: " + event.getItemStack().getItem() + " ===");

        for (Either<FormattedText, TooltipComponent> element : elements) {
            var left = element.left();
            if (left.isEmpty()) {
                dbg("  [RIGHT element] no FormattedText — flushing spiral block (size=" + pendingSpiralBlock.size() + ") then passing through");
                flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                rebuilt.add(element);
                continue;
            }

            FormattedText formattedText = left.get();
            String rawText = formattedText.getString();
            Style extractedStyle = formattedText instanceof Component comp ? comp.getStyle() : Style.EMPTY;

            dbg("  [LEFT element] rawText=" + rawText.replace("\uE446", "<MARKER>"));

            ParsedMarker parsed = ParsedMarker.from(rawText);
            if (parsed == null) {
                dbg("    -> no marker found, pendingSpiral.size=" + pendingSpiralBlock.size());
                if (pendingSpiralBlock.isEmpty()) {
                    dbg("    -> passthrough as plain");
                    rebuilt.add(element);
                } else {
                    dbg("    -> flushing spiral block before adding plain text");
                    flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                    rebuilt.add(Either.left(asComponent(formattedText, extractedStyle)));
                }
                continue;
            }

            dbg("    -> parsed: type='" + parsed.type() + "' content='" + parsed.content() + "' prefix='" + parsed.prefix() + "' suffix='" + parsed.suffix() + "'");

            if (!parsed.prefix().isEmpty()) {
                dbg("    -> flushing for prefix, then emitting prefix");
                flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                rebuilt.add(Either.left(Component.literal(parsed.prefix()).withStyle(extractedStyle)));
            }

            Component styledContent = Component.literal(parsed.content()).withStyle(extractedStyle);

            if (parsed.type().equals(ArtefactStyles.TYPE_FLAME)) {
                dbg("    -> FLAME: flushing spiral block then emitting FlamingTooltipData");
                flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                rebuilt.add(Either.right(new FlamingClientTooltipComponent.FlamingTooltipData(styledContent)));

            } else if (parsed.type().equalsIgnoreCase(ArtefactStyles.TYPE_SPIRAL_ORIGIN)) {
                dbg("    -> SPIRAL: adding to pendingSpiralBlock (new size=" + (pendingSpiralBlock.size() + 1) + ")");
                pendingSpiralBlock.add(new SpiralLine(styledContent, parsed.type().equalsIgnoreCase(ArtefactStyles.TYPE_SPIRAL_ORIGIN), ArtefactStyles.TYPE_SPIRAL_ORIGIN));

            } else if (parsed.type().equalsIgnoreCase(ArtefactStyles.TYPE_GRAVITY)) {
                dbg("    -> GRAVITY/WIGGLE: flushing spiral block (size=" + pendingSpiralBlock.size() + ") then emitting WiggleTooltipData");
                flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                rebuilt.add(Either.right(new WiggleClientTooltipComponent.WiggleTooltipData(styledContent, phrase, "gravity_" + groupCounter, event.getItemStack())));

            } else {
                dbg("    -> UNKNOWN type '" + parsed.type() + "': flushing spiral block then emitting as plain text");
                flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                rebuilt.add(Either.left(styledContent));
            }

            if (!parsed.suffix().isEmpty()) {
                dbg("    -> flushing for suffix, then emitting suffix");
                flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter++);
                rebuilt.add(Either.left(Component.literal(parsed.suffix()).withStyle(extractedStyle)));
            }
        }

        dbg("  [END OF ELEMENTS] final flush, pendingSpiral.size=" + pendingSpiralBlock.size());
        flushPendingSpiralBlock(pendingSpiralBlock, rebuilt, event, phrase, groupCounter);

        dbg("=== REBUILT " + rebuilt.size() + " elements ===");
        for (int i = 0; i < rebuilt.size(); i++) {
            var r = rebuilt.get(i);
            String desc = r.left().map(t -> "LEFT: " + t.getString())
                    .orElseGet(() -> "RIGHT: " + r.right().get().getClass().getSimpleName());
            dbg("  [" + i + "] " + desc);
        }

        elements.clear();
        elements.addAll(rebuilt);
    }

    private static void flushPendingSpiralBlock(List<SpiralLine> pendingSpiralBlock, List<Either<FormattedText, TooltipComponent>> rebuilt, RenderTooltipEvent.GatherComponents event, String phrase, int groupIndex) {
        if (pendingSpiralBlock.isEmpty()) return;

        // A single S-line has no neighbours to spiral around — treat it as a wiggle
        if (pendingSpiralBlock.size() == 1) {
            SpiralLine lone = pendingSpiralBlock.get(0);
            String groupKey = buildGroupKey(event, groupIndex, lone.text().getString());
            rebuilt.add(Either.right(new WiggleClientTooltipComponent.WiggleTooltipData(
                    lone.text(), phrase, groupKey, event.getItemStack()
            )));
            pendingSpiralBlock.clear();
            return;
        }

        // Multi-line block: proceed with spinning as before
        int explicitCenterIndex = -1;
        int blockWidth = 0;
        var font = Minecraft.getInstance().font;
        for (int i = 0; i < pendingSpiralBlock.size(); i++) {
            SpiralLine line = pendingSpiralBlock.get(i);
            if (explicitCenterIndex == -1 && line.explicitCenter()) explicitCenterIndex = i;
            blockWidth = Math.max(blockWidth, font.width(line.text()));
        }

        int particleOwnerIndex = Math.max(explicitCenterIndex, 0);
        String groupKey = buildGroupKey(event, groupIndex, pendingSpiralBlock.get(particleOwnerIndex).text().getString());

        for (SpiralLine line : pendingSpiralBlock) {
            rebuilt.add(Either.right(new SpinningClientTooltipComponent.SpinningTooltipData(
                    line.text(), phrase, groupKey, blockWidth,
                    pendingSpiralBlock.size(), particleOwnerIndex, explicitCenterIndex,
                    event.getItemStack()
            )));
        }
        pendingSpiralBlock.clear();
    }

    private static String buildGroupKey(RenderTooltipEvent.GatherComponents event, int groupIndex, String anchorText) {
        String itemKey = String.valueOf(event.getItemStack().getItem());
        return itemKey + "#" + groupIndex + "#" + anchorText;
    }

    private static Component asComponent(FormattedText text, Style extractedStyle) {
        if (text instanceof Component component) {
            return component.copy();
        }
        return Component.literal(text.getString()).withStyle(extractedStyle);
    }

    private record SpiralLine(Component text, boolean explicitCenter, String type) {
    }

    private record ParsedMarker(String type, String content, String prefix, String suffix) {
        static ParsedMarker from(String rawText) {
            if (!rawText.contains(ArtefactStyles.MARKER)) {
                return null;
            }

            int first = rawText.indexOf(ArtefactStyles.MARKER);
            int last = rawText.lastIndexOf(ArtefactStyles.MARKER);
            if (first == -1 || last == -1 || first == last || first + 1 >= rawText.length()) {
                return null;
            }

            String type = rawText.substring(first + 1, first + 2);
            String content = rawText.substring(first + 2, last);
            String prefix = rawText.substring(0, first);
            String suffix = rawText.substring(last + 1);
            return new ParsedMarker(type, content, prefix, suffix);
        }
    }

    private static class ArtefactStyles {
        // Using Private Use Area (PUA) to avoid mod conflicts
        public static final String MARKER = "\uE446";
        public static final String TYPE_GRAVITY = "G";
        public static final String TYPE_FLAME = "F";
        public static final String TYPE_SPIRAL_ORIGIN = "S";

        public static MutableComponent gravity(String text) {
            return Component.literal(MARKER + TYPE_GRAVITY + text + MARKER);
        }

        public static MutableComponent flaming(String text) {
            return Component.literal(MARKER + TYPE_FLAME + text + MARKER);
        }

        public static MutableComponent spiral(String text) {
            return Component.literal(MARKER + TYPE_SPIRAL_ORIGIN + text + MARKER);
        }
    }
}
