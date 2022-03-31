package it.unibz.inf.tdllitefpx.tbox;

import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

public class RBox extends LinkedList<RoleInclusionAssertion> implements FormattableObj {
    
    public boolean add(RoleInclusionAssertion ri) {
		return super.add(ri);
	}

    @Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		StringBuilder sb = new StringBuilder();
		for (RoleInclusionAssertion ri: this){
			sb.append(ri.toString(fmt)+
					fmt.getSymbol(this));
		}
		return sb.toString();
	}
}
