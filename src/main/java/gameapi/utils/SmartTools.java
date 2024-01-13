package gameapi.utils;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gameapi.GameAPI;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SmartTools {

    public static String timeDiffMillisToString(long m1, long m2) {
        if (m2 - m1 < 0) {
            GameAPI.plugin.getLogger().error("End millis should not be bigger than start millis");
            return "";
        }
        long diff = Math.abs(m2 - m1);
        return timeMillisToString(diff);
    }

    public static String timeMillisToString(long diff) {
        long hour = diff / 3600000;
        long minute = diff / 60000 - hour * 60;
        long second = (diff - hour * 3600000 - minute * 60000) / 1000;
        long millis = (diff - hour * 3600000 - minute * 60000 - second * 1000);
        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            if (hour < 10) {
                sb.append("0").append(hour).append(":");
            } else {
                sb.append(hour).append(":");
            }
        }
        if (minute > 0) {
            if (minute < 10) {
                sb.append("0").append(minute).append(":");
            } else {
                sb.append(minute).append(":");
            }
        } else {
            sb.append("00:");
        }
        if (second > 0) {
            if (second < 10) {
                sb.append("0").append(second).append(":");
            } else {
                sb.append(second).append(":");
            }
        } else if (second == 0) {
            sb.append("00:");
        }
        if (millis > 0) {
            sb.append(millis);
        } else {
            sb.append("000");
        }
        return sb.toString();
    }

    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    public static String dateToString(Date date) {
        return dateToString(date, "yyyy-MM-dd-HH-mm-ss");
    }

    public static String dateToString(Date date, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    @Deprecated
    public static Date stringToDate(String string) {
        return stringToDate(string, "yyyy-MM-dd-HH-mm-ss");
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

    public static boolean isInRange(int min, int max, int compare) {
        return Math.max(min, compare) == Math.min(compare, max);
    }

    public static SimpleAxisAlignedBB getAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new SimpleAxisAlignedBB(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
    }

    /**
     * This is a method to set blocks in an area.
     *
     * @param bb    the area you want to remove blocks
     * @param level the level you selected
     * @param block the block you want to replace the old ones
     */
    public static synchronized void setAreaBlocks(AxisAlignedBB bb, Block block, Level level) {
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to remove blocks in an area.
     *
     * @param bb    the area you want to remove blocks
     * @param level the level you selected
     */
    public static synchronized void removeAreaBlocks(AxisAlignedBB bb, Level level) {
        Block block = new BlockAir();
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to destroy blocks in an area.
     *
     * @param bb             the area you want to remove blocks
     * @param level          the level you selected
     * @param particleEffect the variety of particle you want to display
     */
    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, ParticleEffect particleEffect) {
        Block block = Block.get(0);
        if (particleEffect != null) {
            bb.forEach((i, i1, i2) -> {
                level.setBlock(i, i1, i2, block, false, false);
                level.addParticleEffect(new Location(i, i1, i2, level), particleEffect);
            });
        } else {
            bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
        }
    }

    /**
     * This is a method to summon a certain amount of Exp Orb entity.
     *
     * @param source position you intend to summon it
     * @param exp    exp amount
     */
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

    /**
     * This is a method to get the ordinal string by a number.
     *
     * @param number       大于0的数字
     * @param isAllCapital 是否全部大写
     * @return 序数词字符串
     */
    public static String getOrdinalString(int number, boolean isAllCapital) {
        switch (number) {
            case 1:
                if (isAllCapital) {
                    return "1ST";
                } else {
                    return "1st";
                }
            case 2:
                if (isAllCapital) {
                    return "2ND";
                } else {
                    return "2nd";
                }
            case 3:
                if (isAllCapital) {
                    return "3RD";
                } else {
                    return "3rd";
                }
            default:
                if (number < 0) {
                    return "Invalid Number";
                }
                if (isAllCapital) {
                    return number + "TH";
                } else {
                    return number + "th";
                }
        }
    }

    public static List<AdvancedLocation> parseLocations(String... locationString) {
        List<AdvancedLocation> advancedLocations = new ArrayList<>();
        for (String s : locationString) {
            advancedLocations.add(parseLocation(s));
        }
        return advancedLocations;
    }

    public static AdvancedLocation parseLocation(String locationString) {
        String[] positions = locationString.split(":");
        if (positions.length < 4) {
            if (positions.length == 3) {
                AdvancedLocation loc = new AdvancedLocation();
                loc.setLocation(new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2])));
                loc.setVersion(0);
                return loc;
            }
            GameAPI.plugin.getLogger().warning("Wrong Location Format! Please check it again, text: " + locationString);
            return null;
        }
        if (!Server.getInstance().isLevelLoaded(positions[3])) {
            if (Server.getInstance().loadLevel(positions[3])) {
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(0);
                if (positions.length >= 6) {
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(1);
                    if (positions.length == 7) {
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(2);
                    }
                }
                return advancedLocation;
            } else {
                return null;
            }
        } else {
            Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
            AdvancedLocation advancedLocation = new AdvancedLocation();
            advancedLocation.setLocation(location);
            advancedLocation.setVersion(0);
            if (positions.length >= 6) {
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(1);
                if (positions.length == 7) {
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(2);
                }
            }
            return advancedLocation;
        }
    }

    public static Skin loadSkin(String skinPath, String loadName) {
        File skinDataFile = new File(skinPath + "/skin.png");
        File skinJsonFile = new File(skinPath + "/skin.json");
        File skinAnimJsonFile = new File(skinPath + "/skin.animation.json");
        if (skinDataFile.exists()) {
            Skin skin = new Skin();
            skin.setSkinId(loadName);
            try {
                skin.setSkinData(ImageIO.read(skinDataFile));
            } catch (Exception e) {
                GameAPI.plugin.getLogger().error("皮肤 " + loadName + " 读取错误，请检查图片格式或图片尺寸！", e);
            }

            //如果是4D皮肤
            if (skinJsonFile.exists()) {
                Map<String, Object> skinJson = (new Config(skinJsonFile, Config.JSON)).getAll();
                String geometryName = null;

                String formatVersion = (String) skinJson.getOrDefault("format_version", "1.10.0");
                skin.setGeometryDataEngineVersion(formatVersion); //设置皮肤版本，主流格式有1.16.0,1.12.0(Blockbench新模型),1.10.0(Blockbench Legacy模型),1.8.0
                switch (formatVersion) {
                    case "1.16.0":
                    case "1.12.0":
                        geometryName = getGeometryName(skinJsonFile);
                        if (geometryName.equals("nullvalue")) {
                            GameAPI.plugin.getLogger().error("暂不支持该版本格式的皮肤！请等待更新！");
                        } else {
                            skin.generateSkinId(loadName);
                            skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + geometryName + "\"}}");
                            skin.setGeometryName(geometryName);
                            skin.setGeometryData(readFile(skinJsonFile));
                            GameAPI.plugin.getLogger().info("皮肤 " + loadName + " 读取中");
                        }
                        break;
                    default:
                        GameAPI.plugin.getLogger().warning("[" + loadName + "] 的版本格式为：" + formatVersion + "，正在尝试加载！");
                    case "1.10.0":
                    case "1.8.0":
                        for (Map.Entry<String, Object> entry : skinJson.entrySet()) {
                            if (geometryName == null) {
                                if (entry.getKey().startsWith("geometry")) {
                                    geometryName = entry.getKey();
                                }
                            } else {
                                break;
                            }
                        }
                        skin.generateSkinId(loadName);
                        skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + geometryName + "\"}}");
                        skin.setGeometryName(geometryName);
                        skin.setGeometryData(readFile(skinJsonFile));
                        if (skinAnimJsonFile.exists()) {
                            skin.setAnimationData(readFile(skinAnimJsonFile));
                        }
                        break;
                }
            }
            skin.setTrusted(true);
            if (skin.isValid()) {
                GameAPI.plugin.getLogger().info("皮肤 " + loadName + " 读取完成");
                return skin;
            } else {
                GameAPI.plugin.getLogger().error("皮肤 " + loadName + " 验证失败，请检查皮肤文件完整性！");
            }
        } else {
            GameAPI.plugin.getLogger().error("皮肤 " + loadName + " 错误的名称格式，请将皮肤文件命名为 skin.png 模型文件命名为 skin.json");
        }
        return null;
    }

    protected static String getGeometryName(File file) {
        Config originGeometry = new Config(file, Config.JSON);
        if (!originGeometry.getString("format_version").equals("1.12.0") && !originGeometry.getString("format_version").equals("1.16.0")) {
            return "nullvalue";
        }
        //先读取minecraft:geometry下面的项目
        List<Map<String, Object>> geometryList = (List<Map<String, Object>>) originGeometry.get("minecraft:geometry");
        //不知道为何这里改成了数组，所以按照示例文件读取第一项
        Map<String, Object> geometryMain = geometryList.get(0);
        //获取description内的所有
        Map<String, Object> descriptions = (Map<String, Object>) geometryMain.get("description");
        return (String) descriptions.getOrDefault("identifier", "geometry.unknown"); //获取identifier
    }

    protected static String readFile(File file) {
        String content = "";
        try {
            content = Utils.readFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static List<Vector3> parseVectorsFromStrings(String... str) {
        List<Vector3> vector3List = new ArrayList<>();
        for (String s : str) {
            vector3List.add(parseVectorFromString(s));
        }
        return vector3List;
    }

    public static Vector3 parseVectorFromString(String str) {
        String[] locArray = str.split(":");
        if (locArray.length == 3) {
            return new Vector3(Double.parseDouble(locArray[0]), Double.parseDouble(locArray[1]), Double.parseDouble(locArray[2]));
        } else {
            return null;
        }
    }

    public static Map<String, Object> convertConfigToMap(File file) {
        if (file.getName().endsWith(".json")) {
            InputStream stream;
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8); //一定要以utf-8读取
            JsonReader reader = new JsonReader(streamReader);
            Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new GsonAdapter()).create();
            Map<String, Object> mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
            }.getType());

            // Remember to close the streamReader after your implementation.
            try {
                reader.close();
                streamReader.close();
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return mainMap;
        } else if (file.getName().endsWith(".yml")) {
            return new Config(file, Config.YAML).getAll();
        }
        return new HashMap<>();
    }

    public static Item parseItemFromMap(Map<String, Object> map) {
        Item item = Item.fromString((String) map.get("id"));
        item.setDamage((Integer) map.getOrDefault("damage", 1));
        item.setCount((Integer) map.getOrDefault("count", 1));
        if (!item.hasCompoundTag()) {
            item.setNamedTag(new CompoundTag().putBoolean("Unbreakable", true));
        } else {
            item.getNamedTag().putBoolean("Unbreakable", true);
        }
        if (map.containsKey("enchantments")) {
            List<Map<String, Object>> enchantmentDataEntries = (List<Map<String, Object>>) map.get("enchantments");
            for (Map<String, Object> enchantmentDataEntry : enchantmentDataEntries) {
                Enchantment enchantment = Enchantment.getEnchantment((Integer) enchantmentDataEntry.get("id"));
                enchantment.setLevel((Integer) enchantmentDataEntry.getOrDefault("level", 1));
                item.addEnchantment(enchantment);
            }
        }
        return item;
    }
}
