package org.jnbt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* loaded from: jnbt-1.1.jar:org/jnbt/NBTConstants.class */
public final class NBTConstants {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final int TYPE_END = 0;
    public static final int TYPE_BYTE = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_BYTE_ARRAY = 7;
    public static final int TYPE_STRING = 8;
    public static final int TYPE_LIST = 9;
    public static final int TYPE_COMPOUND = 10;
    public static final int TYPE_INT_ARRAY = 11;
    public static final int TYPE_LONG_ARRAY = 12;

    private NBTConstants() {
    }
}
