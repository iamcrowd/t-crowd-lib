### Temporal Library for encoding Temporal Models into DL-Lite and reason over them. This library outs a file for feeding each off-the-shelf QTL/LTL reasoner. t-crowd library is partially based on a legacy library built by Marco Gario at Free University of Bozen-Bolzano (Italy)

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help`

```
usage: tcrowd <command> [ <args> ]

Commands are:
    --version    Show version of t-crowd
    help         Display help information
    ltl          Encode ERvt model into LTL formulae
    NuSMV        Encode ERvt model into LTL formulae and return a LTL file together with a NuSMV file
    qtln         Encode ERvt model into QTL formulae over natural numbers
    qtlz         Encode ERvt model into QTL formulae over integers
    tdllitefpx   Encode ERvt model as a KB in TDL DL-Litefpx


See 'tcrowd help <command>' for more information on a specific command.
```

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help tdllitefpx`

```
NAME
        tcrowd tdllitefpx - Encode ERvt model as a KB in TDL DL-Litefpx

SYNOPSIS
        tcrowd tdllitefpx [ {-t | --tmodel} <temporal model> ]

OPTIONS
        -t <temporal model>, --tmodel <temporal model>
            JSON file input containing a temporal model
```


#### Gario Legacy example

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd tdllitefpx -t value.json`

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd qtlz -t value.json`

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd qtln -t value.json`

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ltl -t value.json`

`$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd NuSMV -t value.json`


## For Developers

#### JAVA 8 (at least)
#### MAVEN 3.6 (at least)

#### Using Maven for creating JAR and dependencies

```
$ mvn clean verify -Dmaven.javadoc.skip=true

$ mvn clean dependency:copy-dependencies package -Dmaven.javadoc.skip=true

```
### Tests
#### JUnit 5
https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-running-unit-tests-with-maven/

`$ mvn clean test`

`$ mvn -Dtest=it.gilia.tcrowd.encoding.DefaultStrategyTest -DfailIfNoTests=false test`

`$ mvn -Dtest=it.gilia.tcrowd.encoding.DefaultStrategyABoxTest -DfailIfNoTests=false test`

