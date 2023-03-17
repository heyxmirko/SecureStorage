package me.heyxmirko.securestorage;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public final class SecureStorage extends JavaPlugin implements Listener {

    private final Set<Player> recentlyBrokenBlocksPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic;
        getLogger().info("Plugin has been loaded!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        recentlyBrokenBlocksPlayers.add(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                recentlyBrokenBlocksPlayers.remove(event.getPlayer());
            }
        }.runTaskLater(this, 5L);
    }

    @EventHandler
    public void inInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if(player.getGameMode().equals(GameMode.SURVIVAL)) {
            if(recentlyBrokenBlocksPlayers.contains(player)) {
                InventoryType inventoryType = event.getInventory().getType();
                if(inventoryType != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    player.sendActionBar(ChatColor.RED+"You cannot open this container yet.");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin has been unloaded!");
    }
}
