package gameapi.ranking;

import lombok.Data;

@Data
public class RankingFormat {

    private String scoreShowFormat;

    private String championPrefix;

    private String runnerUpPrefix;

    private String secondRunnerUpPrefix;

    public RankingFormat() {
        this.scoreShowFormat = "[%rank%] %player%: %score%";
        this.championPrefix = "§6";
        this.runnerUpPrefix = "§e";
        this.secondRunnerUpPrefix = "§a";
    }

    public RankingFormat(String scoreShowFormat, String champion_prefix, String runnerUpPrefix, String secondRunnerUpPrefix) {
        this.scoreShowFormat = scoreShowFormat;
        this.championPrefix = champion_prefix;
        this.runnerUpPrefix = runnerUpPrefix;
        this.secondRunnerUpPrefix = secondRunnerUpPrefix;
    }
}
