package gameapi.utils;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SmartTools {

    //https://blog.csdn.net/weixin_39975055/article/details/115082818
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

    public static boolean isInRange(int min, int max, int compare){
        return Math.max(min, compare) == Math.min(compare, max);
    }

    public static SimpleAxisAlignedBB getAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2){
        return new SimpleAxisAlignedBB(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
    }

    /**
     * This is a method to set blocks in an area.
     *
     * @param bb the area you want to remove blocks
     * @param level the level you selected
     * @param block the block you want to replace the old ones
     *
     */
    public static synchronized void setAreaBlocks(AxisAlignedBB bb, Block block, Level level){
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to remove blocks in an area.
     *
     * @param bb the area you want to remove blocks
     * @param level the level you selected
     *
     */
    public static synchronized void removeAreaBlocks(AxisAlignedBB bb, Level level){
        Block block = new BlockAir();
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to destroy blocks in an area.
     *
     * @param bb the area you want to remove blocks
     * @param level the level you selected
     * @param particleEffect the variety of particle you want to display
     *
     */
    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, ParticleEffect particleEffect){
        Block block = Block.get(0);
        if(particleEffect != null){
            bb.forEach((i, i1, i2) -> {
                level.setBlock(i, i1, i2, block, false, false);
                level.addParticleEffect(new Location(i, i1, i2, level), particleEffect);
            });
        }else{
            bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
        }
    }

    /**
     * This is a method to summon a certain amount of Exp Orb entity.
     *
     * @param source position you intend to summon it
     * @param exp exp amount
     */
    public static void dropExpOrb(Location source, int exp) {
        if(source != null && source.getChunk() != null) {
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

    /**
     * This is a method to get the ordinal string by a number.
     *
     * @param number  大于0的数字
     * @param isAllCapital  是否全部大写
     * @return 序数词字符串
     */
    public static String getOrdinalString(int number, boolean isAllCapital){
        switch (number){
            case 1:
                if(isAllCapital){
                    return "1ST";
                }else {
                    return "1st";
                }
            case 2:
                if(isAllCapital){
                    return "2RD";
                }else {
                    return "2rd";
                }
            case 3:
                if(isAllCapital){
                    return "3RD";
                }else {
                    return "3rd";
                }
            default:
                if(number < 0){
                    return "Invalid Number";
                }
                if(isAllCapital){
                    return number+"TH";
                }else {
                    return number+"th";
                }
        }
    }

    public static AdvancedLocation parseLocation(String locationString){
        String[] positions = locationString.split(":");
        if(positions.length < 4){
            if(positions.length == 3){
                AdvancedLocation loc = new AdvancedLocation();
                loc.setLocation(new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2])));
                loc.setVersion(0);
                return loc;
            }
            GameAPI.plugin.getLogger().warning("Wrong Location Format! Please check it again, text: "+locationString);
            return null;
        }
        if(!Server.getInstance().isLevelLoaded(positions[3])){
            if(Server.getInstance().loadLevel(positions[3])){
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(0);
                if(positions.length >= 6){
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(1);
                    if(positions.length == 7){
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(2);
                    }
                }
                return advancedLocation;
            }else{
                return null;
            }
        }else{
            Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
            AdvancedLocation advancedLocation = new AdvancedLocation();
            advancedLocation.setLocation(location);
            advancedLocation.setVersion(0);
            if(positions.length >= 6){
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(1);
                if(positions.length == 7){
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(2);
                }
            }
            return advancedLocation;
        }
    }

}
