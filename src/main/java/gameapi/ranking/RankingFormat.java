package gameapi.ranking;

import lombok.Data;

@Data
public class RankingFormat {

    String scoreShowFormat = "[%rank%] %player%: %score%";

    String championPrefix = "§6";

    String runnerUpPrefix = "§e";

    String secondRunnerUpPrefix = "§a";

    public RankingFormat() {

    }

    public RankingFormat(String scoreShowFormat, String champion_prefix, String runnerUpPrefix, String secondRunnerUpPrefix) {
        this.scoreShowFormat = scoreShowFormat;
        this.championPrefix = champion_prefix;
        this.runnerUpPrefix = runnerUpPrefix;
        this.secondRunnerUpPrefix = secondRunnerUpPrefix;
    }
}
