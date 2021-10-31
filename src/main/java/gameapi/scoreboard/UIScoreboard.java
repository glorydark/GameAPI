package gameapi.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.concurrent.ConcurrentHashMap;

public class UIScoreboard {
    public static ConcurrentHashMap<Player, Scoreboard> scoreboardConcurrentHashMap = new ConcurrentHashMap<>();

    public static void drawScoreBoardEntry(Player player, String objectiveName, String displayName, String string){
        removeScoreboard(player);
        Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, objectiveName, displayName );
        scoreboardDisplay.addLine(string,0);
        ScoreboardAPI.setScoreboard(player,scoreboard);
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player,scoreboard);
    }

    public static void drawScoreBoardEntry(Player player, String objectiveName, String displayName, String string, String string2){
        removeScoreboard(player);
        Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, objectiveName, displayName );
        scoreboardDisplay.addLine(string,0);
        scoreboardDisplay.addLine(string2,1);
        ScoreboardAPI.setScoreboard(player,scoreboard);
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player,scoreboard);
    }

    public static void drawTimeBoardEntry(Player player, String objectiveName, String displayName, String string){
        removeScoreboard(player);
        Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, objectiveName, displayName );
        scoreboardDisplay.addLine(string,0);
        ScoreboardAPI.setScoreboard(player,scoreboard);
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player,scoreboard);
    }

    public static void removeScoreboard(Player player){
        if(scoreboardConcurrentHashMap.get(player) != null){
            ScoreboardAPI.removeScorebaord(player,scoreboardConcurrentHashMap.get(player));
        }
    }

    public static String secToTime(int seconds) {
        int hour = seconds / 3600;
        int minute = (seconds - hour * 3600) / 60;
        int second = (seconds - hour * 3600 - minute * 60);

        StringBuffer sb = new StringBuffer();
        if (hour > 0) {
            if(hour < 10) {
                sb.append("0"+hour + ":");
            }else{
                sb.append(hour + ":");
            }
        }
        if (minute > 0) {
            if(minute < 10) {
                sb.append("0"+minute + ":");
            }else{
                sb.append(minute + ":");
            }
        }else{
            sb.append("00:");
        }
        if (second > 0) {
            if(second < 10) {
                sb.append("0"+second);
            }else{
                sb.append(second);
            }
        }
        if (second == 0) {
            sb.append("00");
        }
        return sb.toString();
    }
}
