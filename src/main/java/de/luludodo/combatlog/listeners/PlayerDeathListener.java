package de.luludodo.combatlog.listeners;

import de.luludodo.combatlog.CombatPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        CombatPlayer player = CombatPlayer.get(e.getEntity());
        if (player.hasCombatLogged()) {
            e.setDeathMessage();
        }
    }
}
