package it.gilia.tcrowd.encoding;

import it.unibz.inf.tdllitefpx.ExampleTDL;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;

/**
   I translate a JSON formatted diagram into something else depending on the Builder instance given.

   1. Give a Strategy translator instance for specifying the algorithm for translating the diagram.
   2. Give a Builder for specifying the output format.

   # JSON Format

   We expect the following fields:

   - `classes` : An Array of classes information. Each class should have:
     - `attrs` An array of strings representing all attributes names
     - `methods` An array of strings representing all attributes names
     - `name` A string which represent the name of the class.
   - links : An array of links information. Each link should have:
     - `classes` : An array of strings with the name of the classes involved on the relationship.
     - `multiplicity` : An array of strings with the multiplicity on each class side.
     - `name` : A string with the name of the link.
     - `type` : A string with the type name of the link. Could be: "association", "generalization".

   ## Example
   @code{json}
	{"entities":
    	[
        	{"name":"Entity1","timestamp":"snapshot","position":{"x":625,"y":183}},
        	{"name":"Entity2","timestamp":"temporal","position":{"x":328,"y":411}},
        	{"name":"Entity3","timestamp":"","position":{"x":809,"y":432}},
        	{"name":"Entity4","timestamp":"","position":{"x":259,"y":187}},
        	{"name":"Entity5","timestamp":"","position":{"x":151,"y":412}}
    	],
	"attributes":
    	[
        	{"name":"A","type":"key","datatype":"integer","timestamp":"snapshot","position":{"x":625,"y":183}},
        	{"name":"B","type":"normal","datatype":"string","timestamp":"temporal","position":{"x":809,"y":432}},
        	{"name":"C","type":"normal","datatype":"string","timestamp":"","position":{"x":809,"y":432}}
    	],
	"links":
    	[
        	{"name":"AttrA","entity":"Entity1","attribute":"A","type":"attribute"},
        	{"name":"AttrB","entity":"Entity2","attribute":"B","type":"attribute"},
        	{"name":"AttrC","relationship":"R1","attribute":"C","type":"attribute_rel"},
        	{"name":"R","entities":["Entity4","Entity1"],"cardinality":["1..4","3..5"],"roles":["entity4","entity1"],"timestamp":"temporal","type":"relationship"},
        	{"name":"R1","entities":["Entity2","Entity3"],"cardinality":["0..*","0..*"],"roles":["entity2","entity3"],"timestamp":"snapshot","type":"relationship"},
        	{"name":"s1","parent":"Entity4","entities":["Entity2","Entity5"],"type":"isa","constraint":["disjoint","covering"]},
        	{"name":"s2","parent":"Entity1","entities":["Entity3"],"type":"isa","constraint":[]},
        	{"name":"tex1","entities":["Entity2","Entity3"],"type":"tex"},
        	{"name":"dev1","entities":["Entity4","Entity5"],"type":"dev"},
        	{"name":"dexminus1","entities":["Entity1","Entity2"],"type":"dexminus"},
        	{"name":"pex1","entities":"Entity1","type":"pex"},        
    	]
	}
   @endcode
 */

public class Encoder{
	
	public void ervt2dl() {
		// obtener json para parsear
		// definir estrategia de traducción
	}
}