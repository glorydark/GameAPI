package gameapi.task;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.google.common.collect.ImmutableList;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author glorydark
 */
@ToString
public class BlockFillTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 1000; // This is the threshold of executing task counts
    private final Block replacedBlock;
    private final Level level;
    private final Set<Vector3> posList;
    private long proceedBlockCount = 0L;
    private long endMillis = 0L;

    public BlockFillTask(Level level, Block replacedBlock) {
        this(level, replacedBlock, new HashSet<>());
    }

    public BlockFillTask(Level level, Block replacedBlock, Set<Vector3> posList) {
        this.level = level;
        this.replacedBlock = replacedBlock;
        this.posList = posList;
    }

    public static <T> List<Set<T>> splitSet(Set<T> originalSet) {
        List<T> list = new ArrayList<>(originalSet);
        int halfSize = list.size() / 2;

        Set<T> set1 = list.stream().limit(halfSize).collect(Collectors.toSet());
        Set<T> set2 = list.stream().skip(halfSize).collect(Collectors.toSet());

        return Arrays.asList(set1, set2);
    }

    public void addPos(Vector3 vector3) {
        this.posList.add(vector3);
    }

    public ImmutableList<Vector3> getImmutablePosList() {
        return new ImmutableList.Builder<Vector3>().addAll(this.posList).build();
    }

    @Override
    protected Long compute() {
        if (this.posList.size() <= THRESHOLD) {
            for (Vector3 pos : getImmutablePosList()) {
                this.level.setBlock(pos, this.replacedBlock);
                this.proceedBlockCount++;
            }
        } else {
            // 任务太大，需要分割
            List<BlockFillTask> fillTasks = new ArrayList<>();
            for (Set<Vector3> pos : splitSet(this.posList)) {
                BlockFillTask blockFillTask = new BlockFillTask(this.level, this.replacedBlock, pos);
                fillTasks.add(blockFillTask);
            }
            // 执行子任务
            for (BlockFillTask fillTask : fillTasks) {
                fillTask.fork();
            }
            // 等待子任务执行完毕
            for (BlockFillTask fillTask : fillTasks) {
                this.proceedBlockCount += fillTask.join();
            }
        }
        this.endMillis = System.currentTimeMillis();
        return this.proceedBlockCount;
    }

    public long getEndMillis() {
        return endMillis;
    }
}
