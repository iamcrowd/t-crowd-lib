package it.gilia.tcrowd.importer;

import static it.gilia.tcrowd.importer.ImportUtils.validateOWL;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.util.stream.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectCardinalityRestrictionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyCharacteristicAxiomImpl;

import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.ConjunctiveConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;


	/**
 	* 
 	* @author
 	*
 	*/
	public class OWLImport {

		private OWLOntology ontology;
		private OWLOntologyManager manager;

		private static final OWLClassExpression TOP = new OWLDataFactoryImpl().getOWLThing();
		private static final OWLClassExpression BOT = new OWLDataFactoryImpl().getOWLNothing();

		public HashMap<String, HashSet<Constant>> prefix = new HashMap<String, HashSet<Constant>>();
		public HashMap<String, String> namespaces = new HashMap<String, String>();

		TBox myTBox = new TBox();
		ABox myABox = new ABox();
		
		public OWLImport() {
			this.manager = OWLManager.createOWLOntologyManager();
		}

		/**
		 * Returns the OWL ontology being imported.
		 * 
		 * @return
		 */
		public OWLOntology getOntology() {
			return ontology;
		}

		/**
		 * Returns the current TBox
		 * 
		 * @return
		 */
		public TBox getTBox() {
			return this.myTBox;
		}

		/**
		 * Returns the current ABox
		 * 
		 * @return
		 */
		public ABox getABox() {
			return this.myABox;
		}

		/**
     	* It loads an OWL 2 ontology from a URI
		*
     	* @param iri a String containing an Ontology URI.
     	*/
    	public void load(IRI iri) {
        	try {
            	validateOWL(iri);
            	this.ontology = this.manager.loadOntologyFromOntologyDocument(iri);
        	} catch (Exception e) {
            	System.out.println("Error loading ontology with iri: " + iri + ". (" + e.getMessage() + ")");
        	}
    	}

		/**
     	* It loads an OWL 2 ontology from a file
		*
     	* @param path a String containing a file path to an Ontology File.
     	*/
    	public void loadFromPath(String path) {
        	try {
            	File file = new File(path);
            	validateOWL(file);
            	this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
        	} catch (Exception e) {
            	System.out.println("Error loading ontology with path: " + path + ". (" +
                    e.getMessage() + ")");
        	}
    	}

		/**
	 	* 
	 	* It prints the TBox of {@code ontology} on the standard output.
	 	*/
		public void printTBox() {
			Stream<OWLAxiom> tBoxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED);
			tBoxAxioms.forEach((ax) -> System.out.println(ax.toString()));
		}


		public void printNamespaces(){
			for (Entry<String, HashSet<Constant>> entry : prefix.entrySet()) {
				String key = entry.getKey();
				Set<Constant> value = entry.getValue();

				System.out.println("Namespace: " + key);
				for (Constant c : value){
					System.out.println("Name: " + c.toString());
				}
			}
		}


		/**
		 * It generates a prefix for each different namespace in the IRIs and also
		 * encodes each (concept|role|individual) name as the shorter "prefix + name" (whitout the namespace)
		 * 
		 * @param aIRI a full IRI
		 * @return
		 */
		public Constant getPrefixed(IRI aIRI){
			String iri_prefix = aIRI.getNamespace();
			String pf = null;

			if (namespaces.get(iri_prefix) == null) {
				new RandomStringUtils();
				pf = RandomStringUtils.randomAlphanumeric(3);
				namespaces.put(iri_prefix, pf);
			} else {
				pf = namespaces.get(iri_prefix); 
			}
			
			String name = aIRI.getFragment();
			
			if (prefix.get(iri_prefix) == null) {
				prefix.put(iri_prefix, new HashSet<Constant>());
			}

			Constant prefixed_constant = new Constant(pf + "_" + name);
			prefix.get(iri_prefix).add(prefixed_constant);
			return prefixed_constant;
		}


		/**
		 * It checks if an OWLClassExpression is an atomic concept
		 * 
		 * @param e an OWLClassExpression object
		 * @return
		 */
		public static boolean isAtomic(OWLClassExpression e) {
			return e.isOWLClass() || e.isTopEntity() || e.isBottomEntity();
		}

		public static boolean isNegated(OWLClassExpression e) {
			return e.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF;
		}

		public static boolean isConjunctive(OWLClassExpression e) {
			return e.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF;
		}

		public static boolean isQuantifiedRole(OWLClassExpression e) {
			return e.getClassExpressionType() == ClassExpressionType.OBJECT_MIN_CARDINALITY;
		}

		public static boolean isUnion(OWLClassExpression e) {
			return e.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF;
		}
		/**
     	* Example with atomic CI
		* 
     	* @implSpec EQUIVALENT_CLASSES(A, B) can be rewritten as SUBCLASS_OF(A, B) and SUBCLASS_OF(B, A)
		* @implSpec DISJOINT_CLASSES(A, B) can be rewritten as OBJECT_INTERSECTION_OF(SUBCLASS_OF(A, COMPLEMENT_OF(B)) and SUBCLASS_OF(B, COMPLEMENT_OF(A)))
		* @implSpec OBJECT_PROPERTY_DOMAIN(P, A) can be rewritten as SUBCLASS_OF(OBJECT_MIN_CARDINALITY(1, P, TOP), A)
		* @implSpec OBJECT_PROPERTY_RANGE(P, B) can be rewritten as SUBCLASS_OF(OBJECT_MIN_CARDINALITY(1, P^-, TOP), B)
		* @implSpec FUNCTIONAL_OBJECT_PROPERTY(P) can be rewritten as SUBCLASS_OF(TOP, <= 1 P.TOP), i.e. SUBCLASS_OF(TOP, COMPLEMENT_OF(>= 1 P.TOP + 1R))
		* @implSpec DATA_PROPERTY_DOMAIN(d, A) can be rewritten as SUBCLASS_OF(DATA_MIN_CARDINALITY(1, d, TOP), A)
		* @implSpec DATA_PROPERTY_RANGE(d, DT) can be rewritten as SUBCLASS_OF(DATA_MIN_CARDINALITY(1, d^-, TOP), DT)
		* @implSpec DISJOINT_UNION
		* @implSpec OBJECT_UNION(A, B) can be rewritten as COMPLEMENT_OF(OBJECT_INTERSECTION_OF(COMPLEMENT_OF(A) and COMPLEMENT_OF(B)))
		* @implSpec OBJECT_SOME_VALUES_FROM(P, TOP) as OBJECT_MIN_CARDINALITY(1, P, TOP)				
		*
     	*/

    	public void dlliteCI() {
        	// get all tbox axioms
        	Set<OWLAxiom> tboxAxioms = this.ontology.tboxAxioms(Imports.INCLUDED).collect(Collectors.toSet());

        	// iterate each axiom
        	tboxAxioms.forEach(axiom -> {
            	try {
                	// PROCESSING ONLY CONCEPT INCLUSIONS

					// CLASS AXIOMS

                	if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    	ProcessAxiomSubclassOf(axiom);
						
                	} else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)){
						ProcessAxiomEquivalentClasses(axiom);

					} else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)){
						ProcessAxiomDisjointClasses(axiom);

					} else if (axiom.isOfType(AxiomType.DISJOINT_UNION)) {
						OWLAxiom disjointness_part = ((OWLDisjointUnionAxiom) axiom).getOWLDisjointClassesAxiom();
						OWLAxiom equivalence_part = ((OWLDisjointUnionAxiom) axiom).getOWLEquivalentClassesAxiom();

						ProcessAxiomDisjointClasses(disjointness_part);
						ProcessAxiomEquivalentClasses(equivalence_part);
						
					// PROPERTY AXIOMS: DOMAIN, RANGE, FUNCTIONAL

					} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
						ProcessAxiomPropertyDomain(axiom);

					} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE)) {
						ProcessAxiomPropertyRange(axiom);

					} else if (axiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {
						ProcessAxiomFunctionalProperty(axiom);

					} else if (axiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)) {
						ProcessAxiomInverseFunctionalProperty(axiom);

					} 
				
            	} catch (Exception e) {

            	}
        	});
		}

		public void dlliteAbox() {
			Set<OWLAxiom> aboxAxioms = this.ontology.aboxAxioms(Imports.INCLUDED).collect(Collectors.toSet());

        	// iterate each axiom
        	aboxAxioms.forEach(axiom -> {	
            	try {
					if (axiom.isOfType(AxiomType.CLASS_ASSERTION)) {
						OWLClassExpression e = ((OWLClassAssertionAxiom) axiom).getClassExpression();
						OWLIndividual ind = ((OWLClassAssertionAxiom) axiom).getIndividual();

						if (e.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
							AddAtomicConceptAssertion(e, ind);
						} else if (e.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF) {
							AddNegatedConceptAssertion(e, ind);
						} else if (e.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
							Set<OWLClassExpression> conjuncts = e.asConjunctSet();

							for (OWLClassExpression c : conjuncts) {
								if (c.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
									AddAtomicConceptAssertion(c, ind);
								} else if (c.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF) {
									AddNegatedConceptAssertion(c, ind);
								}
							}
						}

					} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
						OWLObjectPropertyExpression property = 
							((OWLObjectPropertyAssertionAxiom) axiom).getProperty();

						OWLIndividual subject = ((OWLObjectPropertyAssertionAxiom) axiom).getSubject();
						OWLIndividual object = ((OWLObjectPropertyAssertionAxiom) axiom).getObject();

						AtomicRigidRole atomic = new AtomicRigidRole(getPrefixed(property.asOWLObjectProperty().getIRI()).toString());

						PositiveRole role = new PositiveRole(atomic);
						this.myABox.addABoxRoleAssertion(new ABoxRoleAssertion(role, 
																			   getPrefixed(subject.asOWLNamedIndividual().getIRI()).toString(), 
																			   getPrefixed(object.asOWLNamedIndividual().getIRI()).toString(), 0));
					}
				} catch (Exception e) {
					e.printStackTrace();
            	}
			});
		}


		private void AddNegatedConceptAssertion(OWLClassExpression e, OWLIndividual ind) {
			OWLClassExpression operand = ((OWLObjectComplementOf)e).getOperand();
			
			if (operand.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
				this.myABox.addConceptsAssertion(new ABoxConceptAssertion(
												  	new NegatedConcept(
														new AtomicConcept(getPrefixed(operand.asOWLClass().getIRI()).toString())),														
				ind.asOWLNamedIndividual().getIRI().getIRIString()));
			}
		}

		private void AddAtomicConceptAssertion(OWLClassExpression e, OWLIndividual ind) {
			this.myABox.addConceptsAssertion(new ABoxConceptAssertion(
												new AtomicConcept(getPrefixed(e.asOWLClass().getIRI()).toString()), 
																  getPrefixed(ind.asOWLNamedIndividual().getIRI()).toString()));
		}

		// AXIOM PROCESSORS ///////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////

		// CLASS AXIOMS ///////////////////////////////////////////////////////

		private void ProcessAxiomSubclassOf(OWLAxiom axiom) {

			// get left and right expressions (SubClass -> SuperClass)

			OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
			OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();

			// normalise UNION_OF -> Atom
			if (isUnion(left) && isAtomic(right)){
				Set<OWLClassExpression> disjuncts = left.asDisjunctSet();
				Concept dllite_right = ConvertConceptToDllite(right);
				for (OWLClassExpression d : disjuncts) {
					Concept dllite_left = ConvertConceptToDllite(d);
					this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
				}
			} else {
				Concept dllite_left = ConvertConceptToDllite(left);
				Concept dllite_right = ConvertConceptToDllite(right);
				this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
			}
		}

		private void ProcessAxiomEquivalentClasses(OWLAxiom axiom) {
			Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
			subClassOfAxioms = ((OWLEquivalentClassesAxiom) axiom).asOWLSubClassOfAxioms();

			subClassOfAxioms.forEach(ax -> {
				ProcessAxiomSubclassOf(ax);

			});
		}

		private void ProcessAxiomDisjointClasses(OWLAxiom axiom) {
			Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
			subClassOfAxioms = ((OWLDisjointClassesAxiom) axiom).asOWLSubClassOfAxioms();

			List<String> leftTracked = new ArrayList<String>();
			subClassOfAxioms.forEach(ax -> {
				OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
				OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();

				leftTracked.add(left.toString());
				if (!leftTracked.contains(((OWLObjectComplementOf) right).getOperand().toString())) {
					ProcessAxiomSubclassOf(ax);
				}
			});

		/*	subClassOfAxioms.forEach(ax -> {
				ProcessAxiomSubclassOf(ax);
			}); */
		}

		// OBJECT PROPERTY AXIOMS: DOMAIN AND RANGE ///////////////////////////
		
		private void ProcessAxiomPropertyDomain(OWLAxiom axiom) {
			OWLObjectPropertyExpression property = ((OWLObjectPropertyDomainAxiom) axiom).getProperty();
			OWLClassExpression domain = ((OWLObjectPropertyDomainAxiom) axiom).getDomain();
			OWLObjectMinCardinality scoa = new OWLObjectMinCardinalityImpl(property, 1, TOP);

			Concept dllite_left = ConvertConceptToDllite(scoa);
			Concept dllite_right = ConvertConceptToDllite(domain);
			this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
		}

		private void ProcessAxiomPropertyRange(OWLAxiom axiom) {
			OWLObjectPropertyExpression property = ((OWLObjectPropertyRangeAxiom) axiom).getProperty();
			OWLClassExpression range = ((OWLObjectPropertyRangeAxiom) axiom).getRange();
			OWLObjectMinCardinality scoa = new OWLObjectMinCardinalityImpl(property.getInverseProperty(), 1, TOP);

			Concept dllite_left = ConvertConceptToDllite(scoa);
			Concept dllite_right = ConvertConceptToDllite(range);
			this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
		}

		// OBJECT PROPERTY AXIOMS: FUNCTIONALITY //////////////////////////////
		
		private void ProcessAxiomFunctionalProperty(OWLAxiom axiom) {
			OWLObjectPropertyExpression property = ((OWLObjectPropertyCharacteristicAxiomImpl) axiom).getProperty();
			OWLObjectMinCardinality scoa = new OWLObjectMinCardinalityImpl(property, 2, TOP);

			Concept dllite_left = ConvertConceptToDllite(scoa);
			Concept bottom = new BottomConcept();
			this.myTBox.add(new ConceptInclusionAssertion(dllite_left, bottom));
		}

		private void ProcessAxiomInverseFunctionalProperty(OWLAxiom axiom) {
			OWLObjectPropertyExpression property = ((OWLObjectPropertyRangeAxiom) axiom).getProperty();
			OWLObjectMinCardinality scoa = new OWLObjectMinCardinalityImpl(property.getInverseProperty(), 2, TOP);

			Concept dllite_left = ConvertConceptToDllite(scoa);
			Concept bottom = new BottomConcept();
			this.myTBox.add(new ConceptInclusionAssertion(dllite_left, bottom));
		}

		/**
		 * Converts an OWL Class Expression to a DL-Lite concept
		 * 
		 * @param e - an OWL Class Expression
		 * @return result - a DL-Lite concept
		 */
		private Concept ConvertConceptToDllite(OWLClassExpression e) {
			if (isAtomic(e)) {
				return new AtomicConcept(getPrefixed(e.asOWLClass().getIRI()).toString());
			}

			if (isNegated(e)) {
				OWLClassExpression operand = ((OWLObjectComplementOf)e).getOperand();
				return new NegatedConcept(ConvertConceptToDllite(operand));
			}

			if (isConjunctive(e)) {
				ConjunctiveConcept concept = new ConjunctiveConcept();
				Set<OWLClassExpression> conjuncts = e.asConjunctSet();

				for (OWLClassExpression c : conjuncts) {
					concept.add(ConvertConceptToDllite(c));
				}

				return concept;
			}


			if (isUnion(e)) {
				ConjunctiveConcept conjunction_of_negations = new ConjunctiveConcept();
				Set<OWLClassExpression> disjuncts = e.asDisjunctSet();

				for (OWLClassExpression d : disjuncts) {
					Concept dld = ConvertConceptToDllite(d);
					conjunction_of_negations.add(new NegatedConcept(dld));
				}

				return new NegatedConcept(conjunction_of_negations);
			}

			if (isQuantifiedRole(e)) {
				OWLClassExpression filler = ((OWLObjectCardinalityRestrictionImpl)e).getFiller();

				if (filler.equals(TOP)) {
					OWLPropertyExpression namedProperty = ((OWLObjectCardinalityRestrictionImpl) e).getProperty().getNamedProperty();
					OWLPropertyExpression property = ((OWLObjectCardinalityRestrictionImpl) e).getProperty().getInverseProperty();

					Role positive_role = new PositiveRole(new AtomicRigidRole(getPrefixed(namedProperty.asOWLObjectProperty().getIRI()).toString()));
					int cardinality = ((OWLObjectCardinalityRestrictionImpl)e).getCardinality();
					
					// if prop is inverse
					if (namedProperty.equals(property)){
						return new QuantifiedRole(positive_role.getInverse(), cardinality);
					} else { // if pro is not inverse	
						return new QuantifiedRole(positive_role, cardinality);
					}
				}

			}

			throw new EmptyStackException();
		}

	}	
