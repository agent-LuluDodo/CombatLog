package de.luludodo.combatlog.listeners;

import de.luludodo.combatlog.CombatPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        CombatPlayer.get(e.getPlayer()).disconnect();
    }
}
