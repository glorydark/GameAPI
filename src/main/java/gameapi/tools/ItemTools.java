package gameapi.tools;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.utils.NukkitTypeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapted from SmallAsWater's method
 * Glorydark added some changes
 */

public class ItemTools {

    private static final Pattern ITEM_STRING_PATTERN = Pattern.compile(
            "^(?:" +
                    "(\\d+)(?::(-?\\d+))?(?::(\\d+))?(?::([\\w+/=-]+))?" +  // 数字ID格式
                    "|" +
                    "(?:([a-z_]\\w*):)?([a-z._]\\w*)(?::(-?\\d+))?(?::(\\d+))?(?::([\\w+/=-]+))?" +  // 命名空间ID格式
                    ")$",
            Pattern.CASE_INSENSITIVE
    );

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("null")) {
            return new byte[0];
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length == 0) {
            return "null";
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String toString(Item item) {
        if (item == null) {
            return "0:0:0:null";
        }
        switch (NukkitTypeUtils.getNukkitType()) {
            case POWER_NUKKIT_X:
            case POWER_NUKKIT_X_2:
            case MOT:
                if (item.hasCompoundTag()) {
                    return item.getNamespaceId() + ":" + item.getDamage() + ":" + item.getCount() + ":" + bytesToHexString(item.getCompoundTag());
                } else {
                    return item.getNamespaceId() + ":" + item.getDamage() + ":" + item.getCount() + ":null";
                }
            default:
                if (item.hasCompoundTag()) {
                    return item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":" + bytesToHexString(item.getCompoundTag());
                } else {
                    return item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":null";
                }
        }
    }

    public static Item fromBase64String(String itemString) {
        String[] strings = itemString.split(":");
        boolean isNumericId = false;
        try {
            int test = Integer.parseInt(strings[0]);
            isNumericId = true;
        } catch (Exception ignored) {

        }
        if (isNumericId) {
            Item item = Item.get(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
            item.setCompoundTag(hexStringToBytes(strings[3]));
            return item;
        } else {
            int countIndex = strings.length - 2;
            StringBuilder identifierAndMeta = new StringBuilder();
            for (int i = 0; i < strings.length - 2; i++) {
                identifierAndMeta.append(strings[i]);
                if (i != strings.length - 3) {
                    identifierAndMeta.append(":");
                }
            }
            Item item = Item.fromString(identifierAndMeta.toString());
            item.setCount(Integer.parseInt(strings[countIndex]));
            item.setCompoundTag(hexStringToBytes(strings[countIndex + 1]));
            return item;
        }
    }

    public static Item parseItemFromMap(Map<String, Object> map) {
        Item item = Item.fromString(map.get("id") + ":" + map.getOrDefault("damage", 0));
        item.setCount((Integer) map.getOrDefault("count", 1));

        String customName = (String) map.getOrDefault("custom_name", "");
        if (!customName.isEmpty()) {
            item.setCustomName(customName);
        }

        if ((Boolean) map.getOrDefault("unbreakable", false)) {
            if (!item.hasCompoundTag()) {
                item.setNamedTag(new CompoundTag().putBoolean("Unbreakable", true));
            } else {
                item.getNamedTag().putBoolean("Unbreakable", true);
            }
        }
        if (map.containsKey("enchantments")) {
            List<Map<String, Object>> enchantmentDataEntries = (List<Map<String, Object>>) map.get("enchantments");
            for (Map<String, Object> enchantmentDataEntry : enchantmentDataEntries) {
                Enchantment enchantment = Enchantment.getEnchantment((Integer) enchantmentDataEntry.get("id"));
                enchantment.setLevel((Integer) enchantmentDataEntry.getOrDefault("level", 1));
                item.addEnchantment(enchantment);
            }
        }
        if (map.containsKey("minecraft:item_lock")) {
            if (item.hasCompoundTag()) {
                item.getNamedTag().putByte("minecraft:item_lock", (Integer) map.getOrDefault("minecraft:item_lock", 0));
            } else {
                item.setNamedTag(new CompoundTag().putByte("minecraft:item_lock", (Integer) map.getOrDefault("minecraft:item_lock", 0)));
            }
        }

        if (map.containsKey("minecraft:keep_on_death")) {
            boolean keepOnDeath = (Boolean) map.getOrDefault("minecraft:keep_on_death", false);
            if (keepOnDeath) {
                if (item.hasCompoundTag()) {
                    item.getNamedTag().putByte("minecraft:keep_on_death", 1);
                } else {
                    item.setNamedTag(new CompoundTag().putByte("minecraft:keep_on_death", 1));
                }
            }
        }

        if (map.containsKey("tags")) {
            item.setNamedTag(parseCompoundTag(item, (Map<String, Object>) map.getOrDefault("tags", new LinkedHashMap<>())));
        }

        if (item.getNamedTag() != null) {
            item.setNamedTag(item.getNamedTag());
        }
        return item;
    }

    public static CompoundTag parseCompoundTag(Item item, Map<String, Object> map) {
        CompoundTag compoundTag = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        Map<String, Object> tagMap = (Map<String, Object>) map.getOrDefault("tags", new LinkedHashMap<>());
        for (Map.Entry<String, Object> entry : tagMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                compoundTag.putString(key, value.toString());
            } else if (value instanceof Double) {
                compoundTag.putDouble(key, (Double) value);
            } else if (value instanceof Float) {
                compoundTag.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                compoundTag.putLong(key, (Long) value);
            } else if (value instanceof Map) {
                if (!((Map<?, ?>) value).keySet().isEmpty()) {
                    Object object = new ArrayList<>(((Map<?, ?>) value).keySet()).get(0);
                    if (object instanceof String) {
                        compoundTag.putCompound(parseCompoundTag(item, (Map<String, Object>) value));
                    }
                }
            }
        }
        return compoundTag;
    }

    public static List<Enchantment> parseEnchantments(List<Map<String, Object>> mapList) {
        List<Enchantment> enchantments = new ArrayList<>();
        for (Map<String, Object> enchantmentDataEntry : mapList) {
            Enchantment enchantment = Enchantment.getEnchantment((Integer) enchantmentDataEntry.get("id"));
            enchantment.setLevel((Integer) enchantmentDataEntry.getOrDefault("level", 1));
            enchantments.add(enchantment);
        }
        return enchantments;
    }

    public static String getIdentifierAndMetaString(Item item) {
        switch (NukkitTypeUtils.getNukkitType()) {
            case POWER_NUKKIT_X:
            case POWER_NUKKIT_X_2:
            case MOT:
                return item.getNamespaceId() + ":" + item.getDamage();
            default:
                return item.getId() + ":" + item.getDamage();
        }
    }

    public static Item toItem(String itemString) {
        if (itemString == null || itemString.isEmpty() || itemString.startsWith("0:")) {
            return Item.get(0);
        }

        Matcher matcher = ITEM_STRING_PATTERN.matcher(itemString.trim().replace(' ', '_'));
        if (!matcher.matches()) {
            return Item.get(0);
        }

        try {
            // 解析数字ID格式
            if (matcher.group(1) != null) {
                int id = Integer.parseInt(matcher.group(1));
                int meta = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                int count = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 1;
                byte[] nbt = hexStringToBytes(matcher.group(4));

                Item item = Item.get(id, meta, count);
                item.setCompoundTag(nbt);
                return item;
            }

            // 解析命名空间ID格式
            String namespace = matcher.group(5);
            String id = matcher.group(6);
            String fullId = (namespace != null) ? namespace + ":" + id : "minecraft:" + id;

            int meta = matcher.group(7) != null ? Integer.parseInt(matcher.group(7)) : 0;
            int count = matcher.group(8) != null ? Integer.parseInt(matcher.group(8)) : 1;
            byte[] nbt = hexStringToBytes(matcher.group(9));

            Item item = Item.fromString(fullId + (meta != 0 ? ":" + meta : ""));
            item.setCount(count);
            item.setCompoundTag(nbt);
            return item;
        } catch (Exception e) {
            return Item.get(0);  // 解析失败返回空气
        }
    }
}
