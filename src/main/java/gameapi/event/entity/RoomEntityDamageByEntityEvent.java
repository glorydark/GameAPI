package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoomEntityDamageByEntityEvent extends RoomEntityEvent implements Cancellable {

    // only used in query instead of modifying damage
    protected int attackCoolDown;

    protected float knockBack;

    protected Entity damager;

    protected EntityDamageEvent.DamageCause cause;

    protected Map<EntityDamageByEntityEvent.DamageModifier, Float> damageModifierFloatMap = new LinkedHashMap<>();

    public RoomEntityDamageByEntityEvent(Room room, Entity entity, Entity damager, int attackCoolDown, float knockBack, EntityDamageEvent.DamageCause cause) {
        super(room, entity);
        this.attackCoolDown = attackCoolDown;
        this.damager = damager;
        this.knockBack = knockBack;
        this.cause = cause;
    }

    public void parseDamageModifierFloatMap(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        this.damageModifierFloatMap.clear();
        for (EntityDamageByEntityEvent.DamageModifier value : EntityDamageEvent.DamageModifier.values()) {
            this.damageModifierFloatMap.put(value, entityDamageByEntityEvent.getDamage(value));
        }
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
        return this.getDamage(EntityDamageEvent.DamageModifier.BASE);
    }

    public void setDamage(float damage) {
        this.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);
    }

    public float getDamage(EntityDamageEvent.DamageModifier damageModifier) {
        return this.damageModifierFloatMap.getOrDefault(damageModifier, 0f);
    }

    public void setDamage(EntityDamageEvent.DamageModifier damageModifier, float value) {
        this.damageModifierFloatMap.put(damageModifier, value);
    }

    public float getFinalDamage() {
        float damage = 0.0F;
        for (Float d : this.damageModifierFloatMap.values()) {
            if (d != null) {
                damage += d;
            }
        }
        return damage;
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

    public Map<EntityDamageByEntityEvent.DamageModifier, Float> getDamageModifierFloatMap() {
        return damageModifierFloatMap;
    }
}
