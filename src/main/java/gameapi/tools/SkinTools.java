package gameapi.tools;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.Utils;
import gameapi.GameAPI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
public class SkinTools {

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

    public static void savePlayerJson(String jsonString, File file) {
        try {
            Utils.writeFile(file, jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseSerializedImage(SerializedImage image, File file) {
        byte[] data = image.data;
        if (data == null) {
            GameAPI.plugin.getLogger().warning("data为null");
            return;
        }
        if (data.length == 0) {
            GameAPI.plugin.getLogger().warning("data长度为0");
            return;
        }

        int width = image.width;
        int height = image.height;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int xOffsetHere = x << 2;
                int previousOffsets = (y << 2) * width;
                // 等同于 int rIndex = x*4+y*4*width;
                int rIndex = xOffsetHere + previousOffsets;
                bufferedImage.setRGB(x, y, new Color(data[rIndex] & 255, data[rIndex + 1] & 255, data[rIndex + 2] & 255, data[rIndex + 3] & 255).getRGB()); // 记得对byte进行转换，转换为int
            }
        }

        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
