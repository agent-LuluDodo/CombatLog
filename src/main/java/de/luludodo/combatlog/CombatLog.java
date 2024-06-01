package de.luludodo.combatlog;

import de.luludodo.combatlog.commands.CombatPlayerReloadCommand;
import de.luludodo.combatlog.listeners.EntityDamagedByEntityListener;
import de.luludodo.combatlog.listeners.PlayerDeathListener;
import de.luludodo.combatlog.listeners.PlayerJoinListener;
import de.luludodo.combatlog.listeners.PlayerQuitListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;
import java.util.TimerTask;

public final class CombatLog extends JavaPlugin {
    private static CombatLog instance;
    public static CombatLog getInstance() {
        return instance;
    }

    private final Timer updateTimer = new Timer("CombatLog updater", true);
    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new EntityDamagedByEntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        getCommand("combatlog-reload").setExecutor(new CombatPlayerReloadCommand());
        getCommand("combatlog-reload").setTabCompleter(new CombatPlayerReloadCommand());

        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CombatPlayer.updateAll();
            }
        }, 0L, 1000L);
        CombatPlayer.reload();

        for (Player player : getServer().getOnlinePlayers()) {
            CombatPlayer.join(player);
        }

        getLogger().info("Enabled CombatLog");
    }

    @Override
    public void onDisable() {
        updateTimer.cancel();

        getLogger().info("Disabled CombatLog");
    }
}
