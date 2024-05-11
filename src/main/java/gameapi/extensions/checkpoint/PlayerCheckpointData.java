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

    List<CheckpointData> checkpointDataList;

    int lap;

    double globalMultiplier = 1.0f;

    double score = 0;

    public PlayerCheckpointData() {
        this.checkpointDataList = new ArrayList<>();
        this.lap = 0;
    }

    public boolean addCheckPointData(CheckpointData data) {
        if (this.checkpointDataList.contains(data)) {
            return false;
        }
        this.globalMultiplier *= data.getGlobalMultiplier();
        this.score += data.getScore();
        return this.checkpointDataList.add(data);
    }

    public double getTotalScore() {
        return globalMultiplier * score;
    }
}
