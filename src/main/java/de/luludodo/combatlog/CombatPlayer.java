package de.luludodo.combatlog;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class CombatPlayer {
    private static final Map<Player, CombatPlayer> convert = new HashMap<>();
    public static CombatPlayer get(Player player) {
        if (!convert.containsKey(player)) {
            join(player);
            CombatLog.getInstance().getLogger().warning("Player " + player.getName() + " was tracked late!");
        }
        return convert.get(player);
    }

    public static void join(Player player) {
        convert.put(player, new CombatPlayer(player));
    }

    private final Player player;
    private final Stack<CombatPlayer> lastCause = new Stack<>();
    private final Map<CombatPlayer, Long> timers = new HashMap<>();
    private final List<CombatPlayer> connected = new ArrayList<>();
    private boolean wasInCombat = false;
    public CombatPlayer(Player player) {
        this.player = player;
    }

    public void damagedBy(Player player) {
        CombatPlayer attacker = get(player);

        connected.add(attacker);
        lastCause.remove(attacker);
        lastCause.push(attacker);
        timers.put(attacker, combatTime);
        attemptUpdateActionBar();

        attacker.connected.add(this);
        attacker.lastCause.remove(this);
        attacker.lastCause.push(this);
        attacker.timers.put(this, combatTime);
        attacker.attemptUpdateActionBar();
    }

    private static long combatTime;
    private static int barLength;
    public static void reload() {
        combatTime = CombatLog.getInstance().getConfig().getLong("CombatTime");
        barLength = CombatLog.getInstance().getConfig().getInt("BarLength");
    }

    public static void updateAll() {
        convert.values().forEach(CombatPlayer::update);
    }

    public void update() {
        try {
            updateTimeLeft();
        } catch (Exception e) {
            CombatLog.getInstance().getLogger().log(Level.SEVERE, e, () -> "Exception while updating time left for CombatPlayer");
        }
        attemptUpdateActionBar();
    }

    private void attemptUpdateActionBar() {
        try {
            updateActionBar();
        } catch (Exception e) {
            CombatLog.getInstance().getLogger().log(Level.SEVERE, e, () -> "Exception while updating actionbar for CombatPlayer");
        }
    }

    private void updateActionBar() {
        if (lastCause.isEmpty()) {
            if (wasInCombat) {
                wasInCombat = false;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6§lCombat §r§7» §aYou're out of combat!"));
            }
            return;
        }

        wasInCombat = true;

        long timeLeft = timers.get(lastCause.peek());
        float multiplier = (float) timeLeft / combatTime;
        multiplier = Math.max(0, multiplier);
        multiplier = Math.min(1, multiplier);
        int barsLeft = Math.round(barLength * multiplier);

        String bars = "§a" + "|".repeat(barLength - barsLeft) + "§c" + "|".repeat(barsLeft);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6§lCombat §r§7» " + bars + " §c" + timeLeft + "§a seconds"));
    }

    private void updateTimeLeft() {
        timers.entrySet().removeIf(entry -> {
            long timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) {
                CombatPlayer attacker = entry.getKey();
                attacker.connected.remove(this);
                lastCause.remove(attacker);
                return true;
            }
            entry.setValue(timeLeft);
            return false;
        });
    }

    public void died() {
        connected.forEach(hit -> {
            hit.timers.remove(this);
            hit.lastCause.remove(this);
            hit.attemptUpdateActionBar();
        });
        timers.clear();
        lastCause.clear();
        attemptUpdateActionBar();
    }

    public void disconnect() {
        connected.forEach(hit -> {
            hit.timers.remove(this);
            hit.lastCause.remove(this);
            hit.attemptUpdateActionBar();
        });
        if (timers.isEmpty()) {
            delete();
        } else {
            combatLogged = true;
            player.setHealth(0);
        }
    }

    public void delete() {
        convert.remove(player);
    }

    private boolean combatLogged = false;
    public boolean hasCombatLogged() {
        return combatLogged;
    }

    public CombatPlayer lastCause() {
        return lastCause.isEmpty()? null : lastCause.peek();
    }

    public String getDisplayName() {
        return player.getDisplayName();
    }
}
