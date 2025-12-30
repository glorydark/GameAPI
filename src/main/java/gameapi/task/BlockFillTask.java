// 只优化最影响性能的部分，其他保持原样
package gameapi.task;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.google.common.collect.ImmutableList;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.RecursiveTask;

/**
 * @author glorydark
 */
@ToString
public class BlockFillTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 1000;
    private final Block replacedBlock;
    private final Level level;
    private final Set<Vector3> posList;
    private long proceedBlockCount = 0L;
    private final long startMillis;
    private long endMillis = 0L;

    private static final Map<Integer, List<Set<Vector3>>> splitCache =
            Collections.synchronizedMap(new WeakHashMap<>());

    public BlockFillTask(Level level, Block replacedBlock) {
        this(level, replacedBlock, new HashSet<>());
    }

    public BlockFillTask(Level level, Block replacedBlock, Set<Vector3> posList) {
        this.level = level;
        this.replacedBlock = replacedBlock;
        this.posList = posList;
        this.startMillis = System.currentTimeMillis();
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

    // 新增：批量添加方法
    public void addPositions(Collection<Vector3> positions) {
        this.posList.addAll(positions);
    }

    public ImmutableList<Vector3> getImmutablePosList() {
        return new ImmutableList.Builder<Vector3>().addAll(this.posList).build();
    }

    // 新增：获取统计信息的方法
    public long getStartMillis() {
        return startMillis;
    }

    public long getElapsedMillis() {
        return endMillis == 0L ? System.currentTimeMillis() - startMillis : endMillis - startMillis;
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
                this.level.setBlock(pos, this.replacedBlock, true, false);
                this.proceedBlockCount++;
            }
        } else {
            // 优化3：减少不必要的对象创建
            List<Set<Vector3>> splitSets = splitSet(this.posList);

            BlockFillTask task1 = new BlockFillTask(this.level, this.replacedBlock, splitSets.get(0));
            BlockFillTask task2 = new BlockFillTask(this.level, this.replacedBlock, splitSets.get(1));

            // 优化4：使用更高效的fork-join模式
            task1.fork();
            this.proceedBlockCount = task2.compute() + task1.join();
        }

        this.endMillis = System.currentTimeMillis();
        return this.proceedBlockCount;
    }

    public long getEndMillis() {
        return endMillis;
    }
}