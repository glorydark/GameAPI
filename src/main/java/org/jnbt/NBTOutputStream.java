package org.jnbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/* loaded from: jnbt-1.1.jar:org/jnbt/NBTOutputStream.class */
public final class NBTOutputStream implements Closeable {
    private final DataOutputStream os;

    public NBTOutputStream(OutputStream os) throws IOException {
        this.os = new DataOutputStream(new GZIPOutputStream(os));
    }

    public void writeTag(Tag tag) throws IOException {
        int type = NBTUtils.getTypeCode(tag.getClass());
        String name = tag.getName();
        byte[] nameBytes = name.getBytes(NBTConstants.CHARSET);
        this.os.writeByte(type);
        this.os.writeShort(nameBytes.length);
        this.os.write(nameBytes);
        if (type == 0) {
            throw new IOException("Named TAG_End not permitted.");
        }
        writeTagPayload(tag);
    }

    private void writeTagPayload(Tag tag) throws IOException {
        int type = NBTUtils.getTypeCode(tag.getClass());
        switch (type) {
            case NBTConstants.TYPE_END /* 0 */:
                writeEndTagPayload((EndTag) tag);
                return;
            case NBTConstants.TYPE_BYTE /* 1 */:
                writeByteTagPayload((ByteTag) tag);
                return;
            case NBTConstants.TYPE_SHORT /* 2 */:
                writeShortTagPayload((ShortTag) tag);
                return;
            case NBTConstants.TYPE_INT /* 3 */:
                writeIntTagPayload((IntTag) tag);
                return;
            case NBTConstants.TYPE_LONG /* 4 */:
                writeLongTagPayload((LongTag) tag);
                return;
            case NBTConstants.TYPE_FLOAT /* 5 */:
                writeFloatTagPayload((FloatTag) tag);
                return;
            case NBTConstants.TYPE_DOUBLE /* 6 */:
                writeDoubleTagPayload((DoubleTag) tag);
                return;
            case NBTConstants.TYPE_BYTE_ARRAY /* 7 */:
                writeByteArrayTagPayload((ByteArrayTag) tag);
                return;
            case NBTConstants.TYPE_STRING /* 8 */:
                writeStringTagPayload((StringTag) tag);
                return;
            case NBTConstants.TYPE_LIST /* 9 */:
                writeListTagPayload((ListTag) tag);
                return;
            case NBTConstants.TYPE_COMPOUND /* 10 */:
                writeCompoundTagPayload((CompoundTag) tag);
                return;
            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    private void writeByteTagPayload(ByteTag tag) throws IOException {
        this.os.writeByte(tag.getValue());
    }

    private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
        byte[] bytes = tag.getValue();
        this.os.writeInt(bytes.length);
        this.os.write(bytes);
    }

    private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
        for (Tag childTag : tag.getValue().values()) {
            writeTag(childTag);
        }
        this.os.writeByte(0);
    }

    private void writeListTagPayload(ListTag tag) throws IOException {
        Class<? extends Tag> clazz = tag.getType();
        List<Tag> tags = tag.getValue();
        int size = tags.size();
        this.os.writeByte(NBTUtils.getTypeCode(clazz));
        this.os.writeInt(size);
		for (Tag value : tags) {
			writeTagPayload(value);
		}
    }

    private void writeStringTagPayload(StringTag tag) throws IOException {
        byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET);
        this.os.writeShort(bytes.length);
        this.os.write(bytes);
    }

    private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
        this.os.writeDouble(tag.getValue());
    }

    private void writeFloatTagPayload(FloatTag tag) throws IOException {
        this.os.writeFloat(tag.getValue());
    }

    private void writeLongTagPayload(LongTag tag) throws IOException {
        this.os.writeLong(tag.getValue());
    }

    private void writeIntTagPayload(IntTag tag) throws IOException {
        this.os.writeInt(tag.getValue());
    }

    private void writeShortTagPayload(ShortTag tag) throws IOException {
        this.os.writeShort(tag.getValue());
    }

    private void writeEndTagPayload(EndTag tag) {
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.os.close();
    }
}
