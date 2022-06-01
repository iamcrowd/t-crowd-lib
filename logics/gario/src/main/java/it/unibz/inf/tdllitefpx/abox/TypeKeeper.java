package it.unibz.inf.tdllitefpx.abox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class TypeKeeper {
    HashMap<Individual, Type> IndividualToType = new HashMap<Individual, Type>();
    HashMap<Type, Individual> TypeToRepresentative = new HashMap<Type, Individual>();
    HashMap<Individual, Set<Individual>> RepresentativeToIndividuals = new HashMap<Individual, Set<Individual>>();
    
    int typeCount = 0;

    public void addAssertion(ABoxConceptAssertion c) {
        try {
            Individual ind = c.getIndividual();
		    IndividualToType.putIfAbsent(ind, new Type());
		    Type type = IndividualToType.get(ind);
		    type.addConcept(c.getConcept());
		    IndividualToType.replace(ind, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAssertion(String name, Concept concept) {
        Individual ind = new Individual(name);
        IndividualToType.putIfAbsent(ind, new Type());
        Type type = IndividualToType.get(ind);
        type.addConcept(concept);
        IndividualToType.replace(ind, type);
    }

    public void computeAbstraction() {
        Type tmp = new Type();
        for(Individual ind : IndividualToType.keySet()) {
            Type type = IndividualToType.get(ind);
            if (TypeToRepresentative.containsKey(type)) {
                Individual repr = TypeToRepresentative.get(type);
                Set<Individual> indSet = RepresentativeToIndividuals.get(repr);

                indSet.add(ind);
                RepresentativeToIndividuals.replace(repr, indSet);
            } else {
                Individual repr = new Individual("t" + String.valueOf(typeCount));
                TypeToRepresentative.put(type, repr);

                Set<Individual> indSet = new HashSet<Individual>();
                indSet.add(ind);
                RepresentativeToIndividuals.put(repr, indSet);

                typeCount++;
            }
            tmp = type;
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
            for (Concept concept : type.conceptSet) {
                AbstractAbox.add(new ABoxConceptAssertion(concept, repr.name));
            }
        }
        
        return AbstractAbox;
    }
}
