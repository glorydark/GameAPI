package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoomEntityDamageByEntityEvent extends RoomEntityEvent implements Cancellable {

    private float finalDamage; // only used in query instead of modifying damage
    protected float damage;
    protected int attackCoolDown;

    protected float knockBack;

    protected Entity damager;

    protected EntityDamageEvent.DamageCause cause;

    protected Map<EntityDamageByEntityEvent.DamageModifier, Float> damageModifierFloatMap = new LinkedHashMap<>();

    public RoomEntityDamageByEntityEvent(Room room, Entity entity, Entity damager, float damage, float finalDamage, int attackCoolDown, float knockBack, EntityDamageEvent.DamageCause cause) {
        super(room, entity);
        this.damage = damage;
        this.finalDamage = finalDamage;
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
        return damage;
    }

    public float getDamage(EntityDamageEvent.DamageModifier damageModifier) {
        return this.damageModifierFloatMap.getOrDefault(damageModifier, 0f);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setDamage(EntityDamageEvent.DamageModifier damageModifier, float value) {
        this.damageModifierFloatMap.put(damageModifier, value);
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

    public void setFinalDamage(float finalDamage) {
        this.finalDamage = finalDamage;
    }
}
