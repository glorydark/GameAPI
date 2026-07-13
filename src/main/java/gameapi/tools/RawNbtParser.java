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
        if (data.length < 2) return List.of();
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
                    if (offset >= data.length) break;
                    try {
                        entries.add(parseBlockCompound());
                    } catch (IndexOutOfBoundsException e) {
                        return entries;
                    }
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
            int tagStart = offset;
            byte tagType = readByte();
            String name = readString();
            switch (name) {
                case "x" -> x = readInt();
                case "y" -> y = readInt();
                case "z" -> z = readInt();
                case "blockId" -> blockId = readInt();
                case "damage" -> damage = readInt();
                case "blockEntityData" -> beData = readRawBytes(tagType, tagStart);
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

    private byte[] readRawBytes(int tagType, int tagStart) {
        skipPayload(tagType);
        return Arrays.copyOfRange(data, tagStart, offset);
    }

    private void skipPayload(int tagType) {
        switch (tagType) {
            case 1 -> offset += 1;
            case 2 -> offset += 2;
            case 3 -> offset += 4;
            case 4 -> offset += 8;
            case 5 -> offset += 4;
            case 6 -> offset += 8;
            case 7 -> {
                int len = readInt();
                if (len < 0) len = 0;
                offset += len;
            }
            case 8 -> {
                int v = readUnsignedShort();
                offset += v;
            }
            case 9 -> {
                int eType = readByte() & 0xFF;
                int len = readInt();
                if (len < 0) len = 0;
                for (int i = 0; i < len && offset < data.length; i++) {
                    skipPayload(eType);
                }
            }
            case 10 -> {
                int guard = 0;
                while (offset < data.length && data[offset] != 0 && guard < 100000) {
                    byte innerType = data[offset++];
                    skipNamedEntry(innerType);
                    guard++;
                }
                if (offset < data.length) offset++;
            }
            case 11 -> {
                int len = readInt();
                if (len < 0) len = 0;
                offset += len * 4;
            }
            case 12 -> {
                int len = readInt();
                if (len < 0) len = 0;
                offset += len * 8;
            }
            default -> {
            }
        }
    }

    private void skipNamedEntry(int tagType) {
        if (tagType == 0) return;
        skipString();
        skipPayload(tagType);
    }

    private byte readByte() {
        if (offset >= data.length) throw new IndexOutOfBoundsException("Unexpected end of data at offset " + offset);
        return data[offset++];
    }

    private int readUnsignedShort() {
        if (offset + 1 >= data.length) throw new IndexOutOfBoundsException("Unexpected end of data at offset " + offset);
        int r = (data[offset] & 0xFF) << 8 | (data[offset + 1] & 0xFF);
        offset += 2;
        return r;
    }

    private int readInt() {
        if (offset + 3 >= data.length) throw new IndexOutOfBoundsException("Unexpected end of data at offset " + offset);
        int r = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
              | (data[offset + 2] & 0xFF) << 8  | (data[offset + 3] & 0xFF);
        offset += 4;
        return r;
    }

    private String readString() {
        int len = readUnsignedShort();
        if (offset + len > data.length) throw new IndexOutOfBoundsException("String length " + len + " exceeds data at offset " + offset);
        String r = new String(data, offset, len, StandardCharsets.UTF_8);
        offset += len;
        return r;
    }

    private void skipString() {
        int len = readUnsignedShort();
        if (offset + len > data.length) throw new IndexOutOfBoundsException("String length " + len + " exceeds data at offset " + offset);
        offset += len;
    }
}
