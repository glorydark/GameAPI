package gameapi.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import com.sun.org.glassfish.gmbal.Description;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Description("Author: Ruok") //引用若水的NBT物品保存代码

public class Inventory {
    public static LinkedHashMap<Player, ArrayList<String>> playerBag = new LinkedHashMap<>();

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
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

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
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

    public static void saveBag(Player gamePlayer) {
        ArrayList<String> bag = new ArrayList<>();
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            Item item = gamePlayer.getInventory().getItem(i);
            String nbt = "null";
            if (item.hasCompoundTag()) {
                nbt = bytesToHexString(item.getCompoundTag());
            }
            bag.add(item.getId() + "-" + item.getDamage() + "-" + item.getCount() + "-" + nbt);
        }
        playerBag.put(gamePlayer, bag);
        gamePlayer.getInventory().clearAll();
    }

    public static void loadBag(Player gamePlayer) {
        gamePlayer.getInventory().clearAll();
        ArrayList<String> bag = playerBag.get(gamePlayer);
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            String[] a = bag.get(i).split("-");
            Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
            if (!a[3].equals("null")) {
                CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
                item.setNamedTag(tag);
            }
            gamePlayer.getInventory().setItem(i, item);
        }
        playerBag.remove(gamePlayer);
    }
}
