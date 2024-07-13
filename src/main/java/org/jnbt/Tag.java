package org.jnbt;

/* loaded from: jnbt-1.1.jar:org/jnbt/Tag.class */
public abstract class Tag {
    private final String name;

    public abstract Object getValue();

    public Tag(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}
