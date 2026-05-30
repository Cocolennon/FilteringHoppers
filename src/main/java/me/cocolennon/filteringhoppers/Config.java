package me.cocolennon.filteringhoppers;

import me.cocolennon.filteringhoppers.utils.Localization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class Config {
    public final String defaultLocale;
    public final boolean autoUpdaterEnabled;
    public final int maxHoppersPerChunk;
    public final ItemCollection itemCollection;

    public final int hopperRate;

    public Config(Main plugin) {
        FileConfiguration config = plugin.getConfig();
        this.defaultLocale = config.getString("default-locale");
        Localization.init(plugin, defaultLocale);
        this.autoUpdaterEnabled = config.getBoolean("auto-updater-enabled");
        this.maxHoppersPerChunk = config.getInt("max-hoppers-per-chunk");
        boolean enabled = config.getBoolean("item-collection.enabled");
        String mode = config.getString("item-collection.mode");
        int radius = config.getInt("item-collection.radius");
        boolean ignoreY = config.getBoolean("item-collection.ignore-y");
        this.itemCollection = new ItemCollection(enabled, mode, radius, ignoreY);
        this.hopperRate = getHopperRate();
    }

    public static int getHopperRate() {
        File spigotFile = new File(Bukkit.getServer().getWorldContainer().getParentFile(), "spigot.yml");
        YamlConfiguration spigotConfig = YamlConfiguration.loadConfiguration(spigotFile);
        return spigotConfig.getInt("world-settings.default.hopper-amount", 1);
    }

    public static final class ItemCollection {
        public final boolean enabled;
        public final String mode;
        public final int radius;
        public final boolean ignoreY;

        private ItemCollection(boolean enabled, String mode, int radius, boolean ignoreY) {
            this.enabled = enabled;
            this.mode = mode;
            this.radius = radius;
            this.ignoreY = ignoreY;
        }
    }
}
