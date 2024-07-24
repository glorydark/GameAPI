package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/NBTUtils.class */
public final class NBTUtils {
    private NBTUtils() {
    }

    public static String getTypeName(Class<? extends Tag> clazz) {
        if (clazz.equals(ByteArrayTag.class)) {
            return "TAG_Byte_Array";
        }
        if (clazz.equals(ByteTag.class)) {
            return "TAG_Byte";
        }
        if (clazz.equals(CompoundTag.class)) {
            return "TAG_Compound";
        }
        if (clazz.equals(DoubleTag.class)) {
            return "TAG_Double";
        }
        if (clazz.equals(EndTag.class)) {
            return "TAG_End";
        }
        if (clazz.equals(FloatTag.class)) {
            return "TAG_Float";
        }
        if (clazz.equals(IntTag.class)) {
            return "TAG_Int";
        }
        if (clazz.equals(ListTag.class)) {
            return "TAG_List";
        }
        if (clazz.equals(LongTag.class)) {
            return "TAG_Long";
        }
        if (clazz.equals(ShortTag.class)) {
            return "TAG_Short";
        }
        if (clazz.equals(StringTag.class)) {
            return "TAG_String";
        }
        throw new IllegalArgumentException("Invalid tag classs (" + clazz.getName() + ").");
    }

    public static int getTypeCode(Class<? extends Tag> clazz) {
        if (clazz.equals(ByteArrayTag.class)) {
            return 7;
        }
        if (clazz.equals(ByteTag.class)) {
            return 1;
        }
        if (clazz.equals(CompoundTag.class)) {
            return 10;
        }
        if (clazz.equals(DoubleTag.class)) {
            return 6;
        }
        if (clazz.equals(EndTag.class)) {
            return 0;
        }
        if (clazz.equals(FloatTag.class)) {
            return 5;
        }
        if (clazz.equals(IntTag.class)) {
            return 3;
        }
        if (clazz.equals(ListTag.class)) {
            return 9;
        }
        if (clazz.equals(LongTag.class)) {
            return 4;
        }
        if (clazz.equals(ShortTag.class)) {
            return 2;
        }
        if (clazz.equals(StringTag.class)) {
            return 8;
        }
        throw new IllegalArgumentException("Invalid tag classs (" + clazz.getName() + ").");
    }

    public static Class<? extends Tag> getTypeClass(int type) {
        switch (type) {
            case NBTConstants.TYPE_END /* 0 */:
                return EndTag.class;
            case NBTConstants.TYPE_BYTE /* 1 */:
                return ByteTag.class;
            case NBTConstants.TYPE_SHORT /* 2 */:
                return ShortTag.class;
            case NBTConstants.TYPE_INT /* 3 */:
                return IntTag.class;
            case NBTConstants.TYPE_LONG /* 4 */:
                return LongTag.class;
            case NBTConstants.TYPE_FLOAT /* 5 */:
                return FloatTag.class;
            case NBTConstants.TYPE_DOUBLE /* 6 */:
                return DoubleTag.class;
            case NBTConstants.TYPE_BYTE_ARRAY /* 7 */:
                return ByteArrayTag.class;
            case NBTConstants.TYPE_STRING /* 8 */:
                return StringTag.class;
            case NBTConstants.TYPE_LIST /* 9 */:
                return ListTag.class;
            case NBTConstants.TYPE_COMPOUND /* 10 */:
                return CompoundTag.class;
            default:
                throw new IllegalArgumentException("Invalid tag type : " + type + ".");
        }
    }
}
