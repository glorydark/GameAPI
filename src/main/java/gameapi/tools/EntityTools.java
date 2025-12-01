package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.utils.BlockIterator;
import gameapi.extensions.particleGun.entity.ParticleGunFakeBullet;
import gameapi.utils.Animation;
import gameapi.utils.protocol.AnimateEntityPacketV2;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 * @date {2023/12/29} {21:18}
 */
public class EntityTools {

    public static void knockBack(Entity attacker, Entity victim) {
        knockBack(attacker, victim, 0.4, false);
    }

    public static void knockBack(Entity attacker, Entity victim, double base) {
        knockBack(attacker, victim, base, false);
    }

    public static void knockBack(Entity attacker, Entity victim, boolean reverse) {
        knockBack(attacker, victim, 0.4, reverse);
    }

    public static void knockBack(Entity attacker, Entity victim, double base, boolean directionReverse) {
        double x = victim.getX() - attacker.getX();
        double z = victim.getZ() - attacker.getZ();
        knockBackV2(victim, x, z, base, 1.0, 1.0, directionReverse);
    }

    public static void knockBack(Entity entity, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (!(f <= 0.0)) {
            f = 1.0 / f;
            Vector3 motion = new Vector3();
            motion.x /= 2.0;
            motion.y /= 2.0;
            motion.z /= 2.0;
            motion.x += x * f * base;
            motion.y += base;
            motion.z += z * f * base;
            if (motion.y > base) {
                motion.y = base;
            }
            entity.setMotion(motion);
        }
    }

    public static void knockBackV2(Entity victim, double xDiff, double zDiff, double base, double XzKB, double yKB, boolean directionReverse) {

        if (directionReverse) {
            xDiff = -xDiff;
            zDiff = -zDiff;
        }

        Vector3 motion = new Vector3(victim.motionX, victim.motionY, victim.motionZ);

        motion.x /= 2.0d;
        motion.y /= 2.0d;
        motion.z /= 2.0d;
        motion.x += (xDiff < 0 ? -1 : 1) * base * XzKB;
        motion.y += base * yKB;
        motion.z += (zDiff < 0 ? -1 : 1) * base * XzKB;

        victim.motionX = motion.x;
        victim.motionY = motion.y;
        victim.motionZ = motion.z;
        if (!victim.justCreated) {
            victim.updateMovement();
        }

        if (victim.isPlayer) {
            Player victimPlayer = (Player) victim;
            if (victim.chunk != null && victimPlayer.spawned) {
                victim.addMotion(victimPlayer.motionX, victimPlayer.motionY, victimPlayer.motionZ);
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = victimPlayer.getId();
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                victimPlayer.dataPacket(pk);
            }

            if (victimPlayer.motionY > 0.0) {
                victimPlayer.resetInAirTicks();
            }
        }
    }

    public static void bigJump(Entity entity, double XzKB, double yKB, boolean directionReverse) {
        // Vector3 motion = entity.getMotion();

        // 获取玩家的 yaw（角度制）
        double yaw = entity.getYaw();
        // 转换为弧度制（Math.sin/cos 需要弧度）
        double radians = Math.toRadians(yaw);
        // 计算 x 和 z 单位向量
        double x = -Math.sin(radians); // 注意负号
        double z = Math.cos(radians);

        Vector3 motion = new Vector3(x, yKB, z);
        motion.x *= XzKB;
        motion.z *= XzKB;
        if (directionReverse) {
            motion.x *= -1;
            motion.z *= -1;
        }

        // GameDebugManager.info("mot: " + motion + ", directVec: " + entity.getDirectionVector());

        if (!entity.justCreated) {
            entity.updateMovement();
        }

        if (entity.isPlayer) {
            Player victimPlayer = (Player) entity;
            if (entity.chunk != null && victimPlayer.spawned) {
                entity.addMotion(victimPlayer.motionX, victimPlayer.motionY, victimPlayer.motionZ);
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = victimPlayer.getId();
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                victimPlayer.dataPacket(pk);
            }

            if (victimPlayer.motionY > 0.0) {
                victimPlayer.resetInAirTicks();
            }
        }
    }

    public static void knockBackV2(Entity attacker, Entity victim, double base, double XzKB, double yKB) {
        knockBackV2(attacker, victim, base, XzKB, yKB, false);
    }

    public static void knockBackV2(Entity attacker, Entity victim, double base, double XzKB, double yKB, boolean directionReverse) {
        double x = victim.getX() - attacker.getX();
        double z = victim.getZ() - attacker.getZ();
        knockBackV2(victim, x, z, base, XzKB, yKB, directionReverse);
    }

    public static void dropExpOrb(Location source, int exp) {
        if (source != null && source.getChunk() != null) {
            Random rand = ThreadLocalRandom.current();
            for (int split : EntityXPOrb.splitIntoOrbSizes(exp)) {
                CompoundTag nbt = Entity.getDefaultNBT(source, new Vector3((rand.nextDouble() * 0.2 - 0.1) * 2.0, rand.nextDouble() * 0.4, (rand.nextDouble() * 0.2 - 0.1) * 2.0), rand.nextFloat() * 360.0F, 0.0F);
                nbt.putShort("Value", split);
                nbt.putShort("PickupDelay", 10);
                nbt.putBoolean("AntiClean", true);
                Entity entity = Entity.createEntity("XpOrb", source.getChunk(), nbt);
                if (entity != null) {
                    entity.spawnToAll();
                }
            }
        }
    }

    public static void playAnimation(Entity entity, Animation animation) {
        playAnimation(entity, animation, entity.getViewers().values().toArray(new Player[0]));
    }

    public static void playAnimation(Entity entity, Animation animation, Player viewer) {
        playAnimation(entity, animation, new Player[]{viewer});
    }

    public static void playAnimation(Entity entity, Animation animation, Player[] viewers) {
        AnimateEntityPacketV2 pk = AnimateEntityPacketV2.fromAnimation(animation);
        pk.addAnimatedEntityRuntimeIds(entity.getId());
        Server.broadcastPacket(viewers, pk);
    }

    public static void setEntityHeldItem(Entity entity, Item item) {
        MobEquipmentPacket pk = getHeldItemPacket(entity, 0, item, true);
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public static void setEntityArmorContents(Entity entity, Item helmet, Item chestplate, Item leggings, Item boots) {
        MobArmorEquipmentPacket pk = getArmorItemPacket(entity, helmet, chestplate, leggings, boots);
        Server.broadcastPacket(entity.getViewers().values(), pk);

    }

    public static MobEquipmentPacket getHeldItemPacket(Entity entity, int slot, Item item, boolean isHeld) {
        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = entity.getId();
        pk.item = item;
        pk.inventorySlot = slot;
        if (isHeld) {
            pk.hotbarSlot = slot;
        }
        return pk;
    }

    public static MobArmorEquipmentPacket getArmorItemPacket(Entity entity, Item helmet, Item chestplate, Item leggings, Item boots) {
        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = entity.getId();
        pk.slots[0] = helmet;
        pk.slots[1] = chestplate;
        pk.slots[2] = leggings;
        pk.slots[3] = boots;
        return pk;
    }

    public static Map<Integer, Item> getEntityArmorInventory(Entity entity) {
        Map<Integer, Item> inventory = new Int2ObjectOpenHashMap<>();
        if (entity.namedTag.contains("Armor")) {
            ListTag<CompoundTag> listTag = entity.namedTag.getList("Armor", CompoundTag.class);
            int count = 0;
            for (CompoundTag item : listTag.getAll()) {
                int slot = item.getByte("Slot");
                if (slot < 0 || slot > 3) {
                    continue;
                }
                if (slot < count) {
                    continue;
                }
                inventory.put(slot, NBTIO.getItemHelper(item));
                count++;
            }
        }
        return inventory;
    }

    public static EntityItem getEntityItem(Item item, Position position, boolean display) {
        try {
            CompoundTag itemTag = NBTIO.putItemHelper(item);
            itemTag.setName("Item");
            EntityItem entityItem = new EntityItem(position.getLevel().getChunk((int) position.getX() >> 4, (int) position.getZ() >> 4, true),
                    new CompoundTag().putList(new ListTag<>("Pos")
                                    .add(new DoubleTag("", position.getX()))
                                    .add(new DoubleTag("", position.getY()))
                                    .add(new DoubleTag("", position.getZ())))
                            .putShort("Health", 5)
                            .putCompound("Item", itemTag)
                            .putList((new ListTag<>("Motion"))
                                    .add(new DoubleTag("", 0))
                                    .add(new DoubleTag("", 0))
                                    .add(new DoubleTag("", 0)))
                            .putList((new ListTag<>("Rotation"))
                                    .add(new FloatTag("", ThreadLocalRandom.current().nextFloat() * 360.0F))
                                    .add(new FloatTag("", 0.0F)))
                            .putShort("PickupDelay", 10));

            if (display) {
                entityItem.setNameTagAlwaysVisible(true);
                entityItem.setNameTagVisible(true);
                entityItem.setNameTag(item.getCustomName());
            }
            return entityItem;
        } catch (Exception e) {
            return null;
        }
    }

    public static Item getEntityItemInHand(Entity entity) {
        if (entity instanceof Player player) {
            return player.getInventory().getItemInHand();
        } else {
            if (entity.namedTag != null && entity.namedTag.contains("Item")) {
                return NBTIO.getItemHelper(entity.namedTag.getCompound("Item"));
            }
        }
        return Item.AIR_ITEM;
    }

    public static Item getEntityProjectileItem(Entity entity) {
        return switch (entity.getNetworkId()) {
            case EntitySnowball.NETWORK_ID -> Item.get(ItemID.SNOWBALL);
            case EntityGhastFireBall.NETWORK_ID -> Item.get(ItemID.FIRE_CHARGE);
            case EntityArrow.NETWORK_ID, EntitySmallFireBall.NETWORK_ID -> Item.get(ItemID.ARROW);
            case EntityWindCharge.NETWORK_ID -> Item.fromString("minecraft:wind_charge");
            case EntityEnderPearl.NETWORK_ID -> Item.get(ItemID.ENDER_PEARL);
            case EntityEnderCharge.NETWORK_ID -> Item.get(ItemID.DRAGON_BREATH);
            case EntityEgg.NETWORK_ID -> Item.get(ItemID.EGG);
            case EntityThrownTrident.NETWORK_ID -> Item.get(ItemID.TRIDENT);
            case EntityArmorStand.NETWORK_ID -> {
                if (entity instanceof ParticleGunFakeBullet bullet) {
                    yield bullet.getItem();
                }
                yield Item.AIR_ITEM;
            }
            default -> Item.AIR_ITEM;
        };
    }

    public static boolean isBlockBetween(Entity damager, Entity victim) {
        Location loc1 = damager.getLocation().add(0, damager.getEyeHeight(), 0); // e1的眼睛位置
        Location loc2 = victim.getLocation().add(0, victim.getEyeHeight(), 0); // e2的眼睛位置

        // 计算两点之间的向量
        double dx = loc2.getX() - loc1.getX();
        double dy = loc2.getY() - loc1.getY();
        double dz = loc2.getZ() - loc1.getZ();

        double distance = loc1.distance(loc2);
        double step = 0.3; // 采样间隔，越小越精确但性能开销越大

        Block block = getTargetBlock(damager, 5);

        // 从e1到e2直线采样
        for (double d = step; d < distance; d += step) {
            double progress = d / distance; // 进度比例 0~1

            double x = loc1.getX() + dx * progress;
            double y = loc1.getY() + dy * progress;
            double z = loc1.getZ() + dz * progress;

            if (block.isSolid() && block.getBoundingBox().isVectorInside(new Vector3(x, y, z))) {
                return true;
            }
        }

        return false;
    }

    public static Block getTargetBlock(Entity entity, int maxDistance) {
        return getTargetBlock(entity, maxDistance, new Integer[0]);
    }

    public static Block getTargetBlock(Entity entity, int maxDistance, Map<Integer, Object> transparent) {
        return getTargetBlock(entity, maxDistance, (Integer[])transparent.keySet().toArray(new Integer[0]));
    }

    public static Block getTargetBlock(Entity entity, int maxDistance, Integer[] transparent) {
        return getTargetBlock(entity, maxDistance, (Set)(new HashSet(Arrays.asList(transparent))));
    }

    public static Block getTargetBlock(Entity entity, int maxDistance, Set<Integer> transparent) {
        try {
            Block[] blocks = getLineOfSight(entity, maxDistance, 1, transparent);
            Block block = blocks[0];
            if (block != null) {
                if (transparent == null || transparent.isEmpty()) {
                    return block;
                }

                if (transparent.contains(block.getId())) {
                    return block;
                }
            }
        } catch (Exception var5) {
        }

        return null;
    }

    public static Block[] getLineOfSight(Entity entity, int maxDistance, int maxLength, Set<Integer> transparent) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        boolean useTransparent = transparent != null && !transparent.isEmpty();
        LinkedList<Block> blocks = new LinkedList();
        BlockIterator itr = new BlockIterator(entity.level, entity.getPosition(), entity.getDirectionVector(), entity.getEyeHeight(), maxDistance);

        while(itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.pollFirst();
            }

            int id = block.getId();
            if (useTransparent) {
                if (!transparent.contains(id)) {
                    break;
                }
            } else if (id != 0) {
                break;
            }
        }

        return blocks.toArray(Block.EMPTY_ARRAY);
    }
}
