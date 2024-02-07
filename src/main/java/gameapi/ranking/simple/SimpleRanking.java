package gameapi.ranking.simple;

import cn.nukkit.level.Location;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.manager.data.PlayerGameDataManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleRanking extends Ranking {

    // This only stores the inner compared values
    private final String gameName;

    private final String comparedKey;

    public SimpleRanking(Location location, String type, String title, String noDataContent, RankingFormat rankingFormat, RankingSortSequence rankingSortSequence, String gameName, String comparedKey) {
        super(location, type, title, noDataContent, rankingFormat, rankingSortSequence);
        this.rankingData = new LinkedHashMap<>();
        this.gameName = gameName;
        this.comparedKey = comparedKey;
    }

    public Map<String, Object> getLatestRankingData() {
        return new HashMap<>(PlayerGameDataManager.getPlayerAllGameData(this.gameName, this.comparedKey));
    }
}
