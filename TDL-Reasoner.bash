#!/bin/bash
# @param $1 {string} path to folder containing both needed json files ERvt model (tbox.json) and ABox (abox.json) (see examples)
# @param $2 {integer} number identifying particular solver (1: NuSMV | 2: Aalta)  


echo "******************************************************"
echo "*                                                    *"
echo "*                                                    *"
echo "*  Reasoning over Temporal DL-Lite - LTL/PLTL SAT    *"
echo "*                                                    *"
echo "*   Universidad Nacional del Comahue (Argentina)     *"
echo "*    Free University of Bozen Bolzano (Italy)        *"
echo "*                                                    *"
echo "*       Authors: Artale-Braun-Ozaki-Tahrat           *"
echo "*                                                    *"
echo "*                                                    *"
echo "******************************************************"
echo ""
echo "*************FORZA ITALIA!*******************"
echo ""

if [ $# -eq 0 ]
  then
    echo "No arguments supplied. Enter './TDL-Reasoner.bash help'"
    exit
fi

if [ $1 == "help" ];
then
    echo -e "\t \t \t \t \t \t \\e[0;43m**Help me to run**\\e[0m"
    echo ""
    echo -e "\t command: \t ./TDL-Reasoner.bash [example_folder] [solver number]"
    echo ""
    echo -e "\t [example_folder]: " 
    echo -e "\t \t                - (required, not empty) it must contain a TBox file named 'tbox.json'"
    echo -e "\t \t                - (optional) it could contain ABox file named 'abox.json'"
    echo -e "\t \t                - (optional) it could contain a Query file named 'query.txt'"
    echo ""
    echo -e "\t [solver number]: "
    echo -e "\t \t                - 1: NuSMV solver (model checking reduction - pure future or ptlt formulae)"
    echo -e "\t \t                - 2: Aalta solver (model checking reduction - pure future formulae)"
    echo -e "\t \t                - solver number is required"
    echo ""
    echo -e "\t \\e[0;42mCurrent features:\\e[0m"
    echo -e "\t \t                - If ABox file does not exist or is empty, only TBox is checking for satisfiability"
    echo -e "\t \t                  using NuSMV or Aalta. NuSMV accepts PLTL or PF LTL. Aalta only accepts PF LTL"
    echo ""
    echo -e "\t \t                      (a) example NuSMV: './TDL-Reasoner.bash examples/adult/ 1'" 
    echo -e "\t \t                      (b) example Aalta: './TDL-Reasoner.bash examples/adult/ 2'"  
    echo "" 
    echo -e "\t \t                - If both TBox and ABox files do exist, TBox and ABox are checked for satisfiability." 
    echo -e "\t \t                  Use NuSMV and PLTL by default."
    echo ""
    echo -e "\t \t                      (a) example: './TDL-Reasoner.bash examples/adultWithABox/ 1'"
    echo "" 
    echo -e "\t \t                - Users are required to enter 'true' or 'false' to choose using" 
    echo -e "\t \t                  only future operators or past operators"
    echo ""
    echo -e "\t \t \t              - 'true': \t TDL-Lite -> QTL1 -> QTL Pure Future -> LTL"
    echo -e "\t \t \t              - 'false': \t TDL-Lite -> QLT1 -> PLTL"
    echo ""
    echo -e "\t \\e[0;43mTODO:\\e[0m"
    echo -e "\t \t      - Checking SAT TBox and ABox LTL pure future. Currently only works for PTLT and NuSMV"
    echo -e "\t \t      - Checking SAT TBox using NuSMV should not use Gario's translation. Currently it keeps Y operator"
    echo -e "\t \t      - Add more solvers: pltl (tableaux-based) | TRP++ (temporal resolution)"
    exit
fi




echo "Current example directory: "
ls -l $1

echo "Do you want to continue? (Y|N)"
read yn

if [ $yn == "N" -o $yn == "n" ];
then
    echo -e "\\e[0;42mCiao!\\e[0m"
    exit
fi

    tboxfile="${1}tbox.json"

    if [ ! -f "$tboxfile" ];
    then
        echo -e "\\e[0;41mTBox not found. Exit!\\e[0m"
        exit
    fi

if [ $2 -eq 1 ];
then
    echo -e "\\e[0;41mSolver selected: NuSMV\\e[0m"

    aboxfile="${1}abox.json"

    if [ -f "$aboxfile" ];
    then
    
        echo -e "\\e[0;42mPlease, enter true|false to give a pure future or a pltl formulae to NuSMV\\e[0m"

        read purefuture

        if [ $purefuture = true ];
        then
            echo -e "\\e[0;48mThis option has not been yet developed. Exit!\\e[0m"
            exit
                #java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxABoxSatNuSMV -t "${1}tbox.json" -a "${1}abox.json" -pf

         else
             if [ $purefuture = false ];
             then
                java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxABoxSatNuSMV -t "${1}tbox.json" -a "${1}abox.json"
             else
                 echo "You must enter true|false"
                 exit
             fi
         fi

    else
        echo -e "\\e[0;42mPlease, enter true|false to give a pure future or a pltl formulae to NuSMV\\e[0m"

        read purefuture

        if [ $purefuture = true ];
        then
            echo -e "\\e[0;48mpure future formulae\\e[0m"
            java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxSat -t "${1}tbox.json" -q "${1}query.txt" -pf -s NuSMV
        
        else
            if [ $purefuture = false ];
            then
                echo -e "\\e[0;48mPLTL formulae\\e[0m"
                java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxSat -t "${1}tbox.json" -q "${1}query.txt" -s NuSMV
            else 
                echo "You must enter true|false"
                exit
            fi
        fi
    fi


    file="${1}tcrowdOut.smv"
    if [ -f "$file" ];
    then
	    ./solvers/NuSMV/NuSMV -dcx -bmc -bmc_length 11 "${1}tcrowdOut.smv"
    else
	    echo "$file not found."
        exit
    fi

######### if solver is Aalta

else
    if [ $2 -eq 2 ];
    then
        echo -e "\\e[0;42mSolver selected: Aalta v2.0\\e[0m"

        aboxfile="${1}abox.json"

        if [ -f "$aboxfile" ];
        then
            echo -e "\\e[0;48mThis option has not been yet developed. Exit!\\e[0m"
            exit

        #java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxSat -t "${1}tbox.json" -q "${1}query.txt" -pf -s Aalta
        else
           java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxSat -t "${1}tbox.json" -q "${1}query.txt" -pf -s Aalta 
        fi

        file="${1}tcrowdOut.aalta"
        if [ -f "$file" ];
        then
	        ./solvers/Aalta/aalta cat "${1}tcrowdOut.aalta"

        else
	        echo "$file not found."
            exit
        fi
    else
       echo "Invalid solver."
       exit
    fi    
fi

