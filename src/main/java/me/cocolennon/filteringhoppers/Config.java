package me.cocolennon.filteringhoppers;

import me.cocolennon.filteringhoppers.utils.Localization;
import org.bukkit.configuration.file.FileConfiguration;

public final class Config {
    public final String defaultLocale;
    public final boolean autoUpdaterEnabled;
    public final int maxHoppersPerChunk;
    public final ItemCollection itemCollection;

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
