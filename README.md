### This library encodes Temporal Models into DL-Lite and reason over them. It outs a file for feeding off-the-shelf QTL/LTL reasoners. t-crowd library is a joint work between the Universidad Nacional del Comahue (Argentina) and the Free University of Bozen-Bolzano (Italy)

`$ java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help`

```
usage: java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd <command> [ <args> ]

Commands are:
    --version    Show version of t-crowd
    help         Display help information
    NuSMV        Encode ERvt model and Temporal Data into LTL formulae and return a LTL file 
                 together with a NuSMV file including the query given as an input file. 
                 If query file is empty, KB is to be checked for satisifiability. 
                 Otherwise, query must be a concept to be checked.
    tdllitefpx   Encode ERvt model as a KB in TDL DL-Litefpx

```

`$ java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help tdllitefpx`

```
NAME
        tdllitefpx - Encode ERvt model as a KB in TDL DL-Litefpx

SYNOPSIS
        tdllitefpx [ {-a | --tdata} <Temporal Data> ]
                [ {-t | --tmodel} <ERvt temporal model> ]

OPTIONS
        -a <Temporal Data>, --tdata <Temporal Data>
            JSON file input containing temporal data

        -t <ERvt temporal model>, --tmodel <ERvt temporal model>
            JSON file input containing an ERvt temporal model

```

`$ java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help NuSMV`

```
NAME
        NuSMV - Encode ERvt model and Temporal Data into LTL formulae
        and return a LTL file together with a NuSMV file includingthe query
        given as an input file. If query file is empty, KB is to be checked for
        satisifiability. Otherwise, query must be a concept to be checked

SYNOPSIS
        NuSMV [ {-a | --tdata} <Temporal Data> ]
                {-q | --query} <query file>
                [ {-t | --tmodel} <ERvt temporal model> ]

OPTIONS
        -a <Temporal Data>, --tdata <Temporal Data>
            JSON file input containing temporal data

        -q <query file>, --query <query file>
            Plain Query file (.txt)

        -t <ERvt temporal model>, --tmodel <ERvt temporal model>
            JSON file input containing an ERvt temporal model

```

## For Developers

#### JAVA 8 (at least)
#### MAVEN 3.6 (at least)

#### Using Maven for creating JAR and dependencies

```
$ mvn clean compile

$ dependency:copy-dependencies -DskipTests -Dmaven.javadoc.skip package

```
### Tests
#### JUnit 5
https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-running-unit-tests-with-maven/

`$ mvn clean test`

`$ mvn -Dtest=it.gilia.tcrowd.encoding.DefaultStrategyTest -DfailIfNoTests=false test`

`$ mvn -Dtest=it.gilia.tcrowd.encoding.DefaultStrategyABoxTest -DfailIfNoTests=false test`

### Library Structure

```

'client' : it implements the library cli. https://rvesse.github.io/airline/guide/

'ervt' : it parses ERvt input models (given as a JSON objects) and generates a TDL-Lite knowledge base (TBox, ABox)

'logics' : it implements the core of this library. It also allow to manipulate both QTL1 and TDL-Litepfx formulas

'outputs': it implements the output documents

```

### Collaborators

Alessandro Artale

Ana Ozaki

Sabiha Tahrat

Marco Gario

Germán Braun