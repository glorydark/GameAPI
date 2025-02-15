package gameapi.utils.music;

import gameapi.GameAPI;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/*
    this class is excerpted from MusicPlus,
    which is authored by Nissining.
*/
public class NBSDecoder {

    public static Song parse(String identifier, File decodeFile) {
        try {
            return parse(identifier, new FileInputStream(decodeFile));
        } catch (FileNotFoundException e) {
            GameAPI.getGameDebugManager().printError(e);
        }
        return null;
    }

    public static Song parse(String name, InputStream inputStream) {
        Map<Integer, Layer> layerHashMap = new HashMap<>();
        DataInputStream dis = new DataInputStream(inputStream);
        try {
            short length = readShort(dis);
            short songHeight = readShort(dis);
            String title = readString(dis);
            String author = readString(dis);
            readString(dis);
            String description = readString(dis);
            float speed = readShort(dis) / 100f;
            dis.readBoolean();
            dis.readByte();
            dis.readByte();
            readInt(dis);
            readInt(dis);
            readInt(dis);
            readInt(dis);
            readInt(dis);
            readString(dis);
            short tick = -1;
            while (true) {
                short jumpTicks = readShort(dis);
                if (jumpTicks == 0) {
                    break;
                }
                tick += jumpTicks;
                short layer = -1;
                while (true) {
                    short jumpLayers = readShort(dis);
                    if (jumpLayers == 0) {
                        break;
                    }
                    layer += jumpLayers;
                    setNote(layer, tick, dis.readByte(), dis.readByte(), layerHashMap);
                }
            }
            for (int i = 0; i < songHeight; i++) {
                Layer l = layerHashMap.get(i);
                if (l != null) {
                    l.setName(readString(dis)).setVolume(dis.readByte());
                }
            }
            dis.close();
            inputStream.close();
            return new Song(speed, layerHashMap, songHeight, length, title, author, description, name);
        } catch (IOException e) {
            try {
                dis.close();
                inputStream.close();
            } catch (IOException ignored) {

            }
            return null;
        }
    }

    private static void setNote(int layer, int ticks, byte instrument, byte key, Map<Integer, Layer> layerHashMap) {
        Layer l = layerHashMap.getOrDefault(layer, new Layer());
        layerHashMap.put(layer, l);
        l.setNote(ticks, new Note(instrument, key));
    }

    private static short readShort(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        int byte3 = dis.readUnsignedByte();
        int byte4 = dis.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    private static String readString(DataInputStream dis) throws IOException {
        int length = readInt(dis);
        StringBuilder sb = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) dis.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            sb.append(c);
        }
        return sb.toString();
    }

}