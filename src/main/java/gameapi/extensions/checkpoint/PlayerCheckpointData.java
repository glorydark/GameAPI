package gameapi.extensions.checkpoint;

import gameapi.manager.room.CheckpointManager;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 * @date {2023/12/31} {18:14}
 */
@Data
public class PlayerCheckpointData {

    private Map<CheckpointData, Long> checkpointRecordMap;

    private int lap;

    private double globalMultiplier;

    private double score;

    private long lastReachMillis = -1L;

    public PlayerCheckpointData() {
        this(1.0d, 0d);
    }

    public PlayerCheckpointData(double globalMultiplier, double score) {
        this.checkpointRecordMap = new LinkedHashMap<>();
        this.lap = 0;
        this.globalMultiplier = globalMultiplier;
        this.score = score;
    }

    public boolean addCheckPointData(CheckpointManager manager, CheckpointData data) {
        if (this.checkpointRecordMap.containsKey(data)) {
            return false;
        }
        this.globalMultiplier *= data.getGlobalMultiplier();
        this.score += data.getScore();
        this.checkpointRecordMap.put(data, (System.currentTimeMillis() - (this.lastReachMillis == -1? manager.getRoom().getStartMillis(): this.lastReachMillis)));
        this.lastReachMillis = System.currentTimeMillis();
        return true;
    }

    public double getTotalScore() {
        return this.globalMultiplier * this.score;
    }

    public Map<CheckpointData, Long> getCheckpointRecordMap() {
        return checkpointRecordMap;
    }

    public void clearCheckpointRecordData() {
        this.checkpointRecordMap.clear();
    }

    public List<CheckpointData> getReachedCheckpoints() {
        return new ArrayList<>(this.checkpointRecordMap.keySet());
    }
}
