package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/IntTag.class */
public final class IntTag extends Tag {
    private final int value;

    public IntTag(String name, int value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public Integer getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Int" + append + ": " + this.value;
    }
}
