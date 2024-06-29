package gameapi.extensions.checkpoint;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 * @date {2023/12/31} {18:14}
 */
@Data
public class PlayerCheckpointData {

    private List<CheckpointData> checkpointDataList;

    private int lap;

    private double globalMultiplier;

    private double score;

    public PlayerCheckpointData() {
        this(1.0d, 0d);
        this.checkpointDataList = new ArrayList<>();
        this.lap = 0;
    }

    public PlayerCheckpointData(double globalMultiplier, double score) {
        this.checkpointDataList = new ArrayList<>();
        this.lap = 0;
        this.globalMultiplier = globalMultiplier;
        this.score = score;
    }

    public boolean addCheckPointData(CheckpointData data) {
        if (this.checkpointDataList.contains(data)) {
            return false;
        }
        this.globalMultiplier *= data.getGlobalMultiplier();
        this.score += data.getScore();
        this.checkpointDataList.add(data);
        return true;
    }

    public double getTotalScore() {
        return this.globalMultiplier * this.score;
    }
}
