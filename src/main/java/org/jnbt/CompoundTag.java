package org.jnbt;

import java.util.Collections;
import java.util.Map;

/* loaded from: jnbt-1.1.jar:org/jnbt/CompoundTag.class */
public final class CompoundTag extends Tag {
    private final Map<String, Tag> value;

    public CompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = Collections.unmodifiableMap(value);
    }

    @Override // org.jnbt.Tag
    public Map<String, Tag> getValue() {
        return this.value;
    }

    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        StringBuilder bldr = new StringBuilder();
        bldr.append("TAG_Compound").append(append).append(": ").append(this.value.size()).append(" entries\r\n{\r\n");
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            bldr.append("   ").append(entry.getValue().toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }
}
