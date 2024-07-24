package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/Tag.class */
public abstract class Tag {
    private final String name;

    public Tag(String name) {
        this.name = name;
    }

    public abstract Object getValue();

    public final String getName() {
        return this.name;
    }
}
