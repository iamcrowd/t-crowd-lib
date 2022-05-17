package it.unibz.inf.tdllitefpx.abox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class TypeKeeper {
    HashMap<Individual, Type> IndividualToType;
    HashMap<Type, Individual> TypeToRepresentative;
    HashMap<Individual, Set<Individual>> RepresentativeToIndividuals;
    
    int typeCount = 0;

    public void addAssertion(ABoxConceptAssertion c) {
        Individual ind = new Individual(c.value);
		IndividualToType.putIfAbsent(ind, new Type());
		Type type = IndividualToType.get(ind);
		type.addConcept(c.getConceptAssertion());
		IndividualToType.replace(ind, type);
    }

    public void updateType (String name, Concept concept) {
        Individual ind = new Individual(name);
        IndividualToType.putIfAbsent(ind, new Type());
        Type type = IndividualToType.get(ind);
        type.addConcept(concept);
        IndividualToType.replace(ind, type);
    }

    public void computeAbstraction() {
        for(Individual ind : IndividualToType.keySet()) {
            Type type = IndividualToType.get(ind);
            if (TypeToRepresentative.containsKey(type)) {
                RepresentativeToIndividuals.get(
                    TypeToRepresentative.get(type)
                ).add(ind);
            } else {
                Individual repr = new Individual("t" + String.valueOf(typeCount));
                TypeToRepresentative.put(type, repr);
                RepresentativeToIndividuals.put(repr, new HashSet<Individual>());
                RepresentativeToIndividuals.get(repr).add(ind);
                typeCount++;
            }
        }
    }

    public int nIndividuals() {
        return IndividualToType.size();
    }

    public int nTypes() {
        return TypeToRepresentative.size();
    }

    public Set<ABoxConceptAssertion> getAbstractAbox() {
        Set<ABoxConceptAssertion> AbstractAbox = new HashSet<ABoxConceptAssertion>();

        for (Type type : TypeToRepresentative.keySet()) {
            Individual repr = TypeToRepresentative.get(type);
            for (Concept concept : type.value) {
                AbstractAbox.add(new ABoxConceptAssertion(concept, repr.name));
            }
        }
        
        return AbstractAbox;
    }
}
