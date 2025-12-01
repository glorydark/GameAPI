package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.inventory.ContainerSlotType;
import cn.nukkit.network.protocol.types.inventory.FullContainerName;
import gameapi.utils.Language;
import gameapi.utils.TitleData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author glorydark
 * @date {2023/7/14} {19:29}
 */
public class PlayerTools {

    public static void sendActionbar(Collection<Player> players, String title) {
        sendActionbar(players.toArray(new Player[0]), title, 1, 0, 1);
    }

    public static void sendActionbar(Player[] players, String title) {
        sendActionbar(players, title, 1, 0, 1);
    }

    public static void sendActionbar(Collection<Player> players, String title, int fadein, int duration, int fadeout) {
        sendActionbar(players.toArray(new Player[0]), title, fadein, duration, fadeout);
    }

    public static void sendActionbar(Player[] players, String title, int fadein, int duration, int fadeout) {
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
        sendTitle(players.toArray(new Player[0]), title, "", 10, 20, 10);
    }

    public static void sendTitle(Collection<Player> players, String title, String subtitle) {
        sendTitle(players.toArray(new Player[0]), title, subtitle, 10, 20, 10);
    }

    public static void sendTitle(Player[] players, String title) {
        sendTitle(players, title, "");
    }

    public static void sendTitle(Player[] players, String title, String subtitle) {
        sendTitle(players, title, subtitle, 10, 20, 10);
    }

    public static void sendTitle(Collection<Player> players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(players.toArray(new Player[0]), title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        setTitleAnimationTimes(players, fadeIn, stay, fadeOut);
        if (!subtitle.isEmpty()) {
            setSubtitle(players, subtitle);
        }
        setTitle(players, title);
    }

    public static void sendTitle(Collection<Player> players, TitleData titleData) {
        for (Player player : players) {
            player.sendTitle(
                    titleData.getTitle().getText(player),
                    titleData.getSubtitle().getText(player),
                    titleData.getFadeIn(),
                    titleData.getDuration(),
                    titleData.getFadeOut()
            );
        }
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
            sendTranslation(players, message.getText(), ((TranslationContainer) message).getParameters());
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

            for (int i = 0; i < parameters.length; ++i) {
                parameters[i] = Server.getInstance().getLanguage().translateString(parameters[i], parameters, "nukkit.");
            }

            pk.parameters = parameters;
        } else {
            pk.type = 0;
            pk.message = Server.getInstance().getLanguage().translateString(message, parameters);
        }

        Server.broadcastPacket(players, pk);
    }

    /**
     * This is a method to send the OnScreenTextureAnimationPacket.
     * You may see a large shaking effect icon on your screen when used properly.
     *
     * @param player   玩家
     * @param effectId 药水效果id
     */
    public static void showOnScreenTextureAnimation(Player player, int effectId) {
        OnScreenTextureAnimationPacket pk = new OnScreenTextureAnimationPacket();
        pk.effectId = effectId;
        player.dataPacket(pk);
    }

    public static void showOnScreenTextureAnimation(Collection<Player> players, int effectId) {
        for (Player p : players) {
            showOnScreenTextureAnimation(p, effectId);
        }
    }

    public static void fakeTotemUsing(Player player) {
        boolean totemOwned = false;
        if (player.getInventory().getItemInHandFast().getId() == ItemID.TOTEM) {
            totemOwned = true;
        }
        if (player.getOffhandInventory().getItemFast(0).getId() == ItemID.TOTEM) {
            totemOwned = true;
        }
        if (!totemOwned) {
            InventoryContentPacket offhandPacket = new InventoryContentPacket();
            offhandPacket.containerNameData = new FullContainerName(ContainerSlotType.OFFHAND, null);
            offhandPacket.inventoryId = InventoryContentPacket.SPECIAL_OFFHAND;
            offhandPacket.slots = new Item[]{Item.get(ItemID.TOTEM)};
            player.dataPacket(offhandPacket);
        }
        player.getLevel().addLevelEvent(player.getPosition(), LevelEventPacket.EVENT_SOUND_TOTEM);
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = player.getId();
        pk.event = EntityEventPacket.CONSUME_TOTEM;
        player.dataPacket(pk);
        if (totemOwned) {
            player.getOffhandInventory().sendSlot(0);
        }
    }

    // Translation

    public static void sendMessage(Collection<Player> players, Language language, String string, Object... params) {
        for (Player player : players) {
            player.sendMessage(language.getTranslation(player, string, params));
        }
    }

    public static void sendTitle(Collection<Player> players, Language language, String string, Object... params) {
        for (Player player : players) {
            player.sendTitle(language.getTranslation(player, string, params));
        }
    }

    public static void sendActionbar(Collection<Player> players, Language language, String string, Object... params) {
        for (Player player : players) {
            player.sendActionBar(language.getTranslation(player, string, params));
        }
    }

    public static void sendTip(Collection<Player> players, Language language, String string, Object... params) {
        for (Player player : players) {
            player.sendTip(language.getTranslation(player, string, params));
        }
    }

    public static boolean isPC(Player player) {
        int deviceOs = player.getLoginChainData().getDeviceOS();
        switch (deviceOs) {
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public static String getOS(Player p) {
        switch (p.getLoginChainData().getDeviceOS()) {
            case 1:
                return "Android";
            case 2:
                return "iOS";
            case 3:
                return "Mac";
            case 4:
                return "Fire";
            case 5:
                return "Gear VR";
            case 6:
                return "HoloLens";
            case 7:
                return "Windows 10";
            case 8:
                return "Windows";
            case 9:
                return "Dedicated";
            case 10:
                return "tvOS";
            case 11:
                return "PlayStation";
            case 12:
                return "NX";
            case 13:
                return "Xbox";
            case 14:
                return "Windows Phone";
            case 15:
                return "Linux";
            default:
                return "Unknown";
        }
    }

    public static String getTrimmedPlayerName(Player player, int maxLength) {
        if (player.getName().length() <= maxLength) {
            return player.getName();
        } else {
            return player.getName().substring(0, maxLength - 4) + "...";
        }
    }

    public static void resetSpeed(Player player, float moveSpeed) {
        player.setSprinting(false);
        player.setGliding(false);
        player.setCrawling(false);
        player.setSwimming(false);
        player.setSneaking(false);
        player.removeAllEffects();
        player.teleport(player.add(0, 0.1, 0), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        player.setMovementSpeed(moveSpeed);
    }

    //边缘检测
    private static final double PLAYER_BOUNDINGBOX_ADD = 0.3;

    @Nullable
    public static Block getBlockUnderPlayer(Player player) {
        Block block = null;
        int y = player.getLevelBlock().getFloorY();
        for (int i = 0; i <= 1; i++) {
            block = getBlockUnderPlayer(y, player.getLocation());
            y--;
            if (block != null) {
                break;
            }
        }
        return block;
    }

    @Nullable
    public static Block getBlockUnderPlayer(int y, Location location) {
        return getBlockUnderPlayer(y, location, location.getLevel());
    }

    @Nullable
    public static Block getBlockUnderPlayer(int y, Vector3 pos, Level level) {
        Location loc = new Location(pos.getX(), y, pos.getZ(), level);
        loc.setY(y);
        Block b11 = loc.add(+PLAYER_BOUNDINGBOX_ADD,0, -PLAYER_BOUNDINGBOX_ADD).getLevelBlock();
        if (b11.getId() != BlockID.AIR) {
            return b11;
        }
        Block b12 = loc.add(-PLAYER_BOUNDINGBOX_ADD,0, +PLAYER_BOUNDINGBOX_ADD).getLevelBlock();
        if (b12.getId() != BlockID.AIR) {
            return b12;
        }
        Block b21 = loc.add(+PLAYER_BOUNDINGBOX_ADD,0, +PLAYER_BOUNDINGBOX_ADD).getLevelBlock();
        if (b21.getId() != BlockID.AIR) {
            return b21;
        }
        Block b22 = loc.add(-PLAYER_BOUNDINGBOX_ADD,0, -PLAYER_BOUNDINGBOX_ADD).getLevelBlock();
        if (b22.getId() != BlockID.AIR) {
            return b22;
        }

        return null;
    }
}
