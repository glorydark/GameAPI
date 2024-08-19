package gameapi.task;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author glorydark
 */
public class BlockReplaceTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 1000; // This is the threshold of executing task counts
    private final Block sourceBlock;
    private final Block targetBlock;
    private final Level level;
    private final Set<Vector3> posList;
    private final boolean checkDamage;
    private long proceedBlockCount = 0L;
    private long endMillis = 0L;

    public BlockReplaceTask(Level level, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        this(level, sourceBlock, targetBlock, new HashSet<>(), checkDamage);
    }

    public BlockReplaceTask(Level level, Block sourceBlock, Block targetBlock, Set<Vector3> posList, boolean checkDamage) {
        this.level = level;
        this.sourceBlock = sourceBlock;
        this.targetBlock = targetBlock;
        this.posList = posList;
        this.checkDamage = checkDamage;
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
                Block blockAtPos = this.level.getBlock(pos);
                if (blockAtPos.getId() != this.sourceBlock.getId()) {
                    continue;
                }
                if (this.checkDamage && blockAtPos.getDamage() != this.sourceBlock.getDamage()) {
                    continue;
                }
                this.level.setBlock(pos, this.targetBlock);
                this.proceedBlockCount++;
            }
        } else {
            // 任务太大，需要分割
            List<BlockReplaceTask> fillTasks = new ArrayList<>();
            for (Set<Vector3> pos : splitSet(this.posList)) {
                BlockReplaceTask replaceTask = new BlockReplaceTask(this.level, this.sourceBlock, this.targetBlock, pos, checkDamage);
                fillTasks.add(replaceTask);
            }
            // 执行子任务
            for (BlockReplaceTask fillTask : fillTasks) {
                fillTask.fork();
            }
            // 等待子任务执行完毕
            for (BlockReplaceTask fillTask : fillTasks) {
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
