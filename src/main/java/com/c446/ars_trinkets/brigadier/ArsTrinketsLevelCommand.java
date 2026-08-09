package com.c446.ars_trinkets.brigadier;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.capabilities.PlayerLevelHandling;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ArsTrinketsLevelCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ars-trinkets")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", EntityArgument.player())

                                .then(Commands.literal("get")
                                        .then(Commands.literal("level")
                                                .executes(ArsTrinketsLevelCommand::getLevel))
                                        .then(Commands.literal("xp")
                                                .executes(ArsTrinketsLevelCommand::getXp))
                                        .then(Commands.literal("cores")
                                                .executes(ArsTrinketsLevelCommand::getCores))
                                        .then(Commands.literal("cursed")
                                                .executes(ArsTrinketsLevelCommand::getCursed))
                                )

                                .then(Commands.literal("set")
                                        .then(Commands.literal("level")
                                                .then(Commands.argument(
                                                                "amount",
                                                                IntegerArgumentType.integer(0, 10))
                                                        .executes(ArsTrinketsLevelCommand::setLevel)
                                                )
                                        )
                                        .then(Commands.literal("xp")
                                                .then(Commands.argument(
                                                                "amount",
                                                                IntegerArgumentType.integer(0))
                                                        .executes(ArsTrinketsLevelCommand::setXp)
                                                )
                                        )
                                        .then(Commands.literal("cores")
                                                .then(Commands.argument(
                                                                "amount",
                                                                IntegerArgumentType.integer(0, 9))
                                                        .executes(ArsTrinketsLevelCommand::setCores)
                                                )
                                        )
                                        .then(Commands.literal("cursed")
                                                .then(Commands.argument(
                                                                "value",
                                                                BoolArgumentType.bool())
                                                        .executes(ArsTrinketsLevelCommand::setCursed)
                                                )
                                        )
                                )
                        )
        );
    }

    private static LevelingCapability getCapability(
            CommandContext<CommandSourceStack> ctx,
            ServerPlayer target
    ) {
        try {
            return LevelingCapability.get(target);
        } catch (NullPointerException e) {
            ctx.getSource().sendFailure(
                    Component.literal(
                            "Could not access " + target.getName().getString()
                                    + "'s leveling capability."
                    )
            );

            ArsTrinkets.LOGGER.warn(
                    "Leveling capability was null for player {}",
                    target.getName().getString(),
                    e
            );

            return null;
        }
    }

    // GET Methods

    private static int getLevel(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        target.getName().getString() + "'s level: " + cap.level
                ),
                false
        );

        return cap.level;
    }

    private static int getXp(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        target.getName().getString() + "'s XP (souls): " + cap.souls
                ),
                false
        );

        return (int) cap.souls;
    }

    private static int getCores(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        target.getName().getString() + "'s cores: " + cap.cores
                ),
                false
        );

        return cap.cores;
    }

    private static int getCursed(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        target.getName().getString()
                                + " is "
                                + (cap.cursed ? "cursed" : "not cursed")
                ),
                false
        );

        return cap.cursed ? 1 : 0;
    }

    // SET Methods

    private static int setLevel(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        cap.level = (short) amount;
        PlayerLevelHandling.updateBonusGlyphSlots(target);

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        "Set " + target.getName().getString() + "'s level to " + amount
                ),
                true
        );

        ArsTrinkets.LOGGER.info(
                "Set {}'s level to {}",
                target.getName().getString(),
                amount
        );

        return amount;
    }

    private static int setXp(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        cap.souls = amount;

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        "Set " + target.getName().getString()
                                + "'s XP (souls) to " + amount
                ),
                true
        );

        ArsTrinkets.LOGGER.info(
                "Set {}'s souls to {}",
                target.getName().getString(),
                amount
        );

        return amount;
    }

    private static int setCores(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        cap.cores = amount;

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        "Set " + target.getName().getString() + "'s cores to " + amount
                ),
                true
        );

        ArsTrinkets.LOGGER.info(
                "Set {}'s cores to {}",
                target.getName().getString(),
                amount
        );

        return amount;
    }

    private static int setCursed(
            CommandContext<CommandSourceStack> ctx
    ) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = getCapability(ctx, target);

        if (cap == null) {
            return 0;
        }

        boolean value = BoolArgumentType.getBool(ctx, "value");
        cap.cursed = value;

        ctx.getSource().sendSuccess(
                () -> Component.literal(
                        "Set " + target.getName().getString()
                                + " as "
                                + (value ? "cursed" : "not cursed")
                ),
                true
        );

        ArsTrinkets.LOGGER.info(
                "Set {}'s cursed status to {}",
                target.getName().getString(),
                value
        );

        return value ? 1 : 0;
    }
}
