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
echo ""

if [ $# -eq 0 ]
  then
    echo "No arguments supplied. Enter './TDL-Reasoner-RandomTKB.sh help'"
    exit
fi

if [ $1 == "status" ];
then
    echo -e "\t \t \\e[0;43m**Status**\\e[0m"
    echo ""
    echo -e "KB = <RandomTBox, {}> and NuSMV. PLTL (RandomTBox -> QTL1 -> PLTL) \\e[0;42mis working BUT fails if TBox does not include roles\\e[0m"
    echo ""
    echo -e "KB = <RandomTBox, {}> and (NuSMV|Aalta|pltl|TRP++UC). LTL (RandomTBox -> QTL1 -> QTLN -> LTL) \\e[0;42mis working BUT fails if TBox does not include roles\\e[0m"
    echo ""
    echo -e "KB = <RandomTBox, ABox> and NuSMV. PLTL (RandomTBox|ABox -> QTL1 -> PLTL) \\e[0;42mis working\\e[0m"
    echo ""
    echo -e "KB = <RandomTBox, ABox> and (NuSMV|Aalta|pltl|TRP++UC). LTL (RandomTBox|ABox -> QTL1 -> QTLN -> LTL) \\e[0;43m is working \\e[0m"
    echo ""
    exit
fi

if [ $1 == "help" ];
then
    echo -e "\t \t \\e[0;43m**Help me to run**\\e[0m"
    echo ""
    echo -e "\\e[0;46mcommand:\\e[0m ./TDL-Reasoner-RandomTKB.sh [random parameters] [abox folder] [solver number]"
	echo ""
	echo -e "[random parameters]: " 
	echo -e "\t 'ltbox': Number of Concept Inclusions (integer)"
    echo -e "\t 'n': Number of Concepts (integer)"
    echo -e "\t 'lc': Length of each Concept (integer)"
    echo -e "\t 'qm': Maximum Cardinality of Qualified Roles (integer)"
    echo -e "\t 'pt': Probability of generating Temporal Concepts (integer)"
    echo -e "\t 'pr': Probability of generating Rigid Roles (integer)"
    echo ""
    echo -e "[abox folder]: " 
    echo -e "\t - (required) it could contain ABox file named 'abox.json'. If ABox file is empty, only TBox sat is checked"
    echo ""
    echo -e "[solver number]: "
    echo -e "\t - 1: NuSMV solver (model checking reduction - pure future or past formulae)"
    echo -e "\t - 2: Aalta solver (model checking reduction - pure future formulae)"
    echo -e "\t - 3: pltl solver (tableaux - pure future formulae)"
    echo -e "\t - 4: TRP++UC solver (temporal resolution - pure future formulae)"
    echo -e "\t - 5: NuXMV solver (temporal resolution - pure future or past formulae)"
    
    echo -e "\t - solver number is required"
    echo ""
    echo -e "\\e[0;42mExamples of use:\\e[0m"
    echo ""
    echo -e "\t \t (a) example NuSMV: './TDL-Reasoner-RandomTKB.sh 4 2 4 2 5 5 examples/EmptyABoxforRandom/ 1'" 
    echo -e "\t \t (b) example Aalta: './TDL-Reasoner-RandomTKB.sh 4 2 4 2 5 5 examples/EmptyABoxforRandom/ 2'"
    echo -e "\t \t (b) example pltl: './TDL-Reasoner-RandomTKB.sh 4 2 4 2 5 5 examples/EmptyABoxforRandom/ 3'"
    echo -e "\t \t (b) example TRP++UC: './TDL-Reasoner-RandomTKB.sh 4 2 4 2 5 5 examples/EmptyABoxforRandom/ 4'"
    echo -e "\t \t (b) example NuXMV: './TDL-Reasoner-RandomTKB.sh 4 2 4 2 5 5 examples/EmptyABoxforRandom/ 5'"  
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

	re='^[0-9]+$'
	
	if ! [[ $1 =~ $re ]]; 
	then
   		echo "ltbox is not a number"; 
   		exit 1
	fi
	
	if ! [[ $2 =~ $re ]]; 
	then
   		echo "n is not a number"; 
   		exit 1
	fi
	
	if ! [[ $3 =~ $re ]]; 
	then
   		echo "lc is not a number"; 
   		exit 1
	fi
	
	if ! [[ $4 =~ $re ]]; 
	then
   		echo "qm is not a number"; 
   		exit 1
	fi
	
	if ! [[ $5 =~ $re ]]; 
	then
   		echo "pt is not a number"; 
   		exit 1
	fi
	
	if ! [[ $6 =~ $re ]]; 
	then
   		echo "pr is not a number"; 
   		exit 1
	fi    

    aboxfile="${7}abox.json"

    if [ ! -f "$aboxfile" ];
    then
        echo -e "\\e[0;41mABox not found. Exit!\\e[0m"
        exit
    fi
    

### if solver is NuSMV

if [ $8 -eq 1 ];
then
    echo -e "\\e[0;41mSolver selected: NuSMV\\e[0m"
    echo -e "\\e[0;42mPlease, enter true|false to give a pure future (true) or a pltl (false) formulae to NuSMV\\e[0m"
    read purefuture

    if [ $purefuture = true ];
    then
      	java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json" -s NuSMV
    else
       if [ $purefuture = false ];
       then
          java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatPLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json"
       else
    	   echo "You must enter true|false"
           exit
       fi
    fi

    	file="${7}random.smv"
    	if [ -f "$file" ];
    	then
            if [[ "$OSTYPE" == "linux-gnu" ]]; then
            
                ./solvers/NuSMV/NuSMV -dcx -bmc -bmc_length 11 "${7}random.smv"
        
            elif [[ "$OSTYPE" == "darwin"* ]]; then  # Mac OSX

                ./solvers/NuSMV-macos/NuSMV -dcx -bmc -bmc_length 11 "${7}random.smv"

            elif [[ "$OSTYPE" == "cygwin" ]]; then #Windows

	            /solvers/NuSMV-win/NuSMV.exe -dcx -bmc -bmc_length 11 "${7}random.smv"
            fi
    	else
	    	echo "$file not found."
        	exit
    	fi
else
######### if solver is NuXMV
	if [ $8 -eq 5 ];
	then
    	echo -e "\\e[0;41mSolver selected: NuXMV\\e[0m"

    	echo -e "\\e[0;42mPlease, enter true|false to give a pure future (true) or a pltl (false) formulae to NuXMV\\e[0m"

    	read purefuture

    	if [ $purefuture = true ];
    	then
      		java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json" -s NuSMV
    	else
       		if [ $purefuture = false ];
       		then
          		java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatPLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json"
       		else
    	  		echo "You must enter true|false"
           		exit
       		fi
    	fi

    	file="${7}random.smv"
    	if [ -f "$file" ];
   	 	then
	    	./solvers/NuXMV/nuXmv -dcx -bmc -bmc_length 11 "${7}random.smv"
    	else
	    	echo "$file not found."
        	exit
    	fi
######### if solver is Aalta

	else
    	if [ $8 -eq 2 ];
    	then
        	echo -e "\\e[0;42mSolver selected: Aalta v2.0\\e[0m"
           
        	java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json" -s Aalta 

			file="${7}random.aalta"
    
    		if [ -f "$file" ];
    		then
    			cat "${7}random.aalta"
	  			./solvers/Aalta/aalta cat "${7}random.aalta"

    		else
	    		echo "$file not found."
        		exit
    		fi
   		else

######### if solver is pltl
        	if [ $8 -eq 3 ];
        	then
            	echo -e "\\e[0;42mSolver selected: pltl\\e[0m"
           
            	java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json" -s pltl

		    	file="${7}random.pltl"
    
    	    	if [ -f "$file" ];
    	    	then
    		    	cat "${7}random.pltl"
	  		    	./solvers/pltl/pltl/pltl < $file

    	    	else
	    	    	echo "$file not found."
        	    	exit
    	    	fi
   	    	else
######### if solver is TRP++UC
            	if [ $8 -eq 4 ];
            	then
                	echo -e "\\e[0;42mSolver selected: TRP++UC\\e[0m"
           
                	java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox ${1} -n ${2} -lc ${3} -qm ${4} -pt ${5} -pr ${6} -a "${7}abox.json" -s TRP++UC

		        	file="${7}random.ltl"
    
    	        	if [ -f "$file" ];
    	        	then
    		        	cat "${7}random.ltl"
	  		        	./solvers/TRP++UC/trp++uc -f ltl $file

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

