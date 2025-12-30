package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Adapted from: PetteriM's FireworkShow
 * Glorydark added some changes to make it more convenient to spawn a firework
 */
public class FireworkTools {

    private static final int BIT_MASK = 0xff;

    public static void spawnRandomFirework(Location location) {
        spawnRandomFirework(location, null);
    }

    public static void spawnRandomFirework(Location location, Player[] players) {
        spawnRandomFirework(location, false, players);
    }

    public static void spawnRandomFirework(Location location, boolean isImmediateBomb) {
        spawnRandomFirework(location, isImmediateBomb, null);
    }

    public static void spawnRandomFirework(Location location, boolean isImmediateBomb, Player[] players) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int i1 = random.nextInt(14);
        int i2 = random.nextInt(4);
        FireworkTools.spawnFirework(location, FireworkTools.getColorByInt(i1), FireworkTools.getExplosionTypeByInt(i2), isImmediateBomb, players);
    }

    public static void spawnFirework(Location location, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type) {
        spawnFirework(location, color, type, null);
    }

    public static void spawnFirework(Location location, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type, Player[] players) {
        spawnFirework(location, color, type, false, players);
    }

    public static void spawnFirework(Location location, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type, boolean isImmediateBomb) {
        spawnFirework(location, color, type, isImmediateBomb, null);
    }

    public static void spawnFirework(Location location, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type, boolean isImmediateBomb, Player[] players) {
        if (location.getChunk() == null || location.getChunk().getProvider() == null) {
            return;
        }
        Level level = location.getLevel();
        EntityFirework entity = new EntityFirework(level.getChunk((int) location.x >> 4, (int) location.z >> 4), EntityFirework.getDefaultNBT(location));
        entity.setDataProperty(new LongEntityData(Entity.DATA_HAS_DISPLAY, -1L));
        ItemFirework item = ((ItemFirework) Item.get(ItemFirework.FIREWORKS));
        item.clearExplosions();
        item.setFlight(1);
        item.addExplosion(
                new ItemFirework.FireworkExplosion()
                        .addColor(color)
                        .type(type)
        );
        entity.setFirework(item);
        entity.setMotion(new Vector3(0, 0.3, 0));
        if (players == null) {
            entity.spawnToAll();
            if (isImmediateBomb) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.data = 0;
                pk.event = 25;
                pk.eid = entity.getId();
                entity.level.addLevelSoundEvent(location, 58, -1, 72);
                Server.broadcastPacket(entity.getViewers().values(), pk);
                entity.kill();
            }
        } else {
            for (Player player : players) {
                if (player == null) {
                    continue;
                }
                entity.spawnTo(player);
                if (isImmediateBomb) {
                    EntityEventPacket pk = new EntityEventPacket();
                    pk.data = 0;
                    pk.event = 25;
                    pk.eid = entity.getId();
                    entity.level.addLevelSoundEvent(location, 58, -1, 72);
                    player.dataPacket(pk);
                    entity.kill();
                }
            }
        }
    }

    public static ItemFirework.FireworkExplosion.ExplosionType getExplosionTypeByString(String s) {
        switch (s) {
            case "LARGE_BALL":
                return ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL;
            case "SMALL_BALL":
                return ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL;
            case "STAR_SHAPED":
                return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
            case "CREEPER_SHAPED":
                return ItemFirework.FireworkExplosion.ExplosionType.CREEPER_SHAPED;
            default:
                return ItemFirework.FireworkExplosion.ExplosionType.BURST;
        }
    }

    public static ItemFirework.FireworkExplosion.ExplosionType getExplosionTypeByInt(int enumNumber) {
        ItemFirework.FireworkExplosion.ExplosionType type = ItemFirework.FireworkExplosion.ExplosionType.values()[enumNumber];
        if (type == null) {
            return ItemFirework.FireworkExplosion.ExplosionType.BURST;
        }
        return type;
    }

    public static DyeColor getColorByString(String s) {
        switch (s) {
            case "RED":
                return DyeColor.RED;
            case "BLACK":
                return DyeColor.BLACK;
            case "BLUE":
                return DyeColor.BLUE;
            case "BROWN":
                return DyeColor.BROWN;
            case "CYAN":
                return DyeColor.CYAN;
            case "GRAY":
                return DyeColor.GRAY;
            case "GREEN":
                return DyeColor.GREEN;
            case "LIGHT_BLUE":
                return DyeColor.LIGHT_BLUE;
            case "LIGHT_GRAY":
                return DyeColor.LIGHT_GRAY;
            case "LIME":
                return DyeColor.LIME;
            case "MAGENTA":
                return DyeColor.MAGENTA;
            case "ORANGE":
                return DyeColor.ORANGE;
            case "PINK":
                return DyeColor.PINK;
            case "PURPLE":
                return DyeColor.PURPLE;
            case "YELLOW":
                return DyeColor.YELLOW;
            default:
                return DyeColor.WHITE;
        }
    }

    public static DyeColor getColorByInt(Integer integer) {
        DyeColor result = DyeColor.getByDyeData(integer);
        if (result == null) {
            return DyeColor.WHITE;
        }
        return result;
    }

    public static Item getFireworkStarItem(DyeColor dyeColor, ItemFirework.FireworkExplosion.ExplosionType explosionType, boolean flicker, boolean trail, DyeColor... fade) {
        Item item = Item.get(ItemID.FIREWORKSCHARGE);
        CompoundTag tag = new CompoundTag();
        tag.putInt("customColor", getColoredFireworkStarCustomColor(dyeColor));
        CompoundTag itemTag = new CompoundTag();
        byte[] fds = new byte[fade.length];
        for(int i = 0; i < fds.length; ++i) {
            fds[i] = (byte) fade[i].getDyeData();
        }
        itemTag.putByteArray("FireworkFade", fds);
        itemTag.putByte("FireworkColor", dyeColor.getDyeData());
        itemTag.putByte("FireworkType", explosionType.ordinal());
        itemTag.putByte("FireworkTrail", trail? 1: 0);
        itemTag.putByte("FireworkFlicker", flicker? 1: 0);
        return item;
    }

    public static int getColoredFireworkStarCustomColor(DyeColor dyeColor) {
        return switch (dyeColor) {
            // its sequence stands equal to dyeColor sequence.
            case BLACK -> -14869215; // 0b
            case RED -> -5231066; // 1b
            case GREEN -> -10585066; // 2b
            case BROWN -> -8170446; // 3b
            case BLUE -> -12827478; // 4b
            case PURPLE -> -7785800; // 5b
            case CYAN -> -15295332; // 6b
            case LIGHT_GRAY -> -6447721; // 7b
            case GRAY -> -12103854; // 8b
            case PINK -> -816214; // 9b
            case LIME -> -8337633; // 10b
            case YELLOW -> -75715; // 11b
            case LIGHT_BLUE -> -12930086; // 12b
            case MAGENTA -> -3715395; // 13b
            case ORANGE -> -425955; // 14b
            case WHITE -> -986896; // 15b
        };
    }
}
