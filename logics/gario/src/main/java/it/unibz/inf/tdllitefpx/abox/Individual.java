package it.unibz.inf.tdllitefpx.abox;

public class Individual {
    String name;
    int hash;

    public Individual(String name) {
        this.name = name;
        this.hash = name.hashCode();
    }

    public int hashCode() {
        return hash;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Individual)) {
            return false;
        }

        return this.name.equals(((Individual) o).name);
    }
}
