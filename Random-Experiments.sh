#!/bin/bash

# @parameter1: main folder to put the results.
# @parameter2: experiment configuration file

# @return: one new folder will be created inside of the main folder with the respective results of each execution.

# combinations 2400

# amount 100 tbox
# values 24

# size: 5, 10, 15
# Lc: 5, 10, 15 (Lc its 11 values)
# N: 1, 3, 5
# qm: 2, 5
# pr: 0.1, 0.3, 0.7, 0.9
# pt: 0.5, 0.7, 0.9

#SAT using each solver
#time and result
#time for each execution and the final time


#TODO
# 1 - Check for float numbers for probabilities in java
# 2 - Add other reasoners for purefuture
# 3 - See reporting


if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
    exit
fi

   	mainfolder="${1}"

    if [ ! -d "$mainfolder" ];
    then
        echo -e "\\e[0;41m $mainfolder does not exist in your filesystem. Exit!\\e[0m"
        exit
    else
        echo -e "Current directory: $mainfolder"
    fi

    if ls ${1}*.config &>/dev/null
    then
        echo "Found."
        configfile=${1}*.config
        echo $configfile
    else
        echo "Not found."
    fi
    
    i=1
    while IFS= read -r line
    do
        if [[ $i =~ 1 ]];
        then
            size=($line)
        fi

        if [[ $i =~ 2 ]];
        then
            lc=($line)
        fi

        if [[ $i =~ 3 ]];
        then
            N=($line)
        fi

        if [[ $i =~ 4 ]];
        then
            qm=($line)
        fi

        if [[ $i =~ 5 ]];
        then
            pr=($line)
        fi

        if [[ $i =~ 6 ]];
        then
            pt=($line)
        fi

        i=`expr $i + 1`
    done < $configfile

    

    for j in "${size[@]}"  # Size
    do
        for k in "${lc[@]}" # Lc
        do
            for l in "${N[@]}" # N
            do
                for m in "${qm[@]}" # Qm
                do
                    for p in "${pr[@]}" # Pr
                    do
                        for q in "${pt[0]}" #Pt
                        do

                            echo -e "Random parameters-> size: ${j} - lc: ${k} - N: ${l} - Qm: ${m} - Pr: ${p} - Pt: ${q}"
                            
                  ########### NuSMV
                            # New folder to copy the experiment result for this instance
                            
                            namefolder="NuSMV_e_${j}_${k}_${l}_${m}_${p}_${q}"
                            mkdir $mainfolder$namefolder

                            touch $mainfolder$namefolder/abox.json
                            touch $mainfolder$namefolder/outputTDLReasoner.txt    
                            touch $mainfolder$namefolder/outputSATsolver.txt

                            outputTDLfile="$mainfolder$namefolder/outputTDLReasoner.txt"    
                            
                            # Invoking TDL-Reasoner with the random parameters, empty abox. The output is logged in a txt file named 'outputTDLReasoner.txt'
                            java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox $j -n $l -lc $k -qm $m -pt $p -pr $q -a "$mainfolder$namefolder/abox.json" -s NuSMV > $outputTDLfile

                            file="$mainfolder$namefolder/random.smv"
                            outputSolverfile="$mainfolder$namefolder/outputSATsolver.txt"
    
                            # Run the NuSVM solver. NuSMV flags should be set here. The output is copied to a txt file named 'outputSAT.txt'
                            if [ -f "$file" ];
   	                        then
                                echo -e "NuSMV"
	                            ./solvers/NuSMV/NuSMV -dcx -bmc -bmc_length 11 $file > $outputSolverfile
                            else
	                            echo "$file not found."
                                exit
                            fi

                  ########### Aalta
                            # New folder to copy the experiment result for this instance
                            
                            namefolder="Aalta_e_${j}_${k}_${l}_${m}_${p}_${q}"
                            mkdir $mainfolder$namefolder

                            touch $mainfolder$namefolder/abox.json
                            touch $mainfolder$namefolder/outputTDLReasoner.txt    
                            touch $mainfolder$namefolder/outputSATsolver.txt

                            outputTDLfile="$mainfolder$namefolder/outputTDLReasoner.txt"    
                            
                            # Invoking TDL-Reasoner with the random parameters, empty abox. The output is logged in a txt file named 'outputTDLReasoner.txt'
                            java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox $j -n $l -lc $k -qm $m -pt $p -pr $q -a "$mainfolder$namefolder/abox.json" -s Aalta > $outputTDLfile

                            file="$mainfolder$namefolder/random.aalta"
                            outputSolverfile="$mainfolder$namefolder/outputSATsolver.txt"
    
                            # Run the NuSVM solver. NuSMV flags should be set here. The output is copied to a txt file named 'outputSAT.txt'
                            if [ -f "$file" ];
   	                        then
                                echo -e "Aalta"
	                            ./solvers/Aalta/aalta cat $file > $outputSolverfile
                            else
	                            echo "$file not found."
                                exit
                            fi

                        done
                    done
                done
            done
        done
    done


