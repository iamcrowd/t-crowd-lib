package it.gilia.tcrowd.importer;

import static it.gilia.tcrowd.importer.ImportUtils.validateOWL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.*;
import java.text.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.util.*;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectCardinalityRestrictionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLQuantifiedRestrictionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.ConjunctiveConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.roles.AtomicRole;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
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

		TBox myTBox = new TBox();
		
		public OWLImport() {
			this.manager = OWLManager.createOWLOntologyManager();
		}

		/**
		 * Returns the OWL ontology being imported.
		 * 
		 * @return
		 */
		public OWLOntology getOntology(){
			return ontology;
		}

		/**
		 * Returns the current TBox
		 * 
		 * @return
		 */
		public TBox getTBox(){
			return this.myTBox;
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

		/**
     	* Example with atomic CI
		* 
     	* @implSpec EQUIVALENT_CLASSES(A, B) can be rewritten as SUBCLASS_OF(A, B) and SUBCLASS_OF(B, A)
		* @implSpec DISJOINT_CLASSES(A, B) can be rewritten as SUBCLASS_OF(A, COMPLEMENT_OF(B)) and SUBCLASS_OF(B, COMPLEMENT_OF(A))
		* @implSpec OBJECT_PROPERTY_DOMAIN(p, A) can be rewritten as SUBCLASS_OF(>= 1 p.TOP, A)
		* @implSpec OBJECT_PROPERTY_RANGE(p, B) can be rewritten as SUBCLASS_OF(>= 1 p^-.TOP, B)
		* @implSpec FUNCTIONAL_OBJECT_PROPERTY(p) can be rewritten as SUBCLASS_OF(TOP, <= 1 p.TOP), i.e. SUBCLASS_OF(TOP, COMPLEMENT_OF(>= 1 p.TOP + 1R))
		* @implSpec DATA_PROPERTY_DOMAIN(d, A) can be rewritten as SUBCLASS_OF(>= 1 d.TOP, A)
		* @implSpec DATA_PROPERTY_RANGE(d, DT) can be rewritten as SUBCLASS_OF(>= 1 d^-.TOP, DT)
		*
     	*/
    	public void dlliteCI() {
        	// get all tbox axioms
        	Set<OWLAxiom> tboxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        	// iterate each axiom
        	tboxAxioms.forEach(axiom -> {
            	try {
                	// determine if axiom is of type SubClassOf
					System.out.println(axiom.toString());

                	if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    	// get left and right expressions (SubClass -> SuperClass)
                    	OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
                    	OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();
						
						Concept dllite_left = ConvertToDllite(left);
						Concept dllite_right = ConvertToDllite(right);
						this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));

                	} else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)){
						Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
						subClassOfAxioms = ((OWLEquivalentClassesAxiom) axiom).asOWLSubClassOfAxioms();

						subClassOfAxioms.forEach(ax -> {
							OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
                    		OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
					
							Concept dllite_left = ConvertToDllite(left);
							Concept dllite_right = ConvertToDllite(right);
							this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));

						});

					} else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)){
						Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
						subClassOfAxioms = ((OWLDisjointClassesAxiom) axiom).asOWLSubClassOfAxioms();

						subClassOfAxioms.forEach(ax -> {
							OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
                    		OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
					
							Concept dllite_left = ConvertToDllite(left);
							Concept dllite_right = ConvertToDllite(right);
							this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
						});

					} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
						OWLObjectPropertyExpression property = ((OWLObjectPropertyDomainAxiom) axiom).getProperty();
						OWLClassExpression domain = ((OWLObjectPropertyDomainAxiom) axiom).getDomain();
						OWLObjectMinCardinality scoa = new OWLObjectMinCardinalityImpl(property, 1, TOP);
					
						Concept dllite_left = ConvertToDllite(scoa);
						Concept dllite_right = ConvertToDllite(domain);
						this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
					} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE)) {
						OWLObjectPropertyExpression property = ((OWLObjectPropertyRangeAxiom) axiom).getProperty();
						OWLClassExpression range = ((OWLObjectPropertyRangeAxiom) axiom).getRange();
						OWLObjectMinCardinality scoa = new OWLObjectMinCardinalityImpl(property, 1, TOP);
					
						Concept dllite_left = ConvertToDllite(scoa);
						Concept dllite_right = ConvertToDllite(range);
						this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
					}
            	} catch (Exception e) {

            	}
        	});
		}

		/**
		 * Converts an OWL Class Expression to a DL-Lite concept
		 * 
		 * @param e - an OWL Class Expression
		 * @return result - a DL-Lite concept
		 */
		private static Concept ConvertToDllite(OWLClassExpression e) {
			if (isAtomic(e)) {
				return new AtomicConcept(e.asOWLClass().getIRI().getFragment());
			}

			if (isNegated(e)) {
				OWLClassExpression operand = ((OWLObjectComplementOf)e).getOperand();
				return new NegatedConcept(ConvertToDllite(operand));
			}

			if (isConjunctive(e)) {
				ConjunctiveConcept concept = new ConjunctiveConcept();
				Set<OWLClassExpression> conjunctions = e.asConjunctSet();

				for (OWLClassExpression c : conjunctions) {
					concept.add(ConvertToDllite(c));
				}

				return concept;
			}

			if (isQuantifiedRole(e)) {
				OWLClassExpression filler = ((OWLObjectCardinalityRestrictionImpl)e).getFiller();
				
				if (filler.equals(TOP)){
					OWLPropertyExpression property = ((OWLObjectCardinalityRestrictionImpl) e).getProperty();
					Role positive_role = new PositiveRole(new AtomicRigidRole(property.asOWLObjectProperty().getIRI().getFragment()));
					int cardinality = ((OWLObjectCardinalityRestrictionImpl)e).getCardinality();

					return new QuantifiedRole(positive_role, cardinality);
				}

			}

			throw new EmptyStackException();
		}

	}	
