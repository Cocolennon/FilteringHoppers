package me.cocolennon.filteringhoppers;

import me.cocolennon.filteringhoppers.commands.FilteringHoppersCommand;
import me.cocolennon.filteringhoppers.listeners.*;
import me.cocolennon.filteringhoppers.utils.UpdateChecker;
import me.cocolennon.filteringhoppers.utils.Updater;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {
    private String version;
    private boolean usingOldVersion = false;
    private static Main instance;
    private static MiniMessage miniMessage;
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        instance = this;
        miniMessage = MiniMessage.miniMessage();
        checkVersion();
        setUpConfig();
        registerCommandsAndListeners();
        getLogger().info("Plugin enabled!");
    }

    private void checkVersion() {
        new UpdateChecker(this, 111606).getVersion(cVersion -> {
            version = this.getPluginMeta().getVersion();
            if (!getVersion().equals(cVersion)) {
                getLogger().info("You are using an older version of Filtering Hoppers, please update to version " + cVersion);
                usingOldVersion = true;
            }
        });
        if(getConfig().getBoolean("auto-updater-enabled")) {
            Updater updater = new Updater(this, 111606, getFile(), Updater.UpdateType.VERSION_CHECK, true);
            if(updater.getResult().equals(Updater.Result.SUCCESS)) getLogger().info("Update will be applied after next restart!");
        }
    }

    private void setUpConfig(){
        config.addDefault("auto-updater-enabled", true);
        config.addDefault("item-collection.enabled", true);
        config.addDefault("item-collection.mode", "Chunk");
        config.addDefault("item-collection.radius", 16);
        config.addDefault("max-hoppers-per-chunk", 5);
        config.setComments("auto-updater-enabled", List.of("Downloads updates from Spigot automatically"));
        config.setComments("item-collection.mode", List.of("Chunk: Collects items in the same chunk as a hopper", "Radius: Collects items in a radius around a hopper"));
        config.setComments("item-collection.radius", List.of("Radius in which items are collected around a hopper. Only works in radius mode"));
        config.setComments("max-hoppers-per-chunk", List.of("Maximum amount of hoppers per chunk"));
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void registerCommandsAndListeners() {
        getCommand("filteringhoppers").setExecutor(new FilteringHoppersCommand());
        getServer().getPluginManager().registerEvents(new InventoryMoveItemListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockDropItemListener(), instance);
        getServer().getPluginManager().registerEvents(new ItemDropListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), instance);
    }

    public String getVersion() { return instance.version; }
    public boolean getUsingOldVersion() { return instance.usingOldVersion; }
    public static MiniMessage getMiniMessage() { return miniMessage; }
    public static Main getInstance() { return instance; }
}
