package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.DisplaySlot;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;
import cn.nukkit.scoreboard.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.scoreboard.ScoreboardLine;
import cn.nukkit.scoreboard.scorer.FakeScorer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Glorydark
 * Dependence: ScoreBoardPlugin
 */
public class ScoreboardManager {

    public static Map<Player, Scoreboard> scoreboardConcurrentHashMap = new LinkedHashMap<>();

    public static void drawScoreBoardEntry(String objectiveName, String title, Player player, Consumer<Scoreboard> consumer) {
        Scoreboard scoreboard;
        if (scoreboardConcurrentHashMap.containsKey(player)) {
            scoreboard = scoreboardConcurrentHashMap.get(player);
        } else {
            scoreboard = new Scoreboard(objectiveName, title);
            scoreboardConcurrentHashMap.put(player, scoreboard);
        }
        consumer.accept(scoreboard);
        scoreboard.addViewer(player, DisplaySlot.SIDEBAR);
    }

    public static void drawScoreBoardEntry(String objectiveName, String title, Player player, String... strings) {
        Scoreboard scoreboard;
        if (scoreboardConcurrentHashMap.containsKey(player)) {
            scoreboard = scoreboardConcurrentHashMap.get(player);
        } else {
            scoreboard = new Scoreboard(objectiveName, title);
            scoreboardConcurrentHashMap.put(player, scoreboard);
        }
        List<IScoreboardLine> lines = new ArrayList<>();
        for (int lineId = 0; lineId < strings.length; lineId++) {
            lines.add(new ScoreboardLine(scoreboard, new FakeScorer(strings[lineId]), lineId));
        }
        scoreboard.setLines(lines);
        scoreboard.addViewer(player, DisplaySlot.SIDEBAR);
    }


    public static void removeScoreboard(Player player) {
        if (scoreboardConcurrentHashMap.containsKey(player)) {
            Scoreboard scoreboard = scoreboardConcurrentHashMap.get(player);
            scoreboard.removeViewer(player, DisplaySlot.SIDEBAR);
            scoreboardConcurrentHashMap.remove(player);
        }
    }
}
