package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.annotations.Command;
//import com.github.rvesse.airline.annotations.Option;
//import com.github.rvesse.airline.annotations.OptionType;

//import it.unibz.inf.qtl1.NaturalTranslator;
//import it.unibz.inf.qtl1.formulae.Formula;
//import it.unibz.inf.qtl1.output.LatexDocumentCNF;
//import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.ExampleTDL;
//import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;

import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;

import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;

import it.unibz.inf.tdllitefpx.TestAbox1;
import it.unibz.inf.tdllitefpx.Adult;
import it.unibz.inf.tdllitefpx.RigidRoleQ;

import it.gilia.tcrowd.encoding.DefaultStrategy;
import it.gilia.tcrowd.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.Map;
import java.util.Objects;

import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;


@Command(name = "abox",
        description = "Check for Abox Consistency")
public class TCrowdAboxSat extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            
			TestAbox1 exTDL = new TestAbox1();

			TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(exTDL.getTBox(), true, "Abox1", exTDL.getABox(),false);

			Map<String, String> stats = exTDL.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX------");
			String key;
			key = "Basic Concepts:";
			System.out.println(key + stats.get(key));
			key = "Roles:";
			System.out.println(key + stats.get(key));
			key = "CIs:";
			System.out.println(key + stats.get(key));
			System.out.println("------ABOX------");
			Map<String, String> statsA = exTDL.getABox().getStatsABox();
			key = "Concept_Assertion";
			System.out.println(key + statsA.get(key));
			key = "Roles_Assertion:";
			System.out.println(key + statsA.get(key));

			Adult exAdultTDL = new Adult();

			TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(exAdultTDL.getTBox(), true, "Adult", exAdultTDL.getABox(),false);

			Map<String, String> stats1 = exAdultTDL.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX Adult------");
			String key1;
			key1 = "Basic Concepts:";
			System.out.println(key1 + stats1.get(key1));
			key1 = "Roles:";
			System.out.println(key1 + stats1.get(key1));
			key1 = "CIs:";
			System.out.println(key1 + stats1.get(key1));
			System.out.println("------ABOX Adult------");
			Map<String, String> statsA1 = exAdultTDL.getABox().getStatsABox();
			key1 = "Concept_Assertion";
			System.out.println(key1 + statsA1.get(key1));
			key1 = "Roles_Assertion:";
			System.out.println(key1 + statsA1.get(key1));
			
			RigidRoleQ exRigidRoleQTDL = new RigidRoleQ();

			TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(exRigidRoleQTDL.getTBox(), true, "RigidName", exRigidRoleQTDL.getABox(),false);

			Map<String, String> stats2 = exRigidRoleQTDL.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX RigidRoleQ------");
			String key2;
			key2 = "Basic Concepts:";
			System.out.println(key2 + stats2.get(key2));
			key2 = "Roles:";
			System.out.println(key2 + stats2.get(key2));
			key2 = "CIs:";
			System.out.println(key2 + stats2.get(key2));
			System.out.println("------ABOX RigidRoleQ------");
			Map<String, String> statsA2 = exRigidRoleQTDL.getABox().getStatsABox();
			key2 = "Concept_Assertion";
			System.out.println(key2 + statsA2.get(key2));
			key2 = "Roles_Assertion:";
			System.out.println(key2 + statsA2.get(key2));

    			

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
