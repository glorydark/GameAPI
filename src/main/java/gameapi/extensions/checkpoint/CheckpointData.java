package gameapi.extensions.checkpoint;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import gameapi.tools.ParticleTools;
import gameapi.utils.Rotation;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckpointData {

    private Vector3 vector3;

    private Rotation rotation = null;

    private double horizontalRadius;

    private double verticalRadius;

    private Particle particle = new DustParticle(new Vector3(0, 0, 0), BlockColor.YELLOW_BLOCK_COLOR);

    private String particleEffectId = "";

    private String name;

    private boolean particleMarked;

    private double globalMultiplier = 1.0f;

    private double score = 0;

    public CheckpointData(String name, Vector3 vector3, double horizontalRadius, double verticalRadius) {
        this(name, vector3, horizontalRadius, verticalRadius, false);
    }

    public CheckpointData(String name, Vector3 vector3, double horizontalRadius, double verticalRadius, boolean particleMarked) {
        this.name = name;
        this.vector3 = vector3;
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
        this.particleMarked = particleMarked;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    /**
     * This method is to check whether player is in the checking range or not, which depends on location and radius.
     * If the level of player is in different from that of this checking point, this method is bound to return false.
     *
     * @param player the player you want to check
     * @return bool: is that player in range
     */
    public boolean isInRange(Player player) {
        SimpleAxisAlignedBB bb = (SimpleAxisAlignedBB) new SimpleAxisAlignedBB(this.vector3, this.vector3).grow(this.horizontalRadius, this.verticalRadius, this.horizontalRadius);
        return bb.isVectorInside(player.getPosition());
    }

    /**
     * This method is to summon a particle mark.
     */
    public void showParticleMarks(Level level) {
        if (this.particleMarked) {
            if (this.particleEffectId.isEmpty()) {
                ParticleTools.drawCircle(this.particle, this.getLocation(level).add(0, 1, 0), this.horizontalRadius);
            } else {
                SimpleAxisAlignedBB bb = (SimpleAxisAlignedBB) new SimpleAxisAlignedBB(this.vector3, this.vector3).grow(this.horizontalRadius, this.verticalRadius, this.horizontalRadius);
                ParticleTools.lineSpace(this.particleEffectId, level, bb, level.getPlayers().values().toArray(new Player[0]));
            }
        }
    }

    public Location getLocation(Level level) {
        if (this.rotation == null) {
            return Location.fromObject(vector3, level);
        } else {
            return Location.fromObject(vector3, level, rotation.getYaw(), rotation.getPitch(), rotation.getHeadYaw());
        }
    }
}
