package it.unibz.inf.tdllitefpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;

import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;

import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;


public class RigidRoleQ {
	
	public static void main(String[] args) throws Exception{
		RigidRoleQ exTDL = new RigidRoleQ();
		
		
		TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(
				exTDL.getTBox(),
				true, 
				"RigidName",exTDL.getABox(),false);
		
		Map<String, String> stats = exTDL.getTBox().getStats();
		System.out.println("");
		System.out.println("------TBOX------");
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));
		System.out.println("------ABOX------");
		Map<String, String> statsA = exTDL.getABox().getStatsABox();
		key="Concept_Assertion:";
		System.out.println(key+ statsA.get(key));
		key="Roles_Assertion:";
		System.out.println(key+ statsA.get(key));
	
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("C:\\Program Files (x86)\\NuSMV-2.6\\bin\\NuSMV.exe", "RigidName.smv");
		//processBuilder.redirectOutput().toString();
		try {

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
				System.exit(0);
			} else {
				//abnormal...
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	Concept Person = new AtomicConcept("Person");
	
	Role Name = new PositiveRole(new AtomicRigidRole("Name"));
	

	
	
	public ABox getABox(){
		
		ABox A = new ABox();
		ABoxConceptAssertion a1= new ABoxConceptAssertion(Person,"John");
		A.addConceptsAssertion(a1);
		
		ABoxRoleAssertion r1= new ABoxRoleAssertion(Name,"John", "Mcking",0);
		A.addABoxRoleAssertion(r1);
		ABoxRoleAssertion r2= new ABoxRoleAssertion(Name,"John", "Kennedy",2);
		A.addABoxRoleAssertion(r2);
		ABoxRoleAssertion r3= new ABoxRoleAssertion(Name,"John", "Garcia",1);
		A.addABoxRoleAssertion(r3);
	
		return A;
	}
	
	public TBox getTBox(){
		
		TBox t = new TBox();
		t.addAll(getTBoxT0());
		t.addAll(getTBoxT1());
		
	
		return t;
	}
	
	private TBox getTBoxT0(){
		TBox t0 = new TBox();

		// D
		
		// a person has one Name
			t0.add(new ConceptInclusionAssertion(
				Person,
				new QuantifiedRole(Name, 2)));
		
			// test rigid role with cardinality Q=2	
			t0.add(new ConceptInclusionAssertion(
					Person,
					new QuantifiedRole(Name, 1)));
		t0.add(new ConceptInclusionAssertion(
				Person,
				new NegatedConcept(new QuantifiedRole(Name, 3))));

		// R
		
		
		// H
		
		
		return t0;
	}
	
	private TBox getTBoxT1(){
		TBox t1 = new TBox();
		
		// TS
		/* A person is always a Person
		t1.add(new ConceptInclusionAssertion(
				Person,
				new AlwaysFuture(new AlwaysPast(Person))));*/
		

		// TRANS
		
		
		// EVO


		// GEN
		
		return t1;
	}
	

}

