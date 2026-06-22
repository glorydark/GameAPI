package gameapi.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CompactBuildFormat {

    public record PaletteEntry(int blockId, int damage, int layer1Id, int layer1Damage) {}

    public record DecodeResult(List<RawNbtParser.BlockEntry> blocks,
                                int maxX, int maxY, int maxZ) {}

    public static byte[] encode(List<RawNbtParser.BlockEntry> blocks,
                                  int maxX, int maxY, int maxZ) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(blocks.size() * 8);
            DataOutputStream dos = new DataOutputStream(baos);

            // Build palette
            Map<PaletteEntry, Integer> paletteMap = new LinkedHashMap<>();
            for (RawNbtParser.BlockEntry b : blocks) {
                PaletteEntry pe = new PaletteEntry(b.blockId(), b.damage(),
                    b.layer1Id(), b.layer1Damage());
                paletteMap.putIfAbsent(pe, paletteMap.size());
            }
            List<PaletteEntry> palette = new ArrayList<>(paletteMap.keySet());

            // Separate blocks with block entities
            List<RawNbtParser.BlockEntry> beBlocks = new ArrayList<>();
            for (RawNbtParser.BlockEntry b : blocks) {
                if (b.blockEntityData() != null) beBlocks.add(b);
            }

            // Header
            dos.write("GCBD".getBytes(StandardCharsets.US_ASCII));
            dos.writeByte(1);
            writeVarint(dos, maxX);
            writeVarint(dos, maxY);
            writeVarint(dos, maxZ);
            writeVarint(dos, palette.size());
            writeVarint(dos, blocks.size());
            writeVarint(dos, beBlocks.size());

            // Palette
            for (PaletteEntry pe : palette) {
                dos.writeShort(pe.blockId());
                dos.writeShort(pe.damage());
                dos.writeShort(pe.layer1Id());
                dos.writeShort(pe.layer1Damage());
            }

            // Block data
            for (RawNbtParser.BlockEntry b : blocks) {
                int pIdx = paletteMap.get(new PaletteEntry(
                    b.blockId(), b.damage(), b.layer1Id(), b.layer1Damage()));
                writeVarint(dos, pIdx);
                writeVarint(dos, b.x());
                writeVarint(dos, b.y());
                writeVarint(dos, b.z());
            }

            // Block entity data
            for (RawNbtParser.BlockEntry b : beBlocks) {
                writeVarint(dos, b.x());
                writeVarint(dos, b.y());
                writeVarint(dos, b.z());
                writeVarint(dos, b.blockEntityData().length);
                dos.write(b.blockEntityData());
            }

            dos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static DecodeResult decode(byte[] data) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

            byte[] magic = new byte[4];
            dis.readFully(magic);
            if (!"GCBD".equals(new String(magic, StandardCharsets.US_ASCII)))
                throw new IllegalArgumentException("Bad magic");
            int version = dis.readByte() & 0xFF;
            if (version != 1) throw new IllegalArgumentException("Unknown version: " + version);

            int maxX = readVarint(dis);
            int maxY = readVarint(dis);
            int maxZ = readVarint(dis);
            int paletteSize = readVarint(dis);
            int blockCount = readVarint(dis);
            int beCount = readVarint(dis);

            // Palette
            List<PaletteEntry> palette = new ArrayList<>(paletteSize);
            for (int i = 0; i < paletteSize; i++) {
                palette.add(new PaletteEntry(
                    dis.readShort() & 0xFFFF, dis.readShort() & 0xFFFF,
                    dis.readShort() & 0xFFFF, dis.readShort() & 0xFFFF));
            }

            // Blocks
            List<RawNbtParser.BlockEntry> blocks = new ArrayList<>(blockCount);
            for (int i = 0; i < blockCount; i++) {
                int pIdx = readVarint(dis);
                int x = readVarint(dis);
                int y = readVarint(dis);
                int z = readVarint(dis);
                PaletteEntry pe = palette.get(pIdx);
                blocks.add(new RawNbtParser.BlockEntry(x, y, z,
                    pe.blockId(), pe.damage(), null, pe.layer1Id(), pe.layer1Damage()));
            }

            // Block entities
            Map<String, byte[]> beMap = new HashMap<>();
            for (int i = 0; i < beCount; i++) {
                int x = readVarint(dis);
                int y = readVarint(dis);
                int z = readVarint(dis);
                int nbtLen = readVarint(dis);
                byte[] nbtData = new byte[nbtLen];
                dis.readFully(nbtData);
                beMap.put(x + "," + y + "," + z, nbtData);
            }

            for (int i = 0; i < blocks.size(); i++) {
                RawNbtParser.BlockEntry b = blocks.get(i);
                byte[] beData = beMap.get(b.x() + "," + b.y() + "," + b.z());
                if (beData != null) {
                    blocks.set(i, new RawNbtParser.BlockEntry(
                        b.x(), b.y(), b.z(), b.blockId(), b.damage(), beData,
                        b.layer1Id(), b.layer1Damage()));
                }
            }

            return new DecodeResult(blocks, maxX, maxY, maxZ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeVarint(DataOutputStream dos, int value) throws IOException {
        while (value > 127) {
            dos.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        dos.writeByte(value & 0x7F);
    }

    private static int readVarint(DataInputStream dis) throws IOException {
        int result = 0, shift = 0;
        while (true) {
            int b = dis.readByte() & 0xFF;
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) return result;
            shift += 7;
        }
    }
}
