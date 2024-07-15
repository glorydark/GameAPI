package gameapi.manager;

import cn.nukkit.Player;
import gameapi.GameAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class GameDebugManager {

    private static final List<Player> players = new ArrayList<>();

    public static void info(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        GameAPI.getInstance().getLogger().info(message);
    }

    public static void warning(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        GameAPI.getInstance().getLogger().warning(message);
    }

    public static void error(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        GameAPI.getInstance().getLogger().error(message);
    }

    public static void addPlayer(Player player) {
        players.add(player);
    }

    public static void removePlayer(Player player) {
        players.remove(player);
    }

    public static List<Player> getPlayers() {
        return players;
    }
}
