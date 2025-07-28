package gameapi.extensions.particleGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class Bullet {

    private final Player owner;

    private final Weapon weapon;

    private final Location startPos;
    private final Vector3 movingMotion;
    protected boolean alive = true;
    private Vector3 currentPos;

    public Bullet(Player owner, Weapon weapon) {
        this.startPos = owner.add(0, 1.25, 0);
        this.currentPos = this.startPos.clone();
        this.owner = owner;
        this.weapon = weapon;
        double yawRad = Math.toRadians(owner.getYaw());
        double pitchRad = Math.toRadians(owner.getPitch());

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        this.movingMotion = owner.getDirectionVector().add(x, y, z);
    }

    public void onBulletMove() {
        this.currentPos = this.currentPos.add(this.movingMotion.getX(), this.movingMotion.getY(), this.movingMotion.getZ());
        if (this.currentPos.distance(this.startPos) > this.weapon.getBulletMoveDist()) {
            this.alive = false;
        } else {
            Particle particle = this.weapon.getShootParticle();
            particle.setComponents(Location.fromObject(this.currentPos, owner.getLevel()));
            this.owner.getLevel().addParticle(particle);

            if (this.weapon.isDamageEntity()) {
                for (Entity entity : this.startPos.getLevel().getEntities()) {
                    if (entity.distance(this.currentPos) < 0.25) {
                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(owner, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, weapon.getBulletDamage());
                        Server.getInstance().getPluginManager().callEvent(event);
                    }
                }
            }

            if (this.weapon.isDamagePlayer()) {
                for (Player player : this.startPos.getLevel().getPlayers().values()) {
                    if (player.distance(this.currentPos) < 0.25) {
                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(owner, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, weapon.getBulletDamage());
                        Server.getInstance().getPluginManager().callEvent(event);
                    }
                }
            }
        }
    }
}
