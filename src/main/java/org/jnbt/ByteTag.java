package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/ByteTag.class */
public final class ByteTag extends Tag {
    private final byte value;

    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public Byte getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Byte" + append + ": " + ((int) this.value);
    }
}
