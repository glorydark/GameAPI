package gameapi.test;

import cn.nukkit.Server;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author glorydark
 */
public class Test {

    public static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    ;


    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        long startMillis = System.currentTimeMillis();

        AtomicInteger successCount = new AtomicInteger(0);
        Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.getInstance(), new Task() {

            int time = 0;

            @Override
            public void onRun(int i) {
                time++;
                if (time > 10) {
                    this.cancel();
                }
                double loadBefore = -1;
                for (File file : Objects.requireNonNull(new File("E:/test_samples/players/").listFiles())) {
                    Runtime runtime = Runtime.getRuntime();
                    executor.submit(() -> {
                        Config config = new Config(file, Config.YAML);
                        if (config.exists("level")) {
                            successCount.addAndGet(1);
                        }
                    });
                    double loadAfter = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0, 2);
                    if (loadBefore != -1 && loadAfter - loadBefore > 10) {
                        GameAPI.getInstance().getLogger().warning(loadBefore + "MB - " + loadAfter + "MB");
                    }
                    loadBefore = loadAfter;
                }

                System.out.println("finished");
            }
        }, 20);
        /*
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.println("Cost: " + (System.currentTimeMillis() - startMillis - 1000) + "ms");
                System.out.println("Processed " + count + " files");
                System.out.println("Verified: " + successCount.get());
                executor.shutdown();
            }
        } catch (Exception ignored) {

        }
         */
    }

    public static void start() {

    }

    public static String generateRandomExpression() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] operators = {'+', '-', '*', '/'};
        int num1 = random.nextInt(1, 101); // 生成1到100之间的随机整数
        int num2 = random.nextInt(1, 101); // 生成1到100之间的随机整数
        char operator = operators[random.nextInt(operators.length)];
        return num1 + " " + operator + " " + num2;
    }

    private static void printUsedMemory() {
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

        System.out.println("Heap Memory Usage:");
        System.out.println("Init: " + heapUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("Used: " + heapUsage.getUsed() / (1024 * 1024) + " MB");
        System.out.println("Committed: " + heapUsage.getCommitted() / (1024 * 1024) + " MB");
        System.out.println("Max: " + heapUsage.getMax() / (1024 * 1024) + " MB");

        System.out.println("Non-Heap Memory Usage:");
        System.out.println("Init: " + nonHeapUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("Used: " + nonHeapUsage.getUsed() / (1024 * 1024) + " MB");
        System.out.println("Committed: " + nonHeapUsage.getCommitted() / (1024 * 1024) + " MB");
        System.out.println("Max: " + nonHeapUsage.getMax() / (1024 * 1024) + " MB");
        System.out.println();
    }
}
