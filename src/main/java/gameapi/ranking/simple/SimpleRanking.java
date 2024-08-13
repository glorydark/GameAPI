package gameapi.ranking.simple;

import cn.nukkit.level.Location;
import gameapi.manager.data.PlayerGameDataManager;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleRanking extends Ranking {

    // This only stores the inner compared values
    private final String gameName;

    private final String dataName;

    public SimpleRanking(Location location, RankingValueType valueType, String gameName, String dataName, String title, String noDataContent, RankingFormat rankingFormat, RankingSortSequence rankingSortSequence) {
        super(location, valueType, title, noDataContent, rankingFormat, rankingSortSequence);
        this.rankingData = new LinkedHashMap<>();
        this.gameName = gameName;
        this.dataName = dataName;
    }

    @Override
    public Map<String, Object> getLatestRankingData() {
        return PlayerGameDataManager.getPlayerAllGameData(this.gameName, this.dataName);
    }
}
