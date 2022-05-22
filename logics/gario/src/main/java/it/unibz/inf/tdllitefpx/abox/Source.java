package it.unibz.inf.tdllitefpx.abox;

import it.unibz.inf.tdllitefpx.roles.Role;

public class Source {
    Individual ind;
    Role role;

    int timestamp;

    public Source(String ind_name, Role role) {
        this.ind = new Individual(ind_name);
        this.role = role;
        this.timestamp = 0;
    }

    public Source(String ind_name, Role role, int timestamp) {
        this.ind = new Individual(ind_name);
        this.role = role;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return ind.hashCode() * role.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Source)) {
            return false;
        }

        return this.ind.equals(((Source) o).ind) && this.role.equals(((Source) o).role);
    }
}
