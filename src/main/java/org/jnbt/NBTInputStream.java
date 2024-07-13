package org.jnbt;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/* loaded from: jnbt-1.1.jar:org/jnbt/NBTInputStream.class */
public final class NBTInputStream implements Closeable {
    private final DataInputStream is;

    public NBTInputStream(InputStream is) throws IOException {
        this.is = new DataInputStream(new GZIPInputStream(is));
    }

    public Tag readTag() throws IOException {
        return readTag(0);
    }

    private Tag readTag(int depth) throws IOException {
        String name;
        int type = this.is.readByte() & 255;
        if (type != 0) {
            int nameLength = this.is.readShort() & 65535;
            byte[] nameBytes = new byte[nameLength];
            this.is.readFully(nameBytes);
            name = new String(nameBytes, NBTConstants.CHARSET);
        } else {
            name = "";
        }
        return readTagPayload(type, name, depth);
    }

    private Tag readTagPayload(int type, String name, int depth) throws IOException {
        switch (type) {
            case NBTConstants.TYPE_END /* 0 */:
                if (depth == 0) {
                    throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
                }
                return new EndTag();
            case NBTConstants.TYPE_BYTE /* 1 */:
                return new ByteTag(name, this.is.readByte());
            case NBTConstants.TYPE_SHORT /* 2 */:
                return new ShortTag(name, this.is.readShort());
            case NBTConstants.TYPE_INT /* 3 */:
                return new IntTag(name, this.is.readInt());
            case NBTConstants.TYPE_LONG /* 4 */:
                return new LongTag(name, this.is.readLong());
            case NBTConstants.TYPE_FLOAT /* 5 */:
                return new FloatTag(name, this.is.readFloat());
            case NBTConstants.TYPE_DOUBLE /* 6 */:
                return new DoubleTag(name, this.is.readDouble());
            case NBTConstants.TYPE_BYTE_ARRAY /* 7 */:
                int length = this.is.readInt();
                byte[] bytes = new byte[length];
                this.is.readFully(bytes);
                return new ByteArrayTag(name, bytes);
            case NBTConstants.TYPE_STRING /* 8 */:
                int length2 = this.is.readShort();
                byte[] bytes2 = new byte[length2];
                this.is.readFully(bytes2);
                return new StringTag(name, new String(bytes2, NBTConstants.CHARSET));
            case NBTConstants.TYPE_LIST /* 9 */:
                int childType = this.is.readByte();
                int length3 = this.is.readInt();
                List<Tag> tagList = new ArrayList<>();
                for (int i = 0; i < length3; i++) {
                    Tag tag = readTagPayload(childType, "", depth + 1);
                    if (tag instanceof EndTag) {
                        throw new IOException("TAG_End not permitted in a list.");
                    }
                    tagList.add(tag);
                }
                return new ListTag(name, NBTUtils.getTypeClass(childType), tagList);
            case NBTConstants.TYPE_COMPOUND /* 10 */:
                Map<String, Tag> tagMap = new HashMap<>();
                while (true) {
                    Tag tag2 = readTag(depth + 1);
                    if (!(tag2 instanceof EndTag)) {
                        tagMap.put(tag2.getName(), tag2);
                    } else {
                        return new CompoundTag(name, tagMap);
                    }
                }
            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.is.close();
    }
}
