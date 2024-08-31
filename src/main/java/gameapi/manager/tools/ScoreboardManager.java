package gameapi.manager.tools;

import cn.nukkit.Player;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import gameapi.tools.SmartTools;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glorydark
 * Dependence: ScoreBoardPlugin
 */
public class ScoreboardManager {
    public static ConcurrentHashMap<Player, Scoreboard> scoreboardConcurrentHashMap = new ConcurrentHashMap<>();

    public static void drawScoreBoardEntry(String objectiveName, String title, Player player, String... strings) {
        Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, objectiveName, title);
        if (scoreboardConcurrentHashMap.containsKey(player)) {
            scoreboardConcurrentHashMap.get(player).hideFor(player);
        }
        for (int lineId = 0; lineId < strings.length; lineId++) {
            scoreboardDisplay.addLine(strings[lineId], lineId);
        }
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player, scoreboard);
    }

    public static void removeScoreboard(Player player) {
        if (scoreboardConcurrentHashMap.containsKey(player)) {
            Scoreboard scoreboard = scoreboardConcurrentHashMap.get(player);
            scoreboard.hideFor(player);
            scoreboardConcurrentHashMap.remove(player);
        }
    }
}
