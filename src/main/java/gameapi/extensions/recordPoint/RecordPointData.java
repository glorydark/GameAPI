package gameapi.extensions.recordPoint;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import lombok.Data;

@Data
public class RecordPointData {

    private Location location;

    private double radius;

    private Particle particle = new DustParticle(new Vector3(0, 0, 0), BlockColor.YELLOW_BLOCK_COLOR);

    private String name;

    public RecordPointData(String name, Location location, double radius) {
        this.name = name;
        this.location = location;
        this.radius = radius;
    }

    /**
     * This method is to check whether player is in the checking range or not, which depends on location and radius.
     * If the level of player is in different from that of this checking point, this method is bound to return false.
     *
     * @param player the player you want to check
     * @return bool: is that player in range
     */
    public boolean isInRange(Player player) {
        if (player.getLevel() != location.getLevel()) {
            return false;
        }
        return location.distance(player) <= radius;
    }

    /**
     * This method is to summon a particle mark.
     */
    public void showParticleMarks() {
        for (int angle = 0; angle < 720; angle++) {
            double x1 = location.getX() + radius * Math.cos(angle * 3.14 / 180);
            double z1 = location.getZ() + radius * Math.sin(angle * 3.14 / 180);
            Particle particle_temp = (Particle) particle.clone();
            particle_temp.setX(x1);
            particle_temp.setY(location.getY());
            particle_temp.setZ(z1);
            if (angle % 30 == 0) {
                location.getLevel().addParticle(particle_temp);
            }
        }
    }

}
