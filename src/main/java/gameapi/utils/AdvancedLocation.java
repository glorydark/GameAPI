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
    private Location location = null;
    private double yaw;
    private double pitch;
    private double headYaw;
    private LocationType version;

    private String inputString = "";

    public AdvancedLocation() {
    }

    public AdvancedLocation(Vector3 pos, Level level) {
        this(Location.fromObject(pos, level));
    }

    public AdvancedLocation(Vector3 pos, Vector3 rot, Level level) {
        this(Location.fromObject(pos, level, rot.x, rot.y, rot.z));
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
                case POS_AND_ROT:
                    this.headYaw = loc.getHeadYaw();
                case POS_AND_ROT_EXCEPT_HEADYAW:
                    this.yaw = loc.getYaw();
                    this.pitch = loc.getPitch();
                case POS:
                    this.location = loc.getLocation();
                    break;
            }
        }
    }

    public Level getLevel() {
        return this.location.getLevel();
    }

    public void setLevel(Level level) {
        this.location.setLevel(level);
    }

    public void teleport(Player player) {
        teleport(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void teleport(Player player, PlayerTeleportEvent.TeleportCause cause) {
        if (this.location == null || !this.location.isValid()) {
            return;
        }
        Location out;
        switch (this.version) {
            case POS_AND_ROT_EXCEPT_HEADYAW:
                out = new Location(this.location.getX(), this.location.getY(), this.location.getZ(), this.yaw, this.pitch);
                out.setLevel(this.location.getLevel());
                break;
            case POS_AND_ROT:
                out = new Location(this.location.getX(), this.location.getY(), this.location.getZ(), this.yaw, this.pitch, this.headYaw);
                out.setLevel(this.location.getLevel());
                break;
            default:
            case POS:
                out = new Location(this.location.getX(), this.location.getY(), this.location.getZ(), player.getYaw(), player.getPitch(), player.getHeadYaw());
                out.setLevel(this.location.getLevel());
                break;
        }
        player.teleport(out, cause);
    }

    public boolean isValid() {
        return location != null && location.isValid();
    }

    public enum LocationType {
        POS,
        POS_AND_ROT_EXCEPT_HEADYAW,
        POS_AND_ROT
    }
}