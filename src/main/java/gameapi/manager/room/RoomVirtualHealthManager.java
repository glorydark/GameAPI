package gameapi.manager.room;

import cn.nukkit.Player;
import gameapi.annotation.Description;
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
    private double defaultMaxHealth = 20.0d;
    private boolean alive = true;
    private final Map<Player, Double> maxHealthMap = new LinkedHashMap<>();

    public RoomVirtualHealthManager(Room room) {
        this.room = room;
    }

    @Deprecated
    @Description(usage = "This method is replaced by getDefaultMaxHealth()")
    public double getMaxHealth() {
        return this.defaultMaxHealth;
    }

    @Deprecated
    @Description(usage = "This method is replaced by setDefaultMaxHealth(double maxHealth)")
    public void setMaxHealth(double maxHealth) {
        this.defaultMaxHealth = maxHealth;
    }

    public double getDefaultMaxHealth() {
        return this.defaultMaxHealth;
    }

    public void setDefaultMaxHealth(double maxHealth) {
        this.defaultMaxHealth = maxHealth;
    }

    public double getMaxHealth(Player player) {
        return this.maxHealthMap.getOrDefault(player, this.defaultMaxHealth);
    }

    public void setMaxHealth(Player player, double maxHealth) {
        this.maxHealthMap.put(player, maxHealth);
    }

    public void addHealth(Player player, Double value) {
        if (!this.alive) {
            return;
        }
        double newHealth = BigDecimal.valueOf(this.getHealth(player)).add(BigDecimal.valueOf(value))
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
        this.setHealth(player, newHealth);
    }

    public void reduceHealth(Player player, double value) {
        if (!this.alive) {
            return;
        }
        double newHealth = BigDecimal.valueOf(getHealth(player)).subtract(BigDecimal.valueOf(value))
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
        this.setHealth(player, newHealth);
    }

    public void setHealth(Player player, double value) {
        if (!this.alive) {
            return;
        }
        double max = this.getMaxHealth(player);
        if (value < 0d) {
            value = 0d;
            this.room.setDeath(player);
        } else if (value > max) {
            value = max;
        }
        double newHealth = BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
        this.healthMap.put(player, newHealth);
        this.resetHealthBar(player, newHealth);
    }

    public void resetHealth(Player player) {
        double max = this.getMaxHealth(player);
        this.healthMap.put(player, max);
        this.resetHealthBar(player, max);
    }

    public double getHealth(Player player) {
        return this.healthMap.getOrDefault(player, this.getMaxHealth(player));
    }

    public void removePlayer(Player player) {
        this.healthMap.remove(player);
    }

    public void clearAll() {
        this.healthMap = new LinkedHashMap<>();
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    protected void resetHealthBar(Player player, double health) {
        double max = this.getMaxHealth(player);
        if (health >= max) {
            player.setHealth(20);
            return;
        }
        BigDecimal decimal = new BigDecimal(health);
        decimal = decimal.divide(BigDecimal.valueOf(max), 1, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(player.getMaxHealth()));
        int finalHealthDisplay = decimal.intValue();
        if (finalHealthDisplay < 1) {
            finalHealthDisplay = 1;
        }
        player.setHealth(finalHealthDisplay);
    }
}
