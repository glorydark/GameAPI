package gameapi.tools;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawNbtParser {

    public record BlockEntry(int x, int y, int z, int blockId, int damage,
                             byte[] blockEntityData,
                             int layer1Id, int layer1Damage) {}

    private final byte[] data;
    private int offset;

    public RawNbtParser(byte[] data) {
        this.data = data;
        this.offset = 0;
    }

    public List<BlockEntry> parseBlocks() {
        byte type = readByte();
        if (type != 10) throw new IllegalArgumentException("Root must be TAG_Compound");
        skipString();

        List<BlockEntry> entries = new ArrayList<>();
        while (offset < data.length) {
            if (data[offset] == 0) break;
            byte tagType = readByte();
            String name = readString();
            if (tagType == 9 && name.equals("blocks")) {
                byte elementType = readByte();
                int length = readInt();
                for (int i = 0; i < length; i++) {
                    entries.add(parseBlockCompound());
                }
            } else {
                skipPayload(tagType);
            }
        }
        return entries;
    }

    private BlockEntry parseBlockCompound() {
        int x = 0, y = 0, z = 0, blockId = 0, damage = 0;
        byte[] beData = null;
        int l1Id = 0, l1Dam = 0;

        while (offset < data.length) {
            if (data[offset] == 0) { offset++; break; }
            byte tagType = readByte();
            String name = readString();
            switch (name) {
                case "x" -> x = readInt();
                case "y" -> y = readInt();
                case "z" -> z = readInt();
                case "blockId" -> blockId = readInt();
                case "damage" -> damage = readInt();
                case "blockEntityData" -> beData = readRawBytes();
                case "layer1" -> {
                    int[] l1 = parseLayer1Compound();
                    l1Id = l1[0];
                    l1Dam = l1[1];
                }
                default -> skipPayload(tagType);
            }
        }
        return new BlockEntry(x, y, z, blockId, damage, beData, l1Id, l1Dam);
    }

    private int[] parseLayer1Compound() {
        int blockId = 0, damage = 0;
        while (offset < data.length && data[offset] != 0) {
            byte tagType = readByte();
            String name = readString();
            switch (name) {
                case "blockId" -> blockId = readInt();
                case "damage" -> damage = readInt();
                default -> skipPayload(tagType);
            }
        }
        if (offset < data.length && data[offset] == 0) offset++;
        return new int[]{blockId, damage};
    }

    private byte[] readRawBytes() {
        int start = offset;
        skipPayload((byte) 10);
        return Arrays.copyOfRange(data, start, offset);
    }

    private void skipPayload(byte tagType) {
        switch (tagType) {
            case 1 -> offset += 1;
            case 2 -> offset += 2;
            case 3 -> offset += 4;
            case 4 -> offset += 8;
            case 5 -> offset += 4;
            case 6 -> offset += 8;
            case 7 -> { int len = readInt(); offset += len; }
            case 8 -> offset += readUnsignedShort();
            case 9 -> {
                byte eType = readByte();
                int len = readInt();
                for (int i = 0; i < len; i++) skipPayload(eType);
            }
            case 10 -> {
                while (offset < data.length && data[offset] != 0) {
                    byte innerType = data[offset++];
                    skipNamedEntry(innerType);
                }
                if (offset < data.length) offset++;
            }
            case 11 -> { int len = readInt(); offset += len * 4; }
            case 12 -> { int len = readInt(); offset += len * 8; }
        }
    }

    private void skipNamedEntry(byte tagType) {
        if (tagType == 0) return;
        skipString();
        skipPayload(tagType);
    }

    private byte readByte() { return data[offset++]; }

    private int readUnsignedShort() {
        int r = (data[offset] & 0xFF) << 8 | (data[offset + 1] & 0xFF);
        offset += 2;
        return r;
    }

    private int readInt() {
        int r = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
              | (data[offset + 2] & 0xFF) << 8  | (data[offset + 3] & 0xFF);
        offset += 4;
        return r;
    }

    private String readString() {
        int len = readUnsignedShort();
        String r = new String(data, offset, len, StandardCharsets.UTF_8);
        offset += len;
        return r;
    }

    private void skipString() {
        int len = readUnsignedShort();
        offset += len;
    }
}
