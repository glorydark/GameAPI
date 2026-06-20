package gameapi.utils;

import cn.nukkit.block.Block;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Data
public class BlockWeightEntry {

    private String block;
    private int weight;

    public Block toBlock() {
        String[] parts = this.block.split(":");
        int id = Integer.parseInt(parts[0]);
        int meta = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return Block.get(id, meta);
    }

    public static List<BlockWeightEntry> fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<BlockWeightEntry>>() {}.getType());
    }

    public static List<BlockWeightEntry> fromJsonFile(File file) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, new TypeToken<List<BlockWeightEntry>>() {}.getType());
        }
    }

    public static List<BlockWeightEntry> fromJsonFile(String path) throws IOException {
        return fromJsonFile(new File(path));
    }
}
