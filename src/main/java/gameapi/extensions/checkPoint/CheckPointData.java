package gameapi.extensions.checkPoint;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckPointData {

    private Vector3 vector3;

    private double horizontalRadius;
    
    private double verticalRadius;

    private Particle particle = new DustParticle(new Vector3(0, 0, 0), BlockColor.YELLOW_BLOCK_COLOR);

    private String name;

    public CheckPointData(String name, Vector3 vector3, double horizontalRadius, double verticalRadius) {
        this.name = name;
        this.vector3 = vector3;
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
    }

    /**
     * This method is to check whether player is in the checking range or not, which depends on location and radius.
     * If the level of player is in different from that of this checking point, this method is bound to return false.
     *
     * @param player the player you want to check
     * @return bool: is that player in range
     */
    public boolean isInRange(Player player) {
        SimpleAxisAlignedBB bb = (SimpleAxisAlignedBB) new SimpleAxisAlignedBB(vector3, vector3).grow(horizontalRadius, verticalRadius, horizontalRadius);
        return bb.isVectorInside(player.getPosition());
    }

    /**
     * This method is to summon a particle mark.
     */
    public void showParticleMarks(Level level) {
        for (int angle = 0; angle < 360; angle++) {
            double x1 = vector3.getX() + horizontalRadius * Math.cos(angle * 3.14 / 180);
            double z1 = vector3.getZ() + horizontalRadius * Math.sin(angle * 3.14 / 180);
            Particle particle_temp = (Particle) particle.clone();
            particle_temp.setX(x1);
            particle_temp.setY(vector3.getY());
            particle_temp.setZ(z1);
            if (angle % 30 == 0) {
                level.addParticle(particle_temp);
            }
        }
    }

    public Location getLocation(Level level) {
        return new Location(vector3.x, vector3.y, vector3.z, level);
    }
}
