package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import gameapi.tools.SpatialTools;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class AdvancedLocation {
    private Location location;
    private double yaw;
    private double pitch;
    private double headYaw;
    private int version;

    private String inputString;

    public AdvancedLocation() {
    }

    public AdvancedLocation(Vector3 vector3, Level level) {
        this(new Location(vector3.x, vector3.y, vector3.z, level));
    }

    public AdvancedLocation(Location location) {
        this.location = location;
        this.pitch = location.getPitch();
        this.headYaw = location.getHeadYaw();
        this.yaw = location.getYaw();
    }

    public AdvancedLocation(String string) {
        this.inputString = string;
        AdvancedLocation loc = SpatialTools.parseLocation(string);
        if (loc != null) {
            switch (loc.getVersion()) {
                case 2:
                    this.headYaw = loc.getHeadYaw();
                case 1:
                    this.yaw = loc.getYaw();
                    this.pitch = loc.getPitch();
                case 0:
                    this.location = loc.getLocation();
                    break;
            }
        }
    }

    public Level getLevel() {
        return location.getLevel();
    }

    public void setLevel(Level level) {
        this.location.setLevel(level);
    }

    public void teleport(Player player) {
        teleport(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void teleport(Player player, PlayerTeleportEvent.TeleportCause cause) {
        if (location == null || !location.isValid()) {
            return;
        }
        Location out;
        switch (version) {
            case 1:
                out = new Location(location.getX(), location.getY(), location.getZ(), yaw, pitch);
                out.setLevel(location.getLevel());
                break;
            case 2:
                out = new Location(location.getX(), location.getY(), location.getZ(), yaw, pitch, headYaw);
                out.setLevel(location.getLevel());
                break;
            default:
            case 0:
                out = new Location(location.getX(), location.getY(), location.getZ(), player.getYaw(), player.getPitch(), player.getHeadYaw());
                out.setLevel(location.getLevel());
                break;
        }
        player.teleport(out, cause);
    }

    public boolean isValid() {
        return location != null && location.isValid();
    }
}