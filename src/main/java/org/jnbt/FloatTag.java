package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/FloatTag.class */
public final class FloatTag extends Tag {
    private final float value;

    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public Float getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Float" + append + ": " + this.value;
    }
}
