package it.unibz.inf.tdllitefpx.abox;

import it.unibz.inf.tdllitefpx.roles.Role;

public class Source {
    Individual individual;
    Role role;

    Integer timestamp;

    public Source(Individual ind, Role role) {
        this.individual = ind;
        this.role = role;
        this.timestamp = null;
    }

    public Source(Individual ind, Role role, int timestamp) {
        this.individual = ind;
        this.role = role;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + ((role == null) ? 0 : role.hashCode());
        hash = prime * hash + ((timestamp == null) ? 0 : timestamp.hashCode());
        hash = prime * hash + ((individual == null) ? 0 : individual.hashCode());

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Source)) {
            return false;
        }

        return this.individual.equals(((Source) o).individual) && this.role.equals(((Source) o).role);
    }
}
