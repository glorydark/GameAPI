package gameapi.task;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.RecursiveTask;

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
    @Getter
    private long endMillis = 0L;
    private static final Map<Integer, List<Set<Vector3>>> splitCache =
            Collections.synchronizedMap(new WeakHashMap<>());

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
        int size = originalSet.size();
        int hash = originalSet.hashCode() * 31 + size;

        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<Set<T>> cached = (List<Set<T>>) (Object) splitCache.get(hash);
        if (cached != null) {
            return cached;
        }

        if (originalSet.isEmpty()) {
            List<Set<T>> result = Arrays.asList(new HashSet<>(), new HashSet<>());
            splitCache.put(hash, (List<Set<Vector3>>) (Object) result);
            return result;
        }

        List<T> list = new ArrayList<>(originalSet);
        int halfSize = list.size() / 2;

        Set<T> set1 = new LinkedHashSet<>(list.subList(0, halfSize));
        Set<T> set2 = new LinkedHashSet<>(list.subList(halfSize, list.size()));

        List<Set<T>> result = Arrays.asList(set1, set2);
        splitCache.put(hash, (List<Set<Vector3>>) (Object) result);
        return result;
    }

    public void addPos(Vector3 vector3) {
        this.posList.add(vector3);
    }

    public ImmutableList<Vector3> getImmutablePosList() {
        return new ImmutableList.Builder<Vector3>().addAll(this.posList).build();
    }

    @Override
    protected Long compute() {
        if (this.posList.isEmpty()) {
            this.endMillis = System.currentTimeMillis();
            return 0L;
        }

        if (this.posList.size() <= THRESHOLD) {
            // 优化2：直接使用Set的迭代器，避免创建新集合
            for (Vector3 pos : this.posList) {
                Block blockAtPos = this.level.getBlock(pos);
                if (blockAtPos.getId() != this.sourceBlock.getId()) {
                    continue;
                }
                if (this.checkDamage && blockAtPos.getDamage() != this.sourceBlock.getDamage()) {
                    continue;
                }
                this.level.setBlock(pos, this.targetBlock, true, false);
                this.proceedBlockCount++;
            }
        } else {
            // 优化3：减少不必要的对象创建
            List<Set<Vector3>> splitSets = splitSet(this.posList);

            BlockReplaceTask task1 = new BlockReplaceTask(this.level, this.sourceBlock, this.targetBlock, splitSets.get(0), checkDamage);
            BlockReplaceTask task2 = new BlockReplaceTask(this.level, this.sourceBlock, this.targetBlock, splitSets.get(1), checkDamage);

            // 优化4：使用更高效的fork-join模式
            task1.fork();
            this.proceedBlockCount = task2.compute() + task1.join();
        }

        this.endMillis = System.currentTimeMillis();
        return this.proceedBlockCount;
    }
    }
