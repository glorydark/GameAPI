package gameapi.manager;

import cn.nukkit.Player;
import gameapi.GameAPI;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
@Data
public class GameDebugManager {

    private static boolean enableConsoleDebug = true;

    private static final List<Player> players = new ArrayList<>();

    public static void info(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        if (enableConsoleDebug) {
            GameAPI.getInstance().getLogger().info(message);
        }
    }

    public static void warning(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        if (enableConsoleDebug) {
            GameAPI.getInstance().getLogger().warning(message);
        }
    }

    public static void error(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        if (enableConsoleDebug) {
            GameAPI.getInstance().getLogger().error(message);
        }
    }

    public static void addPlayer(Player player) {
        players.add(player);
    }

    public static void removePlayer(Player player) {
        players.remove(player);
    }
}
