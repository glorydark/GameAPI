package gameapi.items;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomProjectile;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import cn.nukkit.utils.BlockColor;
import gameapi.entity.EntityBulletSnowball;
import gameapi.extensions.projectileGun.ProjectileGunReloadTask;
import gameapi.tools.ParticleTools;
import gameapi.utils.MolangVariableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
public class ProjectileGunInteractableItem extends ItemCustomProjectile implements ItemDurable {

    public static final List<String> reloadPlayers = new ArrayList<>();
    public static Map<String, Integer> lastShootTickMap = new LinkedHashMap<>();

    public ProjectileGunInteractableItem(@NotNull String id, @Nullable String name, @NotNull String textureName) {
        super(id, name, textureName);
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (ticksUsed == 1) {
            this.reload(player);
        }
        return super.onUse(player, ticksUsed);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (this.isProjectileGun()) {
            this.onGunItemTouch(player, this);
            return false;
        } else {
            return super.onClickAir(player, directionVector);
        }
    }

    @Override
    public int getMaxDurability() {
        return 1000;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public String getProjectileEntityType() {
        return "EntityBulletSnowball";
    }

    public boolean isProjectileGun() {
        return this.hasCompoundTag() && this.getNamedTag().contains("gameapi:projectile_gun_data");
    }

    public CompoundTag getProjectileGunData() {
        return this.hasCompoundTag()? (this.getNamedTag().contains("gameapi:projectile_gun_data")? this.getNamedTag().getCompound("gameapi:projectile_gun_data"): null): null;
    }

    @Override
    public float getThrowForce() {
        return 3F;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.customBuilder(this, CreativeItemCategory.EQUIPMENT)
                .handEquipped(true)
                .creativeGroup("itemGroup.name.sword")
                .canDestroyInCreative(true)
                .build();
    }

    public void shoot(Player player) {
        CompoundTag tag = getProjectileGunData();
        if (tag != null) {
            float throwForce = tag.getFloat("bulletSpeed");
            if (throwForce <= 0F) {
                throwForce = 3F;
            }
            String particleId;
            BlockColor color;
            if (tag.contains("particleColor")) {
                CompoundTag particleColor = tag.getCompound("particleColor");
                particleId = particleColor.getString("id");
                // 读取颜色数据
                if (particleColor.contains("variable.color")) {
                    CompoundTag colorTagSub = particleColor.getCompound("variable.color");
                    float r = colorTagSub.getFloat("r");
                    float g = colorTagSub.getFloat("g");
                    float b = colorTagSub.getFloat("b");

                    // 将浮点数转换回整数 (0-255)
                    int red = (int) (r * 255);
                    int green = (int) (g * 255);
                    int blue = (int) (b * 255);

                    color = new BlockColor(red, green, blue);
                } else {
                    color = null;
                }
            } else {
                color = null;
                particleId = "";
            }
            EntityBulletSnowball.launch(
                    player,
                    player.getDirectionVector(),
                    0.03f,
                    throwForce,
                    bulletSnowball -> {
                        if (particleId.isEmpty()) {
                            return;
                        }
                        ParticleTools.addParticleEffect("minecraft:falling_dust", bulletSnowball,
                                color == null?
                                        new MolangVariableMap() :
                                        new MolangVariableMap()
                                                .setColorRGB("variable.color",
                                                        color.getRed() / 255F,
                                                        color.getGreen() / 255F,
                                                        color.getBlue() / 255F)
                        );
                    },
                    compoundTag -> compoundTag.putFloat("gameapi:gun_bullet_damage", tag.getFloat("damage")));
        }
    }

    public void reload(Player player) {
        if (ProjectileGunInteractableItem.reloadPlayers.contains(player.getName())) {
            return;
        }
        Item item = player.getInventory().getItemInHand();
        String currentItemUuid = item.getNamedTag().getString("gameapi:gun_uuid");
        final String uuid = this.getNamedTag().getString("gameapi:gun_uuid");
        if (item.hasCompoundTag() && currentItemUuid.equals(uuid)) {
            CompoundTag tag = this.getProjectileGunData();
            if (tag == null) {
                return;
            }
            int ammo = tag.getInt("ammo");
            if (ammo <= 0) {
                ammo = 30;
            }
            if (item.getCount() == ammo) {
                return;
            }
            if (item.getCount() > 1) {
                item.setCount(1);
                item.setDamage(item.getMaxDurability() - 1);
                player.getInventory().setItemInHand(item);
            }
            player.sendMessage("8");
            ProjectileGunInteractableItem.reloadPlayers.add(player.getName());
            Server.getInstance().getScheduler().scheduleRepeatingTask(Server.getInstance().getPluginManager().getPlugin("GameAPI"), new ProjectileGunReloadTask(player, this), 1);
        }
    }

    public void onGunItemTouch(Player player, Item item) {
        if (ProjectileGunInteractableItem.reloadPlayers.contains(player.getName())) {
            return;
        }
        if (item.hasCompoundTag() && item.getNamedTag().contains("gameapi:projectile_gun")) {
            CompoundTag tag = getProjectileGunData();
            if (tag == null) {
                return;
            }
            if (item.getDamage() != 0) {
                this.reload(player);
                return;
            }
            int lastShootTick = ProjectileGunInteractableItem.lastShootTickMap.getOrDefault(player.getName(), 0);
            int currentTick = Server.getInstance().getTick();
            if (currentTick - lastShootTick >= tag.getInt("shootInterval")) {
                player.sendMessage("3");
                int count = item.getCount() - 1;
                if (count < 1) {
                    count = 1;
                }
                item.setCount(count);
                this.shoot(player);
                if (count == 1) {
                    item.setDamage(item.getMaxDurability() - 1);
                    player.getInventory().setItemInHand(item);
                    player.sendMessage("5");
                    this.reload(player);
                } else {
                    player.sendMessage("4");
                    player.getInventory().setItemInHand(item);
                    ProjectileGunInteractableItem.lastShootTickMap.put(player.getName(), currentTick);
                }
            }
        }
    }
}
