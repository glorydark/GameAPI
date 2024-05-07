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

    public PlayerCheckpointData() {
        this.checkpointDataList = new ArrayList<>();
        this.lap = 0;
    }

    public boolean addCheckPointData(CheckpointData data) {
        return checkpointDataList.add(data);
    }
}
