### Temporal Library for encoding Temporal Models into DL-Lite and reason over them. This library outs a file for feeding each off-the-shelf QTL/LTL reasoner. Run t-crowd help to see options. t-crowd library is partially based on a legacy library built by Marco Gario at Free University of Bozen-Bolzano (Italy)

``$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help''

``
usage: tcrowd <command> [ <args> ]

Commands are:
    --version    Show version of t-crowd
    help         Display help information
    tdllitefpx   Encode ERvt model as a KB in TDL DL-Litefpx

See 'tcrowd help <command>' for more information on a specific command.
''

``$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd help tdllitefpx''

``NAME
        tcrowd tdllitefpx - Encode ERvt model as a KB in TDL DL-Litefpx

SYNOPSIS
        tcrowd tdllitefpx [ {-t | --tmodel} <temporal model> ]

OPTIONS
        -t <temporal model>, --tmodel <temporal model>
            JSON file input containing a temporal model''


### Gario Legacy example

``$ java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd tdllitefpx -t value.json''

