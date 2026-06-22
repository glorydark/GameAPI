package gameapi.tools;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class BuildLoadBenchmark {

    static ForkJoinPool pool = new ForkJoinPool(
            Math.min(4, Runtime.getRuntime().availableProcessors()));

    public static void main(String[] args) throws Exception {
        String[] roots = {
            "src/main/resources/test/large_build",
            "src/main/resources/test/very_large_build"
        };
        for (String base : roots) {
            runBenchmarkSet(base);
        }
        pool.shutdown();
    }

    static void runBenchmarkSet(String basePath) throws Exception {
        String base = new File(basePath).getAbsolutePath();
        File folder = new File(base);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".nbt") && !name.equals("extra.nbt"));
        if (files == null || files.length == 0) {
            System.out.println("No NBT files found in " + base);
            return;
        }
        Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
        System.out.println("\n============================================");
        System.out.println("  Dataset: " + basePath);
        System.out.println("  Files: " + files.length + ", Total size: " + (totalSize(files) / 1024) + " KB");
        System.out.println("============================================\n");

        // Warmup with fastest method only (skip slow NBTIO.read(File))
        // Keep warmup count low since very_large is big
        for (int i = 0; i < 2; i++) {
            rawParse(files);
        }
        System.out.println("--- Warmup done ---\n");

        // Benchmark methods (skip slow NBTIO.read(File) on very_large)
        String tag = basePath.contains("very") ? " (SKIPPED on very_large)" : "";
        timed("1. Sequential NBTIO.read(File)" + tag, () -> {
            if (tag.isEmpty()) baseline(files);
        });
        timed("2. Parallel CompletableFuture" + tag, () -> {
            if (tag.isEmpty()) parallelFuture(files);
        });
        long t3 = timed("3. Memory-mapped NBTIO.read(byte[])", () -> memmap(files));
        long t4 = timed("4. Parallel + memory-mapped", () -> parallelMemmap(files));
        long t5 = timed("5. Raw byte-scanning parser", () -> rawParse(files));
        long t6 = timed("6. Parallel raw parser", () -> parallelRawParse(files));

        // CBD: read raw, encode to CBD, discard
        long t7 = timed("7. CBD encode (raw->binary)", () -> cbdWrite(files));

        // CBD: read raw, encode to CBD, decode back, verify
        long t8 = timed("8. CBD decode (binary->blocks)", () -> cbdRead(files));

        // Verify correctness
        verifyCorrectnessNBTIO(files);

        // File size comparison
        long nbtSize = totalSize(files);
        long cbdSize = cbdTotalSize(files);

        String name = basePath.substring(basePath.lastIndexOf('/') + 1);
        System.out.println("\n=== Summary: " + name + " ===");
        if (tag.isEmpty()) { System.out.printf("  1. Sequential NBTIO.read(File)     :    SKIP ms\n"); }
        if (tag.isEmpty()) { System.out.printf("  2. Parallel NBTIO.read(File)       :    SKIP ms\n"); }
        System.out.printf("  3. NBTIO.read(byte[])             : %6d ms\n", t3);
        System.out.printf("  4. Parallel NBTIO.read(byte[])    : %6d ms\n", t4);
        System.out.printf("  5. Raw byte-scanning              : %6d ms\n", t5);
        System.out.printf("  6. Parallel raw                   : %6d ms\n", t6);
        System.out.printf("  7. CBD encode                     : %6d ms\n", t7);
        System.out.printf("  8. CBD decode                     : %6d ms\n", t8);
        System.out.printf("  NBT size: %.1f MB | CBD size: %.1f MB | Ratio: %.1f:1\n",
            nbtSize / 1_048_576.0, cbdSize / 1_048_576.0, (double)nbtSize / Math.max(1, cbdSize));
    }

    static long totalSize(File[] files) {
        long sum = 0;
        for (File f : files) sum += f.length();
        return sum;
    }

    static long timed(String label, Runnable r) {
        System.gc();
        long start = System.currentTimeMillis();
        r.run();
        long end = System.currentTimeMillis();
        long ms = end - start;
        System.out.println("  [" + label + "] " + ms + " ms\n");
        return ms;
    }

    // 1. Baseline: sequential NBTIO.read(File)
    static void baseline(File[] files) {
        for (File file : files) {
            try {
                CompoundTag tag = NBTIO.read(file);
                process(tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 2. Parallel CompletableFuture
    static void parallelFuture(File[] files) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    CompoundTag tag = NBTIO.read(file);
                    process(tag);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, pool));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    // 3. Memory-mapped read
    static void memmap(File[] files) {
        for (File file : files) {
            try {
                byte[] data = readMmap(file);
                CompoundTag tag = NBTIO.read(data);
                process(tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Parallel memory-mapped
    static void parallelMemmap(File[] files) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    byte[] data = readMmap(file);
                    CompoundTag tag = NBTIO.read(data);
                    process(tag);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, pool));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    // 5. Raw byte-scanning parser (skip NBTIO tree building)
    static void rawParse(File[] files) {
        for (File file : files) {
            try {
                byte[] data = readAll(file);
                RawNbtParser parser = new RawNbtParser(data);
                List<RawNbtParser.BlockEntry> entries = parser.parseBlocks();
                int count = 0;
                for (RawNbtParser.BlockEntry e : entries) {
                    if (e.blockId() != 0) count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 6. Parallel raw parser
    static void parallelRawParse(File[] files) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    byte[] data = readAll(file);
                    RawNbtParser parser = new RawNbtParser(data);
                    List<RawNbtParser.BlockEntry> entries = parser.parseBlocks();
                    int count = 0;
                    for (RawNbtParser.BlockEntry e : entries) {
                        if (e.blockId() != 0) count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, pool));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    // 7. CBD encode: raw parse -> compact binary
    static void cbdWrite(File[] files) {
        for (File file : files) {
            try {
                byte[] data = readAll(file);
                RawNbtParser parser = new RawNbtParser(data);
                List<RawNbtParser.BlockEntry> entries = parser.parseBlocks();
                int maxX = 0, maxY = 0, maxZ = 0;
                for (RawNbtParser.BlockEntry e : entries) {
                    if (e.x() > maxX) maxX = e.x();
                    if (e.y() > maxY) maxY = e.y();
                    if (e.z() > maxZ) maxZ = e.z();
                }
                byte[] cbd = CompactBuildFormat.encode(entries, maxX, maxY, maxZ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 8. CBD decode: raw parse -> encode -> decode -> verify
    static void cbdRead(File[] files) {
        for (File file : files) {
            try {
                byte[] data = readAll(file);
                RawNbtParser parser = new RawNbtParser(data);
                List<RawNbtParser.BlockEntry> entries = parser.parseBlocks();
                int maxX = 0, maxY = 0, maxZ = 0;
                for (RawNbtParser.BlockEntry e : entries) {
                    if (e.x() > maxX) maxX = e.x();
                    if (e.y() > maxY) maxY = e.y();
                    if (e.z() > maxZ) maxZ = e.z();
                }
                byte[] cbd = CompactBuildFormat.encode(entries, maxX, maxY, maxZ);
                CompactBuildFormat.DecodeResult result = CompactBuildFormat.decode(cbd);
                if (result.blocks().size() != entries.size()) {
                    System.out.println("  CBD MISMATCH: " + file.getName() + " " + entries.size() + " vs " + result.blocks().size());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static long cbdTotalSize(File[] files) {
        long total = 0;
        for (File file : files) {
            try {
                byte[] data = readAll(file);
                RawNbtParser parser = new RawNbtParser(data);
                List<RawNbtParser.BlockEntry> entries = parser.parseBlocks();
                int maxX = 0, maxY = 0, maxZ = 0;
                for (RawNbtParser.BlockEntry e : entries) {
                    if (e.x() > maxX) maxX = e.x();
                    if (e.y() > maxY) maxY = e.y();
                    if (e.z() > maxZ) maxZ = e.z();
                }
                total += CompactBuildFormat.encode(entries, maxX, maxY, maxZ).length;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    static void verifyCorrectnessNBTIO(File[] files) throws Exception {
        System.out.println("\n--- Correctness check ---");
        for (File file : files) {
            byte[] data = readAll(file);
            CompoundTag tag = NBTIO.read(data);
            List<CompoundTag> nbtBlocks = tag.getList("blocks", CompoundTag.class).getAll();

            RawNbtParser parser = new RawNbtParser(data);
            List<RawNbtParser.BlockEntry> rawBlocks = parser.parseBlocks();

            if (nbtBlocks.size() != rawBlocks.size()) {
                System.out.println("  MISMATCH: " + file.getName() + " size " + nbtBlocks.size() + " vs " + rawBlocks.size());
            } else {
                System.out.println("  OK: " + file.getName() + " (" + nbtBlocks.size() + " entries)");
            }
        }
    }

    static byte[] readAll(File file) throws IOException {
        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    static byte[] readMmap(File file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {
            MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            byte[] data = new byte[(int) channel.size()];
            buf.get(data);
            return data;
        }
    }

    static int process(CompoundTag tag) {
        List<CompoundTag> blocks = tag.getList("blocks", CompoundTag.class).getAll();
        int count = 0;
        for (CompoundTag block : blocks) {
            if (block.getInt("blockId") != 0) count++;
        }
        return count;
    }
}
