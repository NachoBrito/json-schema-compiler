package es.nachobrito.jsonschema.compiler;

import java.util.Arrays;

public class Example {
    private transient int transientVar = 10;
    private String name;
    private double score;
    private String[] tags;
    private int id;

    public String getName() {
        return this.name;
    }

    @Override public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Example)) return false;
        Example other = (Example) o;
        if (!other.canEqual((Object)this)) return false;
        if (this.getName() == null ? other.getName() != null : !this.getName().equals(other.getName())) return false;
        if (Double.compare(this.score, other.score) != 0) return false;
        if (!Arrays.deepEquals(this.tags, other.tags)) return false;
        return true;
    }

    @Override public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long temp1 = Double.doubleToLongBits(this.score);
        result = (result*PRIME) + (this.name == null ? 43 : this.name.hashCode());
        result = (result*PRIME) + (int)(temp1 ^ (temp1 >>> 32));
        result = (result*PRIME) + Arrays.deepHashCode(this.tags);
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Example;
    }

}
