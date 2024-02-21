package gameapi.tools;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.utils.NukkitTypeUtils;

import java.util.List;
import java.util.Map;

/**
 * Adapted from SmallAsWater's method
 * Glorydark added some changes
 */

public class InventoryTools {

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

    public static String toBase64String(Item item) {
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
