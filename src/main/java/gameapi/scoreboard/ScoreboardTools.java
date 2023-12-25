package gameapi.scoreboard;

import cn.nukkit.Player;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glorydark
 * Dependence: ScoreBoardPlugin
 */
public class ScoreboardTools {
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

    public static String secToTime(int seconds) {
        int hour = seconds / 3600;
        int minute = (seconds - hour * 3600) / 60;
        int second = (seconds - hour * 3600 - minute * 60);

        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            if (hour < 10) {
                sb.append("0").append(hour).append(":");
            } else {
                sb.append(hour).append(":");
            }
        }
        if (minute > 0) {
            if (minute < 10) {
                sb.append("0").append(minute).append(":");
            } else {
                sb.append(minute).append(":");
            }
        } else {
            sb.append("00:");
        }
        if (second > 0) {
            if (second < 10) {
                sb.append("0").append(second);
            } else {
                sb.append(second);
            }
        }
        if (second == 0) {
            sb.append("00");
        }
        return sb.toString();
    }
}
