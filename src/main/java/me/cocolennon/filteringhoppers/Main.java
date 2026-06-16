package me.cocolennon.filteringhoppers;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.cocolennon.filteringhoppers.commands.FilteringHoppersCommand;
import me.cocolennon.filteringhoppers.commands.ItemCollectionCommand;
import me.cocolennon.filteringhoppers.listeners.*;
import me.cocolennon.filteringhoppers.utils.MetricsUtil;
import me.cocolennon.filteringhoppers.utils.UpdateChecker;
import me.cocolennon.filteringhoppers.utils.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {
    private String version;
    private boolean usingOldVersion = false;
    private static Main instance;
    private Config config;

    @Override
    public void onEnable() {
        instance = this;
        loadConfig(false);
        registerCommands();
        registerListeners();
        MetricsUtil.register(instance);
        getLogger().info("Plugin enabled!");
        checkVersion();
    }

    private void checkVersion() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> { // we don't want to hold the server up for update check
            new UpdateChecker(this, "filtering-hoppers").getVersion(cVersion -> {
                version = this.getPluginMeta().getVersion();
                if (!getVersion().equals(cVersion)) {
                    getLogger().info("You are using an older version of Filtering Hoppers, please update to version " + cVersion);
                    usingOldVersion = true;
                }
            });
            if(config.autoUpdaterEnabled) {
                Updater updater = new Updater(this, "filtering-hoppers", getFile(), Updater.UpdateType.CHECK_DOWNLOAD, true);
                if(updater.getResult().equals(Updater.Result.SUCCESS)) getLogger().info("Update will be applied after next restart!");
            }
        });
    }

    public void loadConfig(boolean reload) {
        if(!reload) {
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        reloadConfig();
        config = new Config(this);
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(FilteringHoppersCommand.register(), List.of("fh"));
            commands.registrar().register(ItemCollectionCommand.register());
        });
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new InventoryMoveItemListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryPickupItemListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockDropItemListener(), instance);
        getServer().getPluginManager().registerEvents(new ItemDropListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), instance);
    }

    public Config config() { return config; }
    public String getVersion() { return instance.version; }
    public boolean getUsingOldVersion() { return instance.usingOldVersion; }
    public static Main getInstance() { return instance; }
}
