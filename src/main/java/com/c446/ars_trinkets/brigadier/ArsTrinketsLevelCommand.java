package com.c446.ars_trinkets.brigadier;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
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
                        .requires(source -> source.hasPermission(2)) // OP level 2
                        .then(Commands.argument("target", EntityArgument.player())
                                // GET subcommands
                                .then(Commands.literal("get")
                                        .then(Commands.literal("level")
                                                .executes(ArsTrinketsLevelCommand::getLevel)
                                        )
                                        .then(Commands.literal("xp")
                                                .executes(ArsTrinketsLevelCommand::getXp)
                                        )
                                        .then(Commands.literal("cores")
                                                .executes(ArsTrinketsLevelCommand::getCores)
                                        )
                                        .then(Commands.literal("cursed")
                                                .executes(ArsTrinketsLevelCommand::getCursed)
                                        )
                                )
                                // SET subcommands
                                .then(Commands.literal("set")
                                        .then(Commands.literal("level")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10))
                                                        .executes(ArsTrinketsLevelCommand::setLevel)
                                                )
                                        )
                                        .then(Commands.literal("xp")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                        .executes(ArsTrinketsLevelCommand::setXp)
                                                )
                                        )
                                        .then(Commands.literal("cores")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 9))
                                                        .executes(ArsTrinketsLevelCommand::setCores)
                                                )
                                        )
                                        .then(Commands.literal("cursed")
                                                .then(Commands.argument("value", BoolArgumentType.bool())
                                                        .executes(ArsTrinketsLevelCommand::setCursed)
                                                )
                                        )
                                )
                        )
        );
    }

    // GET Methods
    private static int getLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = LevelingCapability.get(target);

        ctx.getSource().sendSuccess(
                () -> Component.literal(target.getName().getString() + "'s level: " + cap.level),
                false
        );

        return cap.level;
    }

    private static int getXp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = LevelingCapability.get(target);

        ctx.getSource().sendSuccess(
                () -> Component.literal(target.getName().getString() + "'s XP (souls): " + cap.souls),
                false
        );

        return (int) cap.souls;
    }

    private static int getCores(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = LevelingCapability.get(target);

        ctx.getSource().sendSuccess(
                () -> Component.literal(target.getName().getString() + "'s cores: " + cap.cores),
                false
        );

        return cap.cores;
    }

    private static int getCursed(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        LevelingCapability cap = LevelingCapability.get(target);

        ctx.getSource().sendSuccess(
                () -> Component.literal(target.getName().getString() + " is " + (cap.cursed ? "cursed" : "not cursed")),
                false
        );

        return cap.cursed ? 1 : 0;
    }

    // SET Methods
    private static int setLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        LevelingCapability cap = LevelingCapability.get(target);

        cap.level = (short) amount;

        ctx.getSource().sendSuccess(
                () -> Component.literal("Set " + target.getName().getString() + "'s level to " + amount),
                true
        );

        ArsTrinkets.LOGGER.info("Set {}'s level to {}", target.getName().getString(), amount);

        return amount;
    }

    private static int setXp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        LevelingCapability cap = LevelingCapability.get(target);

        cap.souls = amount;

        ctx.getSource().sendSuccess(
                () -> Component.literal("Set " + target.getName().getString() + "'s XP (souls) to " + amount),
                true
        );

        ArsTrinkets.LOGGER.info("Set {}'s souls to {}", target.getName().getString(), amount);

        return amount;
    }

    private static int setCores(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        LevelingCapability cap = LevelingCapability.get(target);

        cap.cores = amount;

        ctx.getSource().sendSuccess(
                () -> Component.literal("Set " + target.getName().getString() + "'s cores to " + amount),
                true
        );

        ArsTrinkets.LOGGER.info("Set {}'s cores to {}", target.getName().getString(), amount);

        return amount;
    }

    private static int setCursed(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        boolean value = BoolArgumentType.getBool(ctx, "value");
        LevelingCapability cap = LevelingCapability.get(target);

        cap.cursed = value;

        ctx.getSource().sendSuccess(
                () -> Component.literal("Set " + target.getName().getString() + " as " + (value ? "cursed" : "not cursed")),
                true
        );

        ArsTrinkets.LOGGER.info("Set {}'s cursed status to {}", target.getName().getString(), value);

        return value ? 1 : 0;
    }
}