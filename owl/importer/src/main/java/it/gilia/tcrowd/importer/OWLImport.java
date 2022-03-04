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

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLQuantifiedRestrictionImpl;
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

		TBox myTBox = new TBox();
		
		public OWLImport() {
			this.manager = OWLManager.createOWLOntologyManager();
		}

		public OWLOntology getOntology(){
			return ontology;
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
     	*/
    	public void dlliteCI() {
        	// get all tbox axioms
        	Set<OWLAxiom> tboxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        	// iterate each axiom
        	tboxAxioms.forEach(axiom -> {
            	try {
                	// determine if axiom is of type SubClassOf
                	if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    	// get left and right expressions (SubClass -> SuperClass)
                    	OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
                    	OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();
						
						Concept dllite_left = ConvertToDllite(left);
						Concept dllite_right = ConvertToDllite(right);

						System.out.println(axiom.toString());
						this.myTBox.add(new ConceptInclusionAssertion(dllite_left, dllite_right));
                	} 
            	} catch (Exception e) {

            	}
        	});

			try{
				(new LatexOutputDocument(this.myTBox)).toFile("dllitetbox.tex");
			} catch (Exception e) {}
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
				OWLClassExpression filler = ((OWLCardinalityRestriction<OWLClassExpression>)e).getFiller();
				AtomicRole atomic_role = new AtomicRigidRole(filler.asOWLClass().getIRI().getFragment());
				PositiveRole positive_role = new PositiveRole(atomic_role);

				int cardinality = ((OWLCardinalityRestriction<OWLClassExpression>)e).getCardinality();

				return new QuantifiedRole(positive_role, cardinality);
			}

			throw new EmptyStackException();
		}

	}	
