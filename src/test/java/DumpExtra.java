import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import java.io.File;

public class DumpExtra {
    public static void main(String[] args) throws Exception {
        File testDir = new File("src/main/resources/test");
        for (File folder : testDir.listFiles(File::isDirectory)) {
            File extra = new File(folder, "extra.nbt");
            if (!extra.exists()) continue;
            System.out.println("=== " + folder.getName() + " ===");
            CompoundTag tag = NBTIO.read(extra);
            System.out.println(tag.toSNBT());
            File buildJson = new File(folder, "build.json");
            if (buildJson.exists()) {
                System.out.println("  build.json keys: minPos, maxPos, relativeMin, relativeMax");
            }
        }
    }
}
