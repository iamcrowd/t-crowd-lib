package it.unibz.inf.tdllitefpx.abox;

public class Individual {
    final String name;
    int hash = 0;

    public Individual(String name) {
        this.name = name;
    }

    public int hashCode() {
        if (hash == 0) {
            hash = name.hashCode();
        }

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
