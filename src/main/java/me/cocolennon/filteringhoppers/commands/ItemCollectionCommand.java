package me.cocolennon.filteringhoppers.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ItemCollectionCommand {
    public static LiteralCommandNode<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("item-collection")
                .then(Commands.literal("toggle")
                        .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.item-collection.toggle"))
                        .executes(ItemCollectionCommand::toggle))
                .then(Commands.literal("mode")
                        .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.item-collection.mode"))
                        .then(Commands.argument("mode", StringArgumentType.word())
                                .suggests(ItemCollectionCommand::getModeSuggestions)
                                .executes(ItemCollectionCommand::setMode)))
                .then(Commands.literal("radius")
                        .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.item-collection.radius"))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                                .executes(ItemCollectionCommand::setRadius)))
                .then(Commands.literal("ignore-y")
                        .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.item-collection.ignore-y"))
                        .executes(ItemCollectionCommand::toggleIgnoreY));
        return root.build();
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Main main = Main.getInstance();
        boolean state = main.config().itemCollection.enabled;
        configSet(main, "item-collection.enabled", !state);
        player.sendMessage(Localization.get(player, "success.item-collection.toggle." + (!state ? "enabled" : "disabled"), true));
        return Command.SINGLE_SUCCESS;
    }

    private static int setMode(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String mode = context.getArgument("mode", String.class);
        configSet(Main.getInstance(), "item-collection.mode", mode);
        player.sendMessage(Localization.get(player, "success.item-collection.toggle." + mode.toLowerCase(), true));
        return Command.SINGLE_SUCCESS;
    }

    private static int setRadius(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        int radius = context.getArgument("radius", Integer.class);
        configSet(Main.getInstance(), "item-collection.radius", radius);
        player.sendMessage(Localization.get(player, "success.item-collection.radius", true, radius));
        return Command.SINGLE_SUCCESS;
    }

    private static int toggleIgnoreY(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Main main = Main.getInstance();
        boolean state = main.config().itemCollection.ignoreY;
        configSet(main, "item-collection.ignore-y", !state);
        player.sendMessage(Localization.get(player, "success.item-collection.toggle." + (!state ? "enabled" : "disabled"), true));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getModeSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        builder.suggest("Chunk", MessageComponentSerializer.message().serialize(Localization.get(player, "tooltips.item-collection-mode.chunk", false)));
        builder.suggest("Radius", MessageComponentSerializer.message().serialize(Localization.get(player, "tooltips.item-collection-mode.radius", false)));
        return builder.buildFuture();
    }

    private static void configSet(Main main, String node, Object value) {
        main.getConfig().set(node, value);
        main.saveConfig();
        main.loadConfig(true);
    }
}
