package it.unibz.inf.tdllitefpx.abox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RoleMap {
    HashMap<Source, Set<Individual>> SourceToTargetsRigid = new HashMap<Source, Set<Individual>>();
    HashMap<Source, Set<Individual>> SourceToTargetsRigidLocalized = new HashMap<Source, Set<Individual>>();
    HashMap<Source, Set<Individual>> SourceToTargetsLocal = new HashMap<Source, Set<Individual>>();

    void AddRigidTarget(Source source, Individual target) {
        SourceToTargetsRigid.putIfAbsent(source, new HashSet<Individual>());
        Set<Individual> targets = SourceToTargetsRigid.get(source);
        targets.add(target);
        SourceToTargetsRigid.replace(source, targets);
    }

    void AddRigidLocalizedTarget(Source source, Individual target) {
        SourceToTargetsRigidLocalized.putIfAbsent(source, new HashSet<Individual>());
        Set<Individual> targets = SourceToTargetsRigidLocalized.get(source);
        targets.add(target);
        SourceToTargetsRigidLocalized.replace(source, targets);
    }

    void AddLocalTarget(Source source, Individual target) {
        SourceToTargetsLocal.putIfAbsent(source, new HashSet<Individual>());
        Set<Individual> targets = SourceToTargetsLocal.get(source);
        targets.add(target);
        SourceToTargetsLocal.replace(source, targets);
    }
}
