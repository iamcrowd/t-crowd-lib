package it.unibz.inf.dllite;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.Process;

import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.pltl.PltlOutput;
import it.unibz.inf.tdllitefpx.roles.Role;

/**
 * It implements the ProcessTask for sat checking Roles.
 * It returns only the UNSAT roles (SAT roles will be removed from the QTL formula)
 */
public class ProcessRolesTask implements Callable<String> {

    private Formula ltl_tbox;
    private DLLiteConverter converter;
    private Role role;
    private ExecutorService service;

    //processBuilder.command("/home/gbraun/Documentos/TemporalDLlite/NuXMV/nuXmv", "-dcx", "-bmc", "-bmc_length", "60", file);
	//processBuilder.command("/home/gbraun/Documentos/TemporalDLlite/NuXMV/nuXmv","-source", "/home/gbraun/Documentos/TemporalDLlite/NuXMV/script.txt", file);

    public ProcessRolesTask(Formula ltl, DLLiteConverter conv, Role role, ExecutorService service) {
        this.ltl_tbox = ltl;
        this.converter = conv;
        this.role = role;
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        System.out.println("(*) Started checking SAT for Role " + role.toString());

        Formula formRole = converter.getFormulaByRole(role);
        Formula ltlR = new ConjunctiveFormula(ltl_tbox, formRole.makePropositional());
        
        String file = role.toString() + ".smv";
        (new NuSMVOutput(ltlR)).toFile(file);

        String file2 = role.toString() + ".pltl";
        (new PltlOutput(ltlR)).toFile(file2);

        System.out.println("Number of Propositional Variables in the RoleSAT instance: " + ltlR.getPropositions().size());

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
            System.out.println("Success!");
            if (output.toString().contains("true") || output.toString().contains("UNSAT")){
                return role.toString();
            } else {
                return null;
            }
        }
        p5.exitValue();
        service.shutdownNow();
        System.out.println("The process: " + p5.pid() + " for the Role: " + role.toString() +
                          " finished abnormally along with the remaining running processes");
        System.exit(-1);
        throw new Exception();
    }

}