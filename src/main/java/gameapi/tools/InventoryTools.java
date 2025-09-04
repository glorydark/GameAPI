package gameapi.tools;

import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.annotation.Description;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author glorydark
 */
public class InventoryTools {

    public static final int ONLINE_SLOT_FAST_START = 0;
    public static final int ONLINE_SLOT_FAST_HELMET = 36;
    public static final int ONLINE_SLOT_FAST_CHESTPLATE = 37;
    public static final int ONLINE_SLOT_FAST_LEGGINGS = 38;
    public static final int ONLINE_SLOT_FAST_BOOTS = 39;
    public static final int ONLINE_SLOT_FAST_OFFHAND = 40;

    public static final int OFFLINE_SLOT_FAST_START = 9;
    public static final int OFFLINE_SLOT_FAST_HELMET = 45;
    public static final int OFFLINE_SLOT_FAST_CHESTPLATE = 46;
    public static final int OFFLINE_SLOT_FAST_LEGGINGS = 47;
    public static final int OFFLINE_SLOT_FAST_BOOTS = 48;
    public static final int OFFLINE_SLOT_FAST_OFFHAND = 49;

    public static Map<String, String> enchantmentLang = new LinkedHashMap<>() {
        {
            this.put("enchantment.arrowDamage", "力量");
            this.put("enchantment.arrowFire", "火矢");
            this.put("enchantment.arrowInfinite", "无限");
            this.put("enchantment.arrowKnockback", "冲击");
            this.put("enchantment.crossbowMultishot", "多重射击");
            this.put("enchantment.crossbowPiercing", "穿透");
            this.put("enchantment.crossbowQuickCharge", "快速装填");
            this.put("enchantment.curse.binding", "绑定诅咒");
            this.put("enchantment.curse.vanishing", "消失诅咒");
            this.put("enchantment.damage.all", "锋利");
            this.put("enchantment.damage.arthropods", "节肢杀手");
            this.put("enchantment.damage.undead", "亡灵杀手");
            this.put("enchantment.digging", "效率");
            this.put("enchantment.durability", "耐久");
            this.put("enchantment.fire", "火焰附加");
            this.put("enchantment.fishingSpeed", "饵钓");
            this.put("enchantment.frostwalker", "冰霜行者");
            this.put("enchantment.heavy_weapon.breach", "破甲");
            this.put("enchantment.heavy_weapon.density", "致密");
            this.put("enchantment.heavy_weapon.windburst", "风爆");
            this.put("enchantment.knockback", "击退");
            this.put("enchantment.level.1", "I");
            this.put("enchantment.level.10", "X");
            this.put("enchantment.level.2", "II");
            this.put("enchantment.level.3", "III");
            this.put("enchantment.level.4", "IV");
            this.put("enchantment.level.5", "V");
            this.put("enchantment.level.6", "VI");
            this.put("enchantment.level.7", "VII");
            this.put("enchantment.level.8", "VIII");
            this.put("enchantment.level.9", "IX");
            this.put("enchantment.lootBonus", "抢夺");
            this.put("enchantment.lootBonusDigger", "时运");
            this.put("enchantment.lootBonusFishing", "海之眷顾");
            this.put("enchantment.mending", "经验修补");
            this.put("enchantment.oxygen", "水下呼吸");
            this.put("enchantment.protect.all", "保护");
            this.put("enchantment.protect.explosion", "爆炸保护");
            this.put("enchantment.protect.fall", "摔落保护");
            this.put("enchantment.protect.fire", "火焰保护");
            this.put("enchantment.protect.projectile", "弹射物保护");
            this.put("enchantment.soul_speed", "灵魂疾行");
            this.put("enchantment.swift_sneak", "迅捷潜行");
            this.put("enchantment.thorns", "荆棘");
            this.put("enchantment.untouching", "精准采集");
            this.put("enchantment.waterWalker", "深海探索者");
            this.put("enchantment.waterWorker", "水下速掘");
            this.put("enchantment.tridentChanneling", "引雷");
            this.put("enchantment.tridentLoyalty", "忠诚");
            this.put("enchantment.tridentRiptide", "激流");
            this.put("enchantment.tridentImpaling", "穿刺");
        }
    };

    public static Map<Integer, Item> getOnlinePlayerInventoryItems(String name) {
        Player player1 = Server.getInstance().getPlayer(name);
        Map<Integer, Item> map = new LinkedHashMap<>();
        if (player1 != null) {
            map.putAll(player1.getInventory().getContents());
            map.put(ONLINE_SLOT_FAST_OFFHAND, player1.getOffhandInventory().getItem(0));
        }
        return map;
    }

    public static Map<Integer, Item> getOfflinePlayerInventoryItems(String name) {
        return getOfflinePlayerInventoryItems(name, 0);
    }

    public static Map<Integer, Item> getOfflinePlayerInventoryItems(String name, int slotOffset) {
        Map<Integer, Item> contents = new LinkedHashMap<>();
        OfflinePlayer offlinePlayer = (OfflinePlayer) Server.getInstance().getOfflinePlayer(name);
        if (offlinePlayer != null) {
            CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayer.getUniqueId(), false);
            ListTag<CompoundTag> inventory = namedTag.getList("Inventory", CompoundTag.class);
            for (int i = 0; i < inventory.size(); i++) {
                if (i + slotOffset < 0) {
                    continue;
                }
                CompoundTag testTag = inventory.get(i);
                Item item = NBTIO.getItemHelper(testTag);
                contents.put(i + slotOffset, item);
            }
        }
        return contents;
    }

    @Description(usage = "This method will automatically convert slot index to online for compatibility")
    public static Map<Integer, Item> getPlayerInventoryItems(String name) {
        if (Server.getInstance().lookupName(name).isPresent()) {
            Player player1 = Server.getInstance().getPlayer(name);
            if (player1 != null) {
                return player1.getInventory().getContents();
            } else {
                return getOfflinePlayerInventoryItems(name, -9);
            }
        } else {
            return new LinkedHashMap<>();
        }
    }

    public static Map<Integer, Item> getPlayerEnderChestItems(String name) {
        Optional<Player> seePlayer = Optional.ofNullable(Server.getInstance().getPlayer(name));
        if (seePlayer.isPresent()) {
            return new LinkedHashMap<>(seePlayer.get().getEnderChestInventory().slots);
        } else {
            OfflinePlayer offlinePlayer = (OfflinePlayer) Server.getInstance().getOfflinePlayer(name);
            if (offlinePlayer != null) {
                Map<Integer, Item> contents = new LinkedHashMap<>();
                CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayer.getUniqueId(), false);
                ;

                if (namedTag.contains("EnderItems") && namedTag.get("EnderItems") instanceof ListTag) {
                    ListTag<CompoundTag> inventoryList = namedTag.getList("EnderItems", CompoundTag.class);
                    for (CompoundTag compoundTag : inventoryList.getAll()) {
                        Item item = NBTIO.getItemHelper(compoundTag);
                        contents.put(compoundTag.getByte("Slot"), item);
                    }
                }

                return contents;
            } else {
                return new LinkedHashMap<>();
            }
        }
    }
}
