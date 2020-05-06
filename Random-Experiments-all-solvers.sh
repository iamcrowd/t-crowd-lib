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

    # number of iterations

	re='^[0-9]+$'
	
	if ! [[ $2 =~ $re ]]; 
	then
   		echo "Number of iterations must be given"; 
   		exit 1
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

    
    echo -e "***********************NuSMV NuXMV (bmc, bdd) |aalta|pltl (bdd,graph,tree)|trp++uc (graph,tree) solver"
    echo -e "\n \n"

    ########## Total time
    wholeteststart=$(($(date +%s%N)/1000000))
    echo -e `date`
    ########## Total time

    pfltlteststart=$(($(date +%s%N)/1000000))


    ### Reporting

    namefile="result.csv"
    touch $mainfolder/$namefile
    echo "name,size,lc,n,qm,pr,pt,tbox2qtl(s),abox2qtl(s),qtl2qtln(s),abox2qtln(s),qtl2ltl(s),qtln2ltl(s),trtotal(s),sattime(s),total(s),#prop,sat/unsat" >> $mainfolder/$namefile

    ####


    # random for iteration
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

                          # numbers of iterations

                            counter=1
                            sum_iter=0

                            while [ "$counter" -le "$2" ]; 
                            do

                                echo -e "Iteration number: $counter"
                                echo -e ""
                                echo -e ""
                            
                                namefolder_nusmvBMC="NuSMV_BMC_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_nusmvBDD="NuSMV_BDD_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_nuxmvBMC="NuXMV_BMC_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_nuxmvBDD="NuXMV_BDD_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_aalta="Aalta_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_pltlBDD="pltlBDD_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_pltlgraph="pltlgraph_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_pltltree="pltltree_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_trpDFS="trpDFS_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                                namefolder_trpBFS="trpBFS_solver_ltl_iter_${counter}_param_${j}_${k}_${l}_${m}_${p}_${q}"
                            
                                mkdir $mainfolder$namefolder_nusmvBMC
                                mkdir $mainfolder$namefolder_nusmvBDD
                                mkdir $mainfolder$namefolder_nuxmvBMC
                                mkdir $mainfolder$namefolder_nuxmvBDD
                                mkdir $mainfolder$namefolder_aalta
                                mkdir $mainfolder$namefolder_pltlBDD
                                mkdir $mainfolder$namefolder_pltlgraph
                                mkdir $mainfolder$namefolder_pltltree
                                mkdir $mainfolder$namefolder_trpDFS
                                mkdir $mainfolder$namefolder_trpBFS

                                touch $mainfolder/abox.json
                                touch $mainfolder/outputTDLReasoner.txt
                                outputTDLfile="$mainfolder/outputTDLReasoner.txt" 

                                        
                                        echo -e "------------------------"
                                        echo -e "Random parameters-> size: ${j} - lc: ${k} - N: ${l} - Qm: ${m} - Pr: ${p} - Pt: ${q}"

                                        iterationstart=$(($(date +%s%N)/1000000))


                            # Invoking TDL-Reasoner with the random parameters, empty abox. The output is logged in a txt file named 'outputTDLReasoner.txt'
                                java -cp t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd RandomTBoxABoxSatLTL -ltbox $j -n $l -lc $k -qm $m -pt $p -pr $q -a "$mainfolder/abox.json" -s all > $outputTDLfile

                                        translationend=$(($(date +%s%N)/1000000))
                                        translationtotal=$((translationend-iterationstart))  

                                cp $mainfolder/random.smv $mainfolder$namefolder_nusmvBDD
                                cp $mainfolder/random.smv $mainfolder$namefolder_nusmvBMC
                                cp $mainfolder/random.smv $mainfolder$namefolder_nuxmvBDD
                                cp $mainfolder/random.smv $mainfolder$namefolder_nuxmvBMC
                                cp $mainfolder/random.aalta $mainfolder$namefolder_aalta
                                cp $mainfolder/random.pltl $mainfolder$namefolder_pltlBDD
                                cp $mainfolder/random.pltl $mainfolder$namefolder_pltlgraph
                                cp $mainfolder/random.pltl $mainfolder$namefolder_pltlgraph
                                cp $mainfolder/random.ltl $mainfolder$namefolder_trpDFS
                                cp $mainfolder/random.ltl $mainfolder$namefolder_trpBFS

                                cp $mainfolder/*.stats $mainfolder$namefolder_nusmvBDD
                                cp $mainfolder/*.stats $mainfolder$namefolder_nusmvBMC
                                cp $mainfolder/*.stats $mainfolder$namefolder_nuxmvBDD
                                cp $mainfolder/*.stats $mainfolder$namefolder_nuxmvBMC
                                cp $mainfolder/*.stats $mainfolder$namefolder_aalta
                                cp $mainfolder/*.stats $mainfolder$namefolder_pltlBDD
                                cp $mainfolder/*.stats $mainfolder$namefolder_pltlgraph
                                cp $mainfolder/*.stats $mainfolder$namefolder_pltlgraph
                                cp $mainfolder/*.stats $mainfolder$namefolder_trpDFS
                                cp $mainfolder/*.stats $mainfolder$namefolder_trpBFS

                                touch $mainfolder$namefolder_nusmvBDD/outputSATsolver.txt
                                outputSolverfile_nusmvBDD="$mainfolder$namefolder_nusmvBDD/outputSATsolver.txt"

                                touch $mainfolder$namefolder_nusmvBMC/outputSATsolver.txt
                                outputSolverfile_nusmvBMC="$mainfolder$namefolder_nusmvBMC/outputSATsolver.txt"

                                touch $mainfolder$namefolder_nuxmvBMC/outputSATsolver.txt
                                outputSolverfile_nuxmvBMC="$mainfolder$namefolder_nuxmvBMC/outputSATsolver.txt"

                                touch $mainfolder$namefolder_nuxmvBDD/outputSATsolver.txt
                                outputSolverfile_nuxmvBDD="$mainfolder$namefolder_nuxmvBDD/outputSATsolver.txt"

                                touch $mainfolder$namefolder_aalta/outputSATsolver.txt
                                outputSolverfile_aalta="$mainfolder$namefolder_aalta/outputSATsolver.txt"

                                touch $mainfolder$namefolder_pltlBDD/outputSATsolver.txt
                                outputSolverfile_pltlBDD="$mainfolder$namefolder_pltlBDD/outputSATsolver.txt"

                                touch $mainfolder$namefolder_pltlgraph/outputSATsolver.txt
                                outputSolverfile_pltlgraph="$mainfolder$namefolder_pltlgraph/outputSATsolver.txt"

                                touch $mainfolder$namefolder_pltltree/outputSATsolver.txt
                                outputSolverfile_pltltree="$mainfolder$namefolder_pltltree/outputSATsolver.txt"

                                touch $mainfolder$namefolder_trpDFS/outputSATsolver.txt
                                outputSolverfile_trpDFS="$mainfolder$namefolder_trpDFS/outputSATsolver.txt"

                                touch $mainfolder$namefolder_trpBFS/outputSATsolver.txt
                                outputSolverfile_trpBFS="$mainfolder$namefolder_trpBFS/outputSATsolver.txt" 

                                        #Generate input for reporting

                                        # Reading stats file from library
                                        if ls $mainfolder/*.stats &>/dev/null
                                        then
                                            echo "Found."
                                            statsfile=$mainfolder/*.stats

                                            i=1
                                            while IFS= read -r line
                                            do
                                                if [[ $i =~ 1 ]];
                                                then
                                                    tbox2qlt1_line=($line)
                                                    idx=`expr ${#tbox2qlt1_line[@]} - 1`
                                                    tbox2qlt1_ms=${tbox2qlt1_line[$idx]}
                                                    tbox2qlt1_s=$(echo "scale=4;$tbox2qlt1_ms / 1000" | bc -l)
                                                fi

                                                if [[ $i =~ 2 ]];
                                                then
                                                    qtl12qtln_line=($line)
                                                    idx=`expr ${#qtl12qtln_line[@]} - 1`
                                                    qtl12qtln_ms=${qtl12qtln_line[$idx]}
                                                    qtl12qtln_s=$(echo "scale=4;$qtl12qtln_ms / 1000" | bc -l)
                                                fi

                                                if [[ $i =~ 3 ]];
                                                then
                                                    qtlN2ltl_line=($line)
                                                    idx=`expr ${#qtlN2ltl_line[@]} - 1`
                                                    qtlN2ltl_ms=${qtlN2ltl_line[$idx]}
                                                    qtlN2ltl_s=$(echo "scale=4;$qtlN2ltl_ms / 1000" | bc -l)
                                                fi

                                                if [[ $i =~ 4 ]];
                                                then
                                                    nprop_line=($line)
                                                    idx=`expr ${#nprop_line[@]} - 1 `
                                                    nprop=${nprop_line[$idx]}
                                                fi

                                                i=`expr $i + 1`
                                            done < $statsfile
                                         fi


                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/NuSMV/NuSMV -dcx -bmc -bmc_length 11 $file > $outputSolverfile_nusmvBMC
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "false" $outputSolverfile_nusmvBMC
                                  then
                                      sat=`echo "false"`
                                  else
                                        if grep -q "true" $outputSolverfile_nusmvBMC
                                        then
                                            sat=`echo "true"`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile



                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/NuSMV/NuSMV -dcx -dynamic $file > $outputSolverfile_nusmvBDD
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "false" $outputSolverfile_nusmvBDD
                                  then
                                      sat=`echo "false"`
                                  else
                                        if grep -q "true" $outputSolverfile_nusmvBDD
                                        then
                                            sat=`echo "true"`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile


                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/NuXMV/nuXmv -dcx -bmc -bmc_length 11 $file > $outputSolverfile_nuxmvBMC
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "false" $outputSolverfile_nuxmvBMC
                                  then
                                      sat=`echo "false"`
                                  else
                                        if grep -q "true" $outputSolverfile_nuxmvBMC
                                        then
                                            sat=`echo "true"`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile

                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/NuXMV/nuXmv -dcx -dynamic $file > $outputSolverfile_nuxmvBDD
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "false" $outputSolverfile_nuxmvBDD
                                  then
                                      sat=`echo "false"`
                                  else
                                        if grep -q "true" $outputSolverfile_nuxmvBDD
                                        then
                                            sat=`echo "true"`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile


                                  solverstart=$(($(date +%s%N)/1000000))
                                 ./solvers/Aalta/aalta cat $file > $outputSolverfile_aalta
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "sat" $outputSolverfile_aalta
                                  then
                                      sat=`echo sat`
                                  else
                                        if grep -q "unsat" $outputSolverfile_aalta
                                        then
                                            sat=`echo unsat`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile

                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/pltl/pltl/bddpltl -sat -silent < $file > $outputSolverfile_pltlBDD
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "satisfiable" $outputSolverfile_pltlBDD
                                  then
                                      sat=`echo sat`
                                  else
                                        if grep -q "unsatisfiable" $outputSolverfile_pltlBDD
                                        then
                                            sat=`echo unsat`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile

                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/pltl/pltl/pltl graph < $file > $outputSolverfile_pltlgraph
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "satisfiable" $outputSolverfile
                                  then
                                      sat=`echo sat`
                                  else
                                        if grep -q "unsatisfiable" $outputSolverfile_pltlgraph
                                        then
                                            sat=`echo unsat`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile

                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/pltl/pltl/pltl tree < $file > $outputSolverfile_pltltree
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "satisfiable" $outputSolverfile_pltltree
                                  then
                                      sat=`echo sat`
                                  else
                                        if grep -q "unsatisfiable" $outputSolverfile_pltltree
                                        then
                                            sat=`echo unsat`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile

                                  solverstart=$(($(date +%s%N)/1000000))
                                  ./solvers/TRP++UC/trp++uc -f ltl $file > $outputSolverfile_trpDFS
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "Satisfiable" $outputSolverfile_trpDFS
                                  then
                                      sat=`echo sat`
                                  else
                                        if grep -q "Unsatisfiable" $outputSolverfile_trpDFS
                                        then
                                            sat=`echo unsat`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile

                                  solverstart=$(($(date +%s%N)/1000000))
                                 ./solvers/TRP++UC/trp++uc -BFS -f ltl $file > $outputSolverfile_trpBFS
                                  solverend=$(($(date +%s%N)/1000000))
                                  solvertotal=$((solverend-solverstart))

                                  if grep -q "Satisfiable" $outputSolverfile_trpBFS
                                  then
                                      sat=`echo sat`
                                  else
                                        if grep -q "Unsatisfiable" $outputSolverfile_trpBFS
                                        then
                                            sat=`echo unsat`
                                        else
                                            sat=`echo undefined`
                                        fi
                                  fi

                                           # Time to seconds

                                            translationtotal_ms=$(($tbox2qlt1_ms+$qtl12qtln_ms+$qtlN2ltl_ms))
                                            translationtotal_s=$(echo "scale=4;$translationtotal_ms / 1000" | bc -l)

                                            solvertotals=$(echo "scale=4;$solvertotal / 1000" | bc -l)

                                            total_ms=$(($solvertotal+$translationtotal_ms))
                                            totals=$(echo "scale=4;$total_ms / 1000" | bc -l)

                                        echo -e "${namefolder},${j},${k},${l},${m},${p},${q},${tbox2qlt1_s},0,${qtl12qtln_s},0,0,${qtlN2ltl_s},${translationtotal_s},${solvertotals},${totals},${nprop},${sat}" >> $mainfolder/$namefile


                                        ####

                                        # for avg 
                                        #    sum_iter=$(($sum_iter + $total_ms))

                                        # next iteration

                                   counter=$(($counter + 1))
                            done 
                        done
                    done
                done
            done
        done
    done

    pfltltestend=$(($(date +%s%N)/1000000))

    pfwholetestruntime=$((pfltltestend-pfltlteststart))

    echo -e ""
    echo -e ""

    ########## Total time
    wholetestend=$(($(date +%s%N)/1000000))
    echo `date`

    wholetestruntime=$((wholetestend-wholeteststart))

    if [[ $wholetestruntime -gt 1000 ]]
    then
        echo -e "\\e[0;42mComplete Test time (s):\\e[0m"
        echo "scale=2; $wholetestruntime / 1000" | bc -l
    else
        echo -e "\\e[0;42mComplete Test time (ms):\\e[0m" $wholetestruntime
    fi


#end while

                            # avg for iteration
                                
                           # avgs=$(echo "scale=4;$sum_iter / $2 / 1000" | bc -l)
                           # echo -e "avg_iter,,,,,,,,,,,,,,,${avgs},," >> $mainfolder/$namefile
