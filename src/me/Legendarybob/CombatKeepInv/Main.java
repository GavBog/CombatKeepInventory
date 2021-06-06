package me.Legendarybob.CombatKeepInv;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import java.io.File;

public class Main extends JavaPlugin{
    FileConfiguration config = getConfig();
    File cfile;

    public void onEnable() {
        // Config
        this.config.addDefault("LoseToPlayer", Boolean.valueOf(true));
        this.config.addDefault("LoseToNatural", Boolean.valueOf(false));
        this.config.addDefault("KeepLevels", Boolean.valueOf(false));
        this.config.options().copyDefaults(true);
        saveConfig();
        cfile = new File(getDataFolder(), "config.yml");
        // Event Handler
        getServer().getPluginManager().registerEvents(new MyListener(), (Plugin) this);

        // bStats
        int pluginId = 10217;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        // Auto Updater
        SpigetUpdate updater = new SpigetUpdate(this, 88350);
        updater.setVersionComparator(VersionComparator.EQUAL);
        updater.setVersionComparator(VersionComparator.SEM_VER);
        updater.checkForUpdate(new UpdateCallback() {
            @Override
            public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
                getLogger().info("------------------------------------------------");
                getLogger().info("A new CombatKeepInventory Update is Available!");
                getLogger().info("Version: " + newVersion);
                getLogger().info("Download at " + downloadUrl);
                getLogger().info("------------------------------------------------");
            }
            @Override
            public void upToDate() {
                getLogger().info("------------------------------------------------");
                getLogger().info("CombatKeepInventory is Up to Date!");
                getLogger().info("If you run into any problems join our support discord:");
                getLogger().info("https://discord.com/invite/YY9ZsZMtXT");
                getLogger().info("------------------------------------------------");
            }
        });
    }

    public void onDisable() {

    }

    public class MyListener
            implements Listener {
        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            Player player1 = player.getKiller();
            if (player1 instanceof Player) {
                if (Main.this.config.getBoolean("LoseToPlayer")) {
                    if (player.hasPermission("combatkeepinv.alwayskeep")) {
                        event.setKeepInventory(true);
                        event.getDrops().clear();
                        if (Main.this.config.getBoolean("KeepLevels")) {
                            event.setKeepLevel(true);
                        } else {
                            event.setKeepLevel(false);
                        }
                    } else {
                        event.setKeepInventory(false);
                    }
                } else {
                    event.setKeepInventory(true);
                    event.getDrops().clear();
                    if (Main.this.config.getBoolean("KeepLevels")) {
                        event.setKeepLevel(true);
                    } else {
                        event.setKeepLevel(false);
                    }
                }
            } else if (Main.this.config.getBoolean("LoseToNatural")) {
                if (player.hasPermission("combatkeepinv.alwayskeep")) {
                    event.setKeepInventory(true);
                    event.getDrops().clear();
                    if (Main.this.config.getBoolean("KeepLevels")) {
                        event.setKeepLevel(true);
                    } else {
                        event.setKeepLevel(false);
                    }
                } else {
                    event.setKeepInventory(false);
                }
            } else {
                event.setKeepInventory(true);
                event.getDrops().clear();
                if (Main.this.config.getBoolean("KeepLevels")) {
                    event.setKeepLevel(true);
                } else {
                    event.setKeepLevel(false);
                }
            }
        }
    }

    //commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("combatkeepinventoryreload") || label.equalsIgnoreCase("ckireload")) {
            if (sender.hasPermission("combatkeepinv.reload")){
                config = YamlConfiguration.loadConfiguration(cfile);
                sender.sendMessage(ChatColor.GOLD + "CombatKeepInventory has been reloaded!");
            } else{
                sender.sendMessage(ChatColor.RED + "You do not have permission!");
            }
        }
        if (command.getName().equalsIgnoreCase("combatkeepinventoryversion") || label.equalsIgnoreCase("ckiversion")) {
            if (sender.hasPermission("combatkeepinv.version")){
                sender.sendMessage(ChatColor.DARK_GREEN + "Version: " + getDescription().getVersion());
            }
        }
        if (command.getName().equalsIgnoreCase("combatkeepinventorytoggle") || label.equalsIgnoreCase("ckitoggle")) {
            if (sender.hasPermission("combatkeepinv.toggle")) {
                if (args.length == 0) {
                    // No arguments were provided, just "/ckitoggle"
                    sender.sendMessage(ChatColor.RED + "Do /ckitoggle player/natural");
                    return true;
                }
                if (args.length >= 1) {
                    // Some arguments were provided
                    if (args[0].equalsIgnoreCase("player")) {
                        // The first argument is "player", therefore "/ckitoggle player"
                        if (Main.this.config.getBoolean("LoseToPlayer")) {
                            this.getConfig().set("LoseToPlayer", Boolean.valueOf(false));
                            this.saveConfig();
                            sender.sendMessage(ChatColor.GOLD + "Players will no longer lose their items to other Players");
                        } else {
                            this.getConfig().set("LoseToPlayer", Boolean.valueOf(true));
                            this.saveConfig();
                            sender.sendMessage(ChatColor.GOLD + "Players will now lose their items to other Players");
                        }
                        config = YamlConfiguration.loadConfiguration(cfile);
                    }
                    if (args[0].equalsIgnoreCase("natural")) {
                        // The first argument is "natural", therefore "/ckitoggle natural"
                        if (Main.this.config.getBoolean("LoseToNatural")) {
                            this.getConfig().set("LoseToNatural", Boolean.valueOf(false));
                            this.saveConfig();
                            sender.sendMessage(ChatColor.GOLD + "Players will no longer lose their items to Natural Causes");
                        } else {
                            this.getConfig().set("LoseToNatural", Boolean.valueOf(true));
                            this.saveConfig();
                            sender.sendMessage(ChatColor.GOLD + "Players will now lose their items to Natural Causes");
                        }
                        config = YamlConfiguration.loadConfiguration(cfile);
                    }
                }
            }
        }
        return true;
    }
}