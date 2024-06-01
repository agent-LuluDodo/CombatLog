package de.luludodo.combatlog.listeners;

import de.luludodo.combatlog.CombatPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamagedByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Player attacker) {
            CombatPlayer.get(damaged).damagedBy(attacker);
        }
    }
}
