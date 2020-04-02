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

if [ $1 == "status" ];
then
    echo -e "\t \t \\e[0;43m**Status**\\e[0m"
    echo ""
    echo -e "KB = <TBox, {}> and NuSMV solver. PLTL (TBox -> QTL1 -> PLTL) \\e[0;42mis working BUT fails if TBox does not include roles\\e[0m"
    echo ""
    echo -e "KB = <TBox, {}> and NuSMV solver. LTL (TBox -> QTL1 -> QTLN -> LTL) \\e[0;42mis working BUT fails if TBox does not include roles\\e[0m"
    echo ""
    echo -e "KB = <TBox, ABox> and NuSMV solver. PLTL (TBox|ABox -> QTL1 -> PLTL) \\e[0;42mis working\\e[0m"
    echo ""
    echo -e "KB = <TBox, ABox> and NuSMV solver. LTL (TBox|ABox -> QTL1 -> QTLN -> LTL) \\e[0;43mto be implemented\\e[0m"
    echo ""
    echo -e "KB = <TBox, {}> and Aalta solver. LTL (TBox -> QTL1 -> QTLN -> LTL) \\e[0;42mis working BUT fails if TBox does not include roles\\e[0m"
    echo ""
    echo -e "KB = <TBox, ABox> and Aalta solver. LTL (TBox|ABox -> QTL1 -> QTLN -> LTL) \\e[0;43mto be implemented\\e[0m"
    echo ""
    echo -e "KB = <TBox, ABox> and <pure future> solver. LTL (TBox|ABox -> QTL1 -> PLTL -> LTL) \\e[0;43mto be implemented\\e[0m"
    exit
fi

if [ $1 == "help" ];
then
    echo -e "\t \t \\e[0;43m**Help me to run**\\e[0m"
    echo ""
    echo -e "\\e[0;46mcommand:\\e[0m ./TDL-Reasoner.bash [example_folder] [solver number]"
    echo ""
    echo -e "[example_folder]: " 
    echo -e "\t - (required, not empty) it must contain a TBox file named 'tbox.json'"
    echo -e "\t - (required) it could contain ABox file named 'abox.json'"
#    echo -e "\t - (optional) it could contain a Query file named 'query.txt'"
    echo ""
    echo -e "[solver number]: "
    echo -e "\t - 1: NuSMV solver (model checking reduction - pure future or ptlt formulae)"
    echo -e "\t - 2: Aalta solver (model checking reduction - pure future formulae)"
    echo -e "\t - solver number is required"
    echo ""
    echo -e "\\e[0;42mExamples of use:\\e[0m"
    echo ""
    echo -e "\t \t (a) example NuSMV: './TDL-Reasoner.bash examples/adultWithABox/ 1'" 
    echo -e "\t \t (b) example Aalta: './TDL-Reasoner.bash examples/adultWithABox/ 2'"  
    echo "" 
    echo -e "\t - Users are required to enter 'true' or 'false' to choose using" 
    echo -e "\t only future operators or past operators"
    echo ""
    echo -e "\t \t - 'true': \t TDL-Lite -> QTL1 -> QTL Pure Future -> LTL"
    echo -e "\t \t - 'false': \t TDL-Lite -> QLT1 -> PLTL"
    echo ""
    echo -e "\\e[0;43mUpdated TODO:\\e[0m"
    echo ""
    echo -e "\t - BUG 1: Checking SAT TBox and Empty ABox if TBox does not include roles outs pointer null"
    echo ""
    echo -e "\t - We have to review the NuXMV options. Currently, dynamic "
    echo -e "\t - Add more solvers: pltl (tableaux-based) | TRP++ (temporal resolution)"
    exit
fi


#Remove previous files from example folder

find . -name "*.smv" -type f -delete
find . -name "*.aalta" -type f -delete
find . -name "*.tex" -type f -delete

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
    
    aboxfile="${1}abox.json"

    if [ ! -f "$aboxfile" ];
    then
        echo -e "\\e[0;41mABox not found. Exit!\\e[0m"
        exit
    fi
    

### if solver is NuSMV

if [ $2 -eq 1 ];
then
    echo -e "\\e[0;41mSolver selected: NuSMV\\e[0m"

    echo -e "\\e[0;42mPlease, enter true|false to give a pure future (true) or a pltl (false) formulae to NuSMV\\e[0m"

    read purefuture

    if [ $purefuture = true ];
    then
       echo -e "\\e[0;42mTo be checked\\e[0m"
       exit
       #java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s NuSMV 

    else
       if [ $purefuture = false ];
       then
          java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxABoxSatPLTL -t "${1}tbox.json" -a "${1}abox.json"
       else
    	   echo "You must enter true|false"
           exit
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
           
        java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd TBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s Aalta 

		file="${1}tcrowdOut.aalta"
    
    	if [ -f "$file" ];
    	then
    		cat "${1}tcrowdOut.aalta"
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

