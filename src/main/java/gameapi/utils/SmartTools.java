package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.OnScreenTextureAnimationPacket;
import cn.nukkit.network.protocol.SetTitlePacket;
import cn.nukkit.network.protocol.TextPacket;
import com.google.common.base.Strings;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SmartTools {
    //https://blog.csdn.net/weixin_39975055/article/details/115082818

    @Deprecated
    public static String dateToString(Date date) {
        return dateToString(date, "yyyyMMdd_hhmmss");
    }

    public static String dateToString(Date date, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    @Deprecated
    public static Date stringToDate(String string) {
        return stringToDate(string, "yyyy-MM-dd-hh-mm-ss");
    }

    public static Date stringToDate(String string, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        try {
            date = format.parse(string);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return date;
    }

    public static void sendActionbar(Collection<Player> players, String title){
        sendActionbar(players.toArray(new Player[0]), title, 1, 0, 1);
    }

    public static void sendActionbar(Player[] players, String title){
        sendActionbar(players, title, 1, 0, 1);
    }

    public static void sendActionbar(Collection<Player> players, String title, int fadein, int duration, int fadeout){
        sendActionbar(players.toArray(new Player[0]), title, fadein, duration, fadeout);
    }

    public static void sendActionbar(Player[] players, String title, int fadein, int duration, int fadeout){
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = 4;
        pk.text = title;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        Server.broadcastPacket(players, pk);
    }

    public static void sendPopup(Collection<Player> players, String message) {
        sendPopup(players.toArray(new Player[0]), message);
    }

    public static void sendPopup(Player[] players, String message) {
        TextPacket pk = new TextPacket();
        pk.type = 3;
        pk.message = message;
        Server.broadcastPacket(players, pk);
    }

    public static void sendTip(Collection<Player> players, String message) {
        sendTip(players.toArray(new Player[0]), message);
    }

    public static void sendTip(Player[] players, String message) {
        TextPacket pk = new TextPacket();
        pk.type = 5;
        pk.message = message;
        Server.broadcastPacket(players, pk);
    }

    public static void sendTitle(Collection<Player> players, String title) {
        sendTitle(players.toArray(new Player[0]), title, null, 20, 20, 5);
    }

    public static void sendTitle(Player[] players, String title) {
        sendTitle(players, title, null, 20, 20, 5);
    }

    public static void sendTitle(Collection<Player> players, String title, String subtitle) {
        sendTitle(players.toArray(new Player[0]), title, subtitle, 20, 20, 5);
    }

    public static void sendTitle(Player[] players, String title, String subtitle) {
        sendTitle(players, title, subtitle, 20, 20, 5);
    }

    public static void sendTitle(Collection<Player> players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(players.toArray(new Player[0]), title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        setTitleAnimationTimes(players, fadeIn, stay, fadeOut);
        if (!Strings.isNullOrEmpty(subtitle)) {
            setSubtitle(players, subtitle);
        }

        setTitle(players, Strings.isNullOrEmpty(title) ? " " : title);
    }

    public static void setTitleAnimationTimes(Player[] players, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = 5;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        Server.broadcastPacket(players, pk);
    }

    private static void setTitle(Player[] players, String text) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.text = text;
        packet.type = 2;
        Server.broadcastPacket(players, packet);
    }

    public static void setSubtitle(Player[] players, String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = 3;
        pk.text = subtitle;
        Server.broadcastPacket(players, pk);
    }

    public static void sendMessage(Collection<Player> players, String message) {
        sendMessage(players.toArray(new Player[0]), message);
    }

    public static void sendMessage(Player[] players, String message) {
        TextPacket pk = new TextPacket();
        pk.type = 0;
        pk.message = Server.getInstance().getLanguage().translateString(message);
        Server.broadcastPacket(players, pk);
    }

    public static void sendMessage(Collection<Player> players, TextContainer message) {
        sendMessage(players.toArray(new Player[0]), message);
    }

    public static void sendMessage(Player[] players, TextContainer message) {
        if (message instanceof TranslationContainer) {
            sendTranslation(players, message.getText(), ((TranslationContainer)message).getParameters());
        } else {
            sendMessage(players, message.getText());
        }
    }

    public static void sendTranslation(Collection<Player> players, String message) {
        sendTranslation(players.toArray(new Player[0]), message);
    }

    public static void sendTranslation(Player[] players, String message) {
        sendTranslation(players, message, new String[0]);
    }

    public static void sendTranslation(Collection<Player> players, String message, String[] parameters) {
        sendTranslation(players.toArray(new Player[0]), message, parameters);
    }

    public static void sendTranslation(Player[] players, String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (!Server.getInstance().isLanguageForced()) {
            pk.type = 2;
            pk.message = Server.getInstance().getLanguage().translateString(message, parameters, "nukkit.");

            for(int i = 0; i < parameters.length; ++i) {
                parameters[i] = Server.getInstance().getLanguage().translateString(parameters[i], parameters, "nukkit.");
            }

            pk.parameters = parameters;
        } else {
            pk.type = 0;
            pk.message = Server.getInstance().getLanguage().translateString(message, parameters);
        }

        Server.broadcastPacket(players, pk);
    }

    @Experimental
    public static synchronized void removeAreaBlocks(AxisAlignedBB bb, Level level){
        Block block = Block.get(0);
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.floorDouble(bb.getMaxX());
        int maxY = NukkitMath.floorDouble(bb.getMaxY());
        int maxZ = NukkitMath.floorDouble(bb.getMaxZ());
        for(int z = minZ; z <= maxZ; ++z) {
            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    level.setBlock(x, y, z, block, false, false);
                }
            }
        }
    }

    @Experimental
    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, ParticleEffect particleEffect){
        Block block = Block.get(0);
        if(particleEffect != null){
            int minX = NukkitMath.floorDouble(bb.getMinX());
            int minY = NukkitMath.floorDouble(bb.getMinY());
            int minZ = NukkitMath.floorDouble(bb.getMinZ());
            int maxX = NukkitMath.floorDouble(bb.getMaxX());
            int maxY = NukkitMath.floorDouble(bb.getMaxY());
            int maxZ = NukkitMath.floorDouble(bb.getMaxZ());
            for(int z = minZ; z <= maxZ; ++z) {
                for(int x = minX; x <= maxX; ++x) {
                    for(int y = minY; y <= maxY; ++y) {
                        level.setBlock(x, y, z, block, false, false);
                        level.addParticleEffect(new Location(x, y, z, level), particleEffect);
                    }
                }
            }
        }else{
            bb.forEach((i, i1, i2) -> {
                level.setBlock(i, i1, i2, block, false, false);
            });
        }
    }

    @Experimental
    public static void fallingAndDestroyAreaBlocks(AxisAlignedBB bb, Level level){
        Block air = Block.get(0);
        GameAPI.plugin.getLogger().warning(bb.toString());
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.floorDouble(bb.getMaxX());
        int maxY = NukkitMath.floorDouble(bb.getMaxY());
        int maxZ = NukkitMath.floorDouble(bb.getMaxZ());
        GameAPI.plugin.getLogger().warning(minX+":"+minY+":"+minZ);
        GameAPI.plugin.getLogger().warning(maxX+":"+maxY+":"+maxZ);
        for(int z = minZ; z <= maxZ; ++z) {
            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    GameAPI.plugin.getLogger().warning(x+":"+y+":"+z+":"+level.getName());
                    fallBlock(level.getBlock(x, y, z));
                    level.setBlock(x, y, z, air, false, false);
                }
            }
        }
    }

    @Experimental
    public static void fallBlock(Block block){
        CompoundTag nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("", block.x + 0.5)).add(new DoubleTag("", block.y)).add(new DoubleTag("", block.z + 0.5))).putList((new ListTag("Motion")).add(new DoubleTag("", 0.0)).add(new DoubleTag("", 0.0)).add(new DoubleTag("", 0.0))).putList((new ListTag("Rotation")).add(new FloatTag("", 0.0F)).add(new FloatTag("", 0.0F))).putInt("TileID", block.getId()).putByte("Data", block.getDamage());
        EntityFallingBlock fall = (EntityFallingBlock) Entity.createEntity("FallingSand", block.getLevel().getChunk((int)block.x >> 4, (int)block.z >> 4), nbt, new Object[0]);
        if (fall != null) {
            fall.spawnToAll();
        }
    }

    public void dropExpOrb(Location source, int exp) {
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

    public void showOnScreenTextureAnimation(Player player, int effectId){
        OnScreenTextureAnimationPacket pk = new OnScreenTextureAnimationPacket();
        pk.effectId = effectId;
        player.dataPacket(pk);
    }

}
