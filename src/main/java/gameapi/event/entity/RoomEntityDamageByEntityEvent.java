package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomEntityDamageByEntityEvent extends RoomEntityEvent implements Cancellable {

    protected float damage;

    protected int attackCoolDown;

    protected float knockBack;

    protected Entity damager;

    protected EntityDamageEvent.DamageCause cause;

    public RoomEntityDamageByEntityEvent(Room room, Entity entity, Entity damager, float damage, int attackCoolDown, float knockBack, EntityDamageEvent.DamageCause cause) {
        this.room = room;
        this.damage = damage;
        this.attackCoolDown = attackCoolDown;
        if (!room.getRoomRule().isAllowAttackCoolDown()) {
            this.attackCoolDown = 0;
        }
        this.entity = entity;
        this.damager = damager;
        this.knockBack = knockBack;
        this.cause = cause;
    }

    public Entity getDamager() {
        return damager;
    }

    public int getAttackCoolDown() {
        return attackCoolDown;
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
