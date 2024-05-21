package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.OnScreenTextureAnimationPacket;
import cn.nukkit.network.protocol.SetTitlePacket;
import cn.nukkit.network.protocol.TextPacket;
import gameapi.utils.Language;

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
        if (!subtitle.equals("")) {
            setSubtitle(players, subtitle);
        }

        setTitle(players, title);
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

    public String getOS(Player p) {
        switch(p.getLoginChainData().getDeviceOS()) {
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
            default:
                return "Unknown";
        }
    }
}
