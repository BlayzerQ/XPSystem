package ru.blayzer.XPSystem;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class XPSystem extends JavaPlugin {
    Plugin plugin = this;
    XPSystem customLvl = this;

    public void onEnable() {
        plugin.saveDefaultConfig();
        this.getCommand("xpsystem").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new LevelHandler(plugin), this);
        getServer().getPluginManager().registerEvents(new LoreHandler(plugin), this);
        getLogger().info("XPSystem by Blayzer has been enabled!");
    }

    public void onDisable() {
        getLogger().info("XPSystem by Blayzer has been disabled!");
    }
}
