package gameapi.manager.room;

import cn.nukkit.Player;
import gameapi.room.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public class RoomVirtualHealthManager {

    protected final Room room;
    private Map<Player, Double> healthMap = new LinkedHashMap<>();
    private double maxHealth = 20.0d;

    public RoomVirtualHealthManager(Room room) {
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
        if (newHealth >= this.maxHealth) {
            newHealth = this.maxHealth;
        }
        this.healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void reduceHealth(Player player, Double value) {
        double newHealth = BigDecimal.valueOf(getHealth(player) - value).setScale(1, RoundingMode.HALF_UP).doubleValue();
        if (newHealth >= this.maxHealth) {
            newHealth = this.maxHealth;
        }
        this.healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void setHealth(Player player, Double value) {
        double newHealth = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
        this.healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void resetHealth(Player player) {
        this.healthMap.put(player, this.maxHealth);
        resetHealthBar(player, this.maxHealth);
    }

    public double getHealth(Player player) {
        return this.healthMap.getOrDefault(player, this.maxHealth);
    }

    public void removePlayer(Player player) {
        this.healthMap.remove(player);
    }

    public void clearAll() {
        this.healthMap = new LinkedHashMap<>();
    }

    protected void resetHealthBar(Player player, double health) {
        BigDecimal decimal = new BigDecimal(health);
        decimal = decimal.divide(new BigDecimal(this.maxHealth), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(player.getMaxHealth()));
        int finalHealthDisplay = decimal.intValueExact();
        if (finalHealthDisplay < 1) {
            finalHealthDisplay = 1;
        }
        player.setHealth(finalHealthDisplay);
    }
}
