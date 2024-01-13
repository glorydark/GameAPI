package gameapi.room;

import cn.nukkit.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

/**
 * @author glorydark
 */
public class RoomHealthManager {

    private LinkedHashMap<Player, Double> healthMap = new LinkedHashMap<>();

    private double maxHealth = 20.0d;

    protected final Room room;

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
        double newHealth = getHealth(player) + value;
        if (newHealth >= maxHealth) {
            newHealth = maxHealth;
        }
        healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void deductHealth(Player player, Double value) {
        double newHealth = getHealth(player) - value;
        healthMap.put(player, newHealth);
        resetHealthBar(player, newHealth);
    }

    public void setHealth(Player player, Double value) {
        healthMap.put(player, value);
        resetHealthBar(player, value);
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
        decimal = decimal.divide(new BigDecimal(maxHealth), RoundingMode.HALF_UP).multiply(new BigDecimal(player.getMaxHealth()));
        int finalHealthDisplay = decimal.intValueExact();
        if (finalHealthDisplay < 1) {
            finalHealthDisplay = 1;
        }
        player.setHealth(finalHealthDisplay);
    }
}
