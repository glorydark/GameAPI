package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/LongTag.class */
public final class LongTag extends Tag {
    private final long value;

    public LongTag(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public Long getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Long" + append + ": " + this.value;
    }
}
