package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomEntityDamageByEntityEvent extends RoomEntityEvent implements Cancellable {

    protected float damage;

    protected int attackCoolDown;

    protected float knockBack;

    protected Entity damager;

    public RoomEntityDamageByEntityEvent(Room room, EntityDamageByEntityEvent event){
        this.room = room;
        this.damage = event.getDamage();
        this.attackCoolDown = event.getAttackCooldown();
        this.entity = event.getEntity();
        this.damager = event.getDamager();
        this.knockBack = event.getKnockBack();
    }

    public Entity getDamager() {
        return damager;
    }

    public int getAttackCoolDown() {
        return attackCoolDown;
    }

    public float getDamage() {
        return damage;
    }

    public float getKnockBack() {
        return knockBack;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setAttackCoolDown(int attackCoolDown) {
        this.attackCoolDown = attackCoolDown;
    }

    public void setKnockBack(float knockBack) {
        this.knockBack = knockBack;
    }
}
