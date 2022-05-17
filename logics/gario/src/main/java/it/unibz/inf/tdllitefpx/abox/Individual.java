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
}
