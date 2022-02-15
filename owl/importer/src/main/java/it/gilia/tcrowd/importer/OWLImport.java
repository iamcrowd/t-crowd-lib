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
 	* @author gab
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
	 	* @param ontology
	 	* 
	 	* Prints the TBox of {@code ontology} on the standard output.
	 	*/
		public void printTBox() {
			Stream<OWLAxiom> tBoxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED);
			tBoxAxioms.forEach((ax) -> System.out.println(ax.toString()));
		}


		public static boolean isAtomic(OWLClassExpression e) {
			return e.isOWLClass() || e.isTopEntity() || e.isBottomEntity();
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

                    	// check if axiom is of (atomic, atomic)
                    	if (isAtomic(left) && isAtomic(right)) {
							System.out.println(axiom.toString());
							this.myTBox.add(new ConceptInclusionAssertion(new AtomicConcept(left.asOWLClass().getIRI().getFragment()), 
																		  new AtomicConcept(right.asOWLClass().getIRI().getFragment())));
                   	 	} else {
                       	 	throw new EmptyStackException();
                    	}
                	}
            	} catch (Exception e) {

            	}
        	});


			try{
				(new LatexOutputDocument(this.myTBox)).toFile("dllitetbox.tex");
			} catch (Exception e) {}
		}

	}	
