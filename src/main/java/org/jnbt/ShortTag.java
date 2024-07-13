package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/ShortTag.class */
public final class ShortTag extends Tag {
    private final short value;

    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public Short getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Short" + append + ": " + ((int) this.value);
    }
}
