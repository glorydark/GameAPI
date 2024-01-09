package gameapi.extensions.checkPoint;

import lombok.Data;

import java.util.HashSet;

/**
 * @author glorydark
 * @date {2023/12/31} {18:14}
 */
@Data
public class PlayerCheckpointData {

    HashSet<CheckpointData> checkpointDataList;

    int lap;

    public PlayerCheckpointData() {
        this.checkpointDataList = new HashSet<>();
        this.lap = 0;
    }

    public boolean addCheckPointData(CheckpointData data) {
        return checkpointDataList.add(data);
    }
}
