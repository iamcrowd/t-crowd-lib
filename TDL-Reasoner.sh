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
    echo -e "KB = <TBox, {}> and (NuSMV|Aalta|pltl|TRP++UC) solver. LTL (TBox -> QTL1 -> QTLN -> LTL) \\e[0;42mis working BUT fails if TBox does not include roles\\e[0m"
    echo ""
    echo -e "KB = <TBox, ABox> and NuSMV solver. PLTL (TBox|ABox -> QTL1 -> PLTL) \\e[0;42mis working\\e[0m"
    echo ""
    echo -e "KB = <TBox, ABox> and (NuSMV|Aalta|pltl|TRP++UC) solver. LTL (TBox|ABox -> QTL1 -> QTLN -> LTL) \\e[0;43m is working \\e[0m"
    echo ""
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
    echo -e "\t - 1: NuSMV solver (model checking reduction - pure future or past formulae)"
    echo -e "\t - 2: Aalta solver (model checking reduction - pure future formulae)"
    echo -e "\t - 3: pltl solver (tableaux - pure future formulae)"
    echo -e "\t - 4: TRP++UC solver (temporal resolution - pure future formulae)"
    echo -e "\t - 5: NuXMV solver (temporal resolution - pure future formulae)"
    
    echo -e "\t - solver number is required"
    echo ""
    echo -e "\\e[0;42mExamples of use:\\e[0m"
    echo ""
    echo -e "\t \t (a) example NuSMV: './TDL-Reasoner.bash examples/adultWithABox/ 1'" 
    echo -e "\t \t (b) example Aalta: './TDL-Reasoner.bash examples/adultWithABox/ 2'"
    echo -e "\t \t (b) example pltl: './TDL-Reasoner.bash examples/adultWithABox/ 3'"
    echo -e "\t \t (b) example TRP++UC: './TDL-Reasoner.bash examples/adultWithABox/ 4'"
    echo -e "\t \t (b) example NuXMV: './TDL-Reasoner.bash examples/adultWithABox/ 5'"  
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
    exit
fi


#Remove previous files from example folder

find . -name "*.smv" -type f -delete
find . -name "*.aalta" -type f -delete
find . -name "*.tex" -type f -delete
find . -name "*.pltl" -type f -delete
find . -name "*.ltl" -type f -delete


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
       echo -e "\\e[0;42mTo be checked - THIS IS AN EXPERIMENTAL FEATURE!\\e[0m"
       #exit
       echo "Do you want to continue? (Y|N)"
       read yesno

       if [ $yesno == "Y" -o $yesno == "y" ];
       then
            java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ERvtTBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s NuSMV
       else
            exit
       fi 

    else
       if [ $purefuture = false ];
       then
          java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ERvtTBoxABoxSatPLTL -t "${1}tbox.json" -a "${1}abox.json"
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
           
        java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ERvtTBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s Aalta 

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


######### if solver is pltl
        if [ $2 -eq 3 ];
        then
            echo -e "\\e[0;42mSolver selected: pltl\\e[0m"
           
            java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ERvtTBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s pltl

		    file="${1}tcrowdOut.pltl"
    
    	    if [ -f "$file" ];
    	    then
    		    cat "${1}tcrowdOut.pltl"
	  		    ./solvers/pltl/pltl/pltl < $file

    	    else
	    	    echo "$file not found."
        	    exit
    	    fi
   	    else
######### if solver is TRP++UC
            if [ $2 -eq 4 ];
            then
                echo -e "\\e[0;42mSolver selected: TRP++UC\\e[0m"
           
                java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ERvtTBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s TRP++UC

		        file="${1}tcrowdOut.ltl"
    
    	        if [ -f "$file" ];
    	        then
    		        cat "${1}tcrowdOut.ltl"
	  		        ./solvers/TRP++UC/trp++uc -f ltl $file

    	        else
	    	        echo "$file not found."
        	        exit
    	        fi
   	        else
   	        	if [ $2 -eq 5 ];
   	        	then
   	        		echo -e "\\e[0;42mSolver selected: NuXMV\\e[0m"
   	        		java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd ERvtTBoxABoxSatLTL -t "${1}tbox.json" -a "${1}abox.json" -s NuSMV
   	        		
   	        		file="${1}tcrowdOut.smv"
    				if [ -f "$file" ];
    				then
	    				./solvers/NuXMV/NuXMV -dcx -bmc -bmc_length 11 "${1}tcrowdOut.smv"
    				else
	    				echo "$file not found."
        				exit
    				fi
   	        	else
                	echo "Invalid solver."
                	exit
                fi
            fi
        fi
    fi    
fi

