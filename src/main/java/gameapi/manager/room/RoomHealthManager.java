package gameapi.manager.room;

import cn.nukkit.Player;
import gameapi.room.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

/**
 * @author glorydark
 */
public class RoomHealthManager {

    protected final Room room;
    private LinkedHashMap<Player, Double> healthMap = new LinkedHashMap<>();
    private double maxHealth = 20.0d;

    public RoomHealthManager(Room room) {
        this.room = room;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void addHealth(Player player, Double value) {
        double newHealth = BigDecimal.valueOf(getHealth(player) + value).setScale(1, RoundingMode.HALF_UP).doubleValue();
        if (newHealth >= maxHealth) {
            newHealth = maxHealth;
        }
        healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void reduceHealth(Player player, Double value) {
        double newHealth = BigDecimal.valueOf(getHealth(player) - value).setScale(1, RoundingMode.HALF_UP).doubleValue();
        if (newHealth >= maxHealth) {
            newHealth = maxHealth;
        }
        healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void setHealth(Player player, Double value) {
        double newHealth = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
        healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public double getHealth(Player player) {
        return healthMap.getOrDefault(player, maxHealth);
    }

    public void removePlayer(Player player) {
        healthMap.remove(player);
    }

    public void clearAll() {
        healthMap = new LinkedHashMap<>();
    }

    protected void resetHealthBar(Player player, double health) {
        BigDecimal decimal = new BigDecimal(health);
        decimal = decimal.divide(new BigDecimal(maxHealth), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(player.getMaxHealth()));
        int finalHealthDisplay = decimal.intValueExact();
        if (finalHealthDisplay < 1) {
            finalHealthDisplay = 1;
        }
        player.setHealth(finalHealthDisplay);
    }
}
