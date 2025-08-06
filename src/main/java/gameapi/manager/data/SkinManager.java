package gameapi.manager.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import gameapi.GameAPI;
import gameapi.tools.SkinTools;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 *
 * Skin manager is to make skin storage unified in one place
 * for minigame dev. This is quite useful for loading too many
 * skins at a time without classification.
 */
public class SkinManager {

    private static final Map<String, Map<String, Skin>> skins = new LinkedHashMap<>();

    private static final Map<Player, Skin> playerSkinCaches = new LinkedHashMap<>();

    public static boolean registerSkin(String gameName, String id, String skinPath) {
        Skin skin = SkinTools.loadSkin(skinPath, id);
        if (skin != null) {
            if (skins.containsKey(id)) {
                GameAPI.getGameDebugManager().warning("Found a repeated registry on skin {" + id + "} by game {" + gameName + "}, trying to replace it.");
            }
            skins.computeIfAbsent(gameName, s -> new LinkedHashMap<>()).put(id, skin);
            return true;
        } else {
            return false;
        }
    }

    public static void clearSkin(String gameName) {
        skins.remove(gameName);
    }

    public static void clearSkin() {
        skins.clear();
    }

    @Nullable
    public static Skin getSkin(String gameName, String id) {
        return skins.getOrDefault(gameName, new LinkedHashMap<>()).get(id);
    }

    public static void saveSkinToCache(Player player) {
        playerSkinCaches.put(player, player.getSkin());
    }

    public static void removeSkinFromCache(Player player) {
        playerSkinCaches.remove(player);
    }

    @Nullable
    public static Skin getSkinCache(Player player) {
        return playerSkinCaches.get(player);
    }

    public static boolean recoverSkin(Player player) {
        if (playerSkinCaches.containsKey(player)) {
            Skin skin = getSkinCache(player);
            if (skin != null) {
                player.setSkin(skin);
                PlayerSkinPacket pk = new PlayerSkinPacket();
                pk.skin = skin;
                pk.oldSkinName = "";
                pk.newSkinName = skin.getSkinId();
                pk.uuid = player.getUniqueId();
                pk.premium = skin.isPremium();
                Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), pk);
            }
            removeSkinFromCache(player);
            return true;
        }
        return false;
    }

    public static boolean setSkin(Player player, String gameName, String skinId) {
        return setSkin(player, gameName, skinId, true);
    }

    public static boolean setSkin(Player player, String gameName, String skinId, boolean cache) {
        if (skins.containsKey(gameName)) {
            Skin skin = skins.getOrDefault(gameName, new LinkedHashMap<>()).get(skinId);
            if (skin != null) {
                if (cache) {
                    saveSkinToCache(player);
                }
                SkinTools.setSkin(player, skin);
                return true;
            }
        }
        return false;
    }
}
