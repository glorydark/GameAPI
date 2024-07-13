package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/EndTag.class */
public final class EndTag extends Tag {
    public EndTag() {
        super("");
    }

    @Override // org.jnbt.Tag
    public Object getValue() {
        return null;
    }

    public String toString() {
        return "TAG_End";
    }
}
