package gameapi.tools;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import java.io.File;
import java.nio.file.Files;

public class DebugRawParser {
    public static void main(String[] args) throws Exception {
        File folder = new File("src/main/resources/test/large_build");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".nbt"));
        if (files == null || files.length == 0) { System.out.println("No files"); return; }

        File f = files[0];
        System.out.println("File: " + f.getName() + " (" + f.length() + " bytes)");
        byte[] data = Files.readAllBytes(f.toPath());

        CompoundTag tag = NBTIO.read(data);
        int nbtCount = tag.getList("blocks", CompoundTag.class).getAll().size();
        System.out.println("NBTIO blocks count: " + nbtCount);

        RawNbtParser parser = new RawNbtParser(data);
        java.util.List<RawNbtParser.BlockEntry> rawBlocks = parser.parseBlocks();
        System.out.println("Raw parser blocks: " + rawBlocks.size());
        if (rawBlocks.size() > 0) {
            RawNbtParser.BlockEntry e = rawBlocks.get(0);
            System.out.println("  First: x=" + e.x() + " y=" + e.y() + " z=" + e.z() +
                " blockId=" + e.blockId() + " damage=" + e.damage()
                + " l1Id=" + e.layer1Id() + " l1Dam=" + e.layer1Damage());
        }
    }
}
