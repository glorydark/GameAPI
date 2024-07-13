package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/StringTag.class */
public final class StringTag extends Tag {
    private final String value;

    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override // org.jnbt.Tag
    public String getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_String" + append + ": " + this.value;
    }
}
