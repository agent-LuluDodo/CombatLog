package de.luludodo.combatlog.listeners;

import de.luludodo.combatlog.CombatLog;
import de.luludodo.combatlog.CombatPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        CombatPlayer player = CombatPlayer.get(e.getEntity());
        if (player.hasCombatLogged()) {
            e.setDeathMessage(CombatLog.getInstance().getConfig().getString("DeathMsg").replace("%player%", player.getDisplayName()).replace("%killer%", player.lastCause().getDisplayName()));
            player.delete();
        } else {
            player.died();
        }
    }
}
