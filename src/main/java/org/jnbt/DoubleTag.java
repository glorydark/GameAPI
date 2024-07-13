package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/DoubleTag.class */
public final class DoubleTag extends Tag {
    private final double value;

    public DoubleTag(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public Double getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Double" + append + ": " + this.value;
    }
}
