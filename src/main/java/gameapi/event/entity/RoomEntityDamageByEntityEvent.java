package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomEntityDamageByEntityEvent extends RoomEntityEvent implements Cancellable {

    private final float finalDamage;
    protected float damage;
    protected int attackCoolDown;

    protected float knockBack;

    protected Entity damager;

    protected EntityDamageEvent.DamageCause cause;

    public RoomEntityDamageByEntityEvent(Room room, Entity entity, Entity damager, float damage, float finalDamage, int attackCoolDown, float knockBack, EntityDamageEvent.DamageCause cause) {
        super(room, entity);
        this.damage = damage;
        this.finalDamage = finalDamage;
        this.attackCoolDown = attackCoolDown;
        this.damager = damager;
        this.knockBack = knockBack;
        this.cause = cause;
    }

    public Entity getDamager() {
        return damager;
    }

    public int getAttackCoolDown() {
        return this.attackCoolDown;
    }

    public void setAttackCoolDown(int attackCoolDown) {
        this.attackCoolDown = attackCoolDown;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getFinalDamage() {
        return finalDamage;
    }

    public float getKnockBack() {
        return knockBack;
    }

    public void setKnockBack(float knockBack) {
        this.knockBack = knockBack;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
}
