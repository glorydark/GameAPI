package gameapi.extensions.checkPoint;

import lombok.Data;

import java.util.HashSet;

/**
 * @author glorydark
 * @date {2023/12/31} {18:14}
 */
@Data
public class PlayerCheckPointData {

    HashSet<CheckPointData> checkPointDataList;

    int lap;

    public PlayerCheckPointData() {
        this.checkPointDataList = new HashSet<>();
        this.lap = 0;
    }

    public boolean addCheckPointData(CheckPointData data) {
        return checkPointDataList.add(data);
    }
}
