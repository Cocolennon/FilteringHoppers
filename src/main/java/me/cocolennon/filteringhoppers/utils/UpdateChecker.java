package me.cocolennon.filteringhoppers.utils;

import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private final String projectId;

    public UpdateChecker(JavaPlugin plugin, String projectId) {
        this.plugin = plugin;
        this.projectId = projectId;
    }

    public void getVersion(final Consumer<String> consumer) {
        try (InputStream inputStream = URI.create("https://api.modrinth.com/v2/project/" + this.projectId + "/version").toURL().openStream(); Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            if(scanner.hasNext()) {
                String json = scanner.next();
                String version = JsonParser.parseString(json).getAsJsonArray().get(0).getAsJsonObject().get("version_number").getAsString();
                consumer.accept(version);
            }
        }catch(IOException exception){
            plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
        }
    }
}
