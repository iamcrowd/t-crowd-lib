package it.unibz.inf.dllite;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.pltl.PltlOutput;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.Set;

/**
 * It implements the ProcessTask for sat checking Roles.
 * It returns only the UNSAT roles (SAT roles will be removed from the QTL formula)
 */
public class ProcessABoxTask implements Callable<String> {

    private Formula tbox_f;
    private Formula abox_f;
    private Set<Constant> pieceOfIndiv;
    private ExecutorService service;

    //processBuilder.command("/home/gbraun/Documentos/TemporalDLlite/NuXMV/nuXmv", "-dcx", "-bmc", "-bmc_length", "60", file);
	//processBuilder.command("/home/gbraun/Documentos/TemporalDLlite/NuXMV/nuXmv","-source", "/home/gbraun/Documentos/TemporalDLlite/NuXMV/script.txt", file);

    public ProcessABoxTask(Formula abox_f, Formula tbox_f, Set<Constant> piece, ExecutorService service) {
        this.abox_f = abox_f;
        this.tbox_f = tbox_f;
        this.pieceOfIndiv = piece;
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        System.out.println("(*) Started checking SAT for the piece " + this.pieceOfIndiv.toString());

        // Ground the final formula
		Formula ltl = this.tbox_f.makePropositional(this.pieceOfIndiv);
		Formula ltl_a = this.abox_f.makePropositional(this.pieceOfIndiv);
		Formula ltl_KB = new ConjunctiveFormula(ltl, ltl_a);

        String file = RandomStringUtils.randomAlphanumeric(5) + ".smv";
        (new NuSMVOutput(ltl_KB)).toFile(file);
        String file2 = RandomStringUtils.randomAlphanumeric(5) + ".pltl";
        (new PltlOutput(ltl_KB)).toFile(file2);

        System.out.println("Number of Propositional Variables in the piece of ABox: " + ltl_KB.getPropositions().size());

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("black", "solve", file2);
        pb.redirectErrorStream(true);

        Process p5 = pb.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(p5.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }

        int exitVal = p5.waitFor();

        if (exitVal == 0) {
            //System.out.println("Success!");
            if (output.toString().contains("true") || output.toString().contains("UNSAT")){
                return "UNSAT";
            } else {
                return "SAT";
            }
        }
        p5.exitValue();
        service.shutdownNow();
        System.out.println("The process: " + p5.pid() + " finished abnormally along with the remaining running processes");
        System.exit(-1);
        throw new Exception();
    }

}