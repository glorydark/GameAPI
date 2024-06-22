package gameapi.tools;

import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;

import java.util.Random;

/**
 * Adapted from: PetteriM's FireworkShow
 * Glorydark added some changes to make it more convenient to spawn a firework
 */
public class FireworkTools {

    public static void spawnRandomFirework(Location location) {
        FireworkTools.spawnFirework(location, DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL, true);
    }

    public static void spawnRandomFirework(Location location, boolean isImmediateBomb) {
        FireworkTools.spawnFirework(location, DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL, isImmediateBomb);
    }

    public static void spawnFirework(Location location, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type) {
        spawnFirework(location, color, type, false);
    }

    public static void spawnFirework(Location location, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type, boolean isImmediateBomb) {
        if (location.getChunk() == null || location.getChunk().getProvider() == null) {
            return;
        }
        Level level = location.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByteArray("FireworkFade", new byte[0]);
        compoundTag.putBoolean("FireworkFlicker", random.nextBoolean());
        compoundTag.putBoolean("FireworkTrail", random.nextBoolean());
        tag.putCompound("Fireworks", (new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(compoundTag)).putByte("Flight", 1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", location.x + 1.0D))
                .add(new DoubleTag("", location.y + 1.0D))
                .add(new DoubleTag("", location.z + 1.0D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", 0.0F))
                .add(new FloatTag("", 0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        compoundTag.putByteArray("FireworkColor", new byte[]{
                (byte) color.getDyeData()
        });
        compoundTag.putByte("FireworkType", type.ordinal());
        EntityFirework entity = new EntityFirework(level.getChunk((int) location.x >> 4, (int) location.z >> 4), nbt);
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
        switch (enumNumber) {
            case 2:
                return ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL;
            case 3:
                return ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL;
            case 4:
                return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
            case 5:
                return ItemFirework.FireworkExplosion.ExplosionType.CREEPER_SHAPED;
            default:
                return ItemFirework.FireworkExplosion.ExplosionType.BURST;
        }
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
        switch (integer) {
            case 1:
                return DyeColor.RED;
            case 2:
                return DyeColor.BLACK;
            case 3:
                return DyeColor.BLUE;
            case 4:
                return DyeColor.BROWN;
            case 5:
                return DyeColor.CYAN;
            case 6:
                return DyeColor.GRAY;
            case 7:
                return DyeColor.GREEN;
            case 8:
                return DyeColor.LIGHT_BLUE;
            case 9:
                return DyeColor.LIGHT_GRAY;
            case 10:
                return DyeColor.LIME;
            case 11:
                return DyeColor.MAGENTA;
            case 12:
                return DyeColor.ORANGE;
            case 13:
                return DyeColor.PINK;
            case 14:
                return DyeColor.PURPLE;
            case 16:
                return DyeColor.YELLOW;
            default:
                return DyeColor.WHITE;
        }
    }
}
