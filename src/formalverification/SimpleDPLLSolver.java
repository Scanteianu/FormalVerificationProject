/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//http://profs.sci.univr.it/~farinelli/courses/ar/slides/DPLL.pdf
package formalverification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author danie
 */
public class SimpleDPLLSolver extends SatSolver {
    @Override
    public String getName(){
        return  "DPLL";
    }
    //todo: splitting thing
    @Override
    public List<Variable> findSolution(ArrayList<ArrayList<Variable>> cnfInput){
        List<Variable> realSolution=findSolutionHelp(cnfInput);
        //assign unassigned variables
        Set<Integer> vars = new HashSet();
        for(ArrayList<Variable> clause:cnfInput){
            for(Variable var: clause){
                vars.add(var.number);
            }
        }
        for(Variable var: realSolution){
            if(vars.contains(var.number))
                vars.remove(var.number);
        }
        for(Integer i: vars){
            Variable v = new Variable();
            v.number=i;
            v.isTrue=true;
            realSolution.add(v);
        }
        Collections.sort(realSolution);
        return realSolution;
    }
    public List<Variable> findSolutionHelp(ArrayList<ArrayList<Variable>> cnfInput) {
        ArrayList<Variable> solution = new ArrayList();
        HashMap<Integer,Boolean> currentStepAssignments = new HashMap();
        ArrayList<ArrayList<Variable>> updatedInput=cloneCnf(cnfInput);
        boolean keepReducing=true;
        while(keepReducing){
            Boolean changedSolo=setSoloVars(updatedInput,currentStepAssignments);
            if(changedSolo==null){
                return Collections.emptyList();//unsat flag
            }
            if(changedSolo){
                updatedInput=applyAssignments(updatedInput,currentStepAssignments);
            }
            boolean purified=purify(updatedInput,currentStepAssignments);
            if(purified){
                updatedInput=applyAssignments(updatedInput,currentStepAssignments);
            }
            keepReducing=purified||changedSolo;
        }
        if(updatedInput.isEmpty()){
            updateSolution(solution,currentStepAssignments);
            return solution;
        }
        int nextAssignment = pickNextAssignment(updatedInput);
        boolean flip=nextAssignment<0;
        if(flip){
            nextAssignment=Math.abs(nextAssignment);
        //try true
            List<Variable> subSol = trySubSat(updatedInput,nextAssignment,true^flip,currentStepAssignments);
            if(!subSol.isEmpty()){
                solution.addAll(subSol);
                updateSolution(solution,currentStepAssignments);
            }
            //try false
            subSol = trySubSat(updatedInput,nextAssignment,false^flip,currentStepAssignments);
            if(!subSol.isEmpty()){
                solution.addAll(subSol);
                updateSolution(solution,currentStepAssignments);
            }
        }
        else{
            return splitSubSat(updatedInput,nextAssignment, currentStepAssignments);
            
        }
        
    
        return Collections.emptyList();
    }
    private void updateSolution(ArrayList<Variable> solution, HashMap<Integer,Boolean> currentStepAssignments){
        for(Integer key: currentStepAssignments.keySet()){
                Variable v = new Variable();
                v.number=key;
                v.isTrue=currentStepAssignments.get(v.number);
                solution.add(v);
            }

    }
    List<Variable> trySubSat(ArrayList<ArrayList<Variable>> cnfInput,int nextAssignment, boolean assignVal,HashMap<Integer,Boolean> currentStepAssignments){
        ArrayList<ArrayList<Variable>> nextInput=cloneCnf(cnfInput);
        currentStepAssignments.put(nextAssignment,assignVal);
        applyAssignments(nextInput,currentStepAssignments);
        List<Variable> subSol=findSolutionHelp(nextInput);
        return subSol;
    }
    /**
     * precondition: there are no tautologies in cnf input (although this may not matter)
     * 
     * @param cnfInput
     * @param nextAssignment
     * @param currentStepAssignments
     * @return 
     */
    List<Variable> splitSubSat(ArrayList<ArrayList<Variable>> cnfInput,int nextAssignment, HashMap<Integer,Boolean> currentStepAssignments){
        ArrayList<ArrayList<Variable>> positive = new ArrayList<>(cnfInput.size());
        ArrayList<ArrayList<Variable>> negative = new ArrayList<>(cnfInput.size());
        for(ArrayList<Variable> clause: cnfInput){
            boolean added=false;
            for(Variable v: clause){
                if(v.number==nextAssignment){
                    if(v.isTrue){
                        positive.add(clause);
                        added=true;
                        break;
                    }
                    else{
                        negative.add(clause);
                        added=true;
                        break;
                    }
                }
            }
            if(!added){
                positive.add(clause);
                negative.add(clause);
            }
        }
        Variable solVar = new Variable();
        solVar.number=nextAssignment;
        List<Variable> positiveSolution = trySubSat(positive, nextAssignment, false,currentStepAssignments);
        if(!positiveSolution.isEmpty()){
            solVar.isTrue=false;
            positiveSolution.add(solVar);
            return positiveSolution;
        }
        List<Variable> negativeSolution = trySubSat(negative, nextAssignment, true,currentStepAssignments);
        if(!negativeSolution.isEmpty()){
            solVar.isTrue=true;
            negativeSolution.add(solVar);
            return negativeSolution;
        }
        return Collections.emptyList();
    }
    int pickNextAssignment(ArrayList<ArrayList<Variable>> input){
        int maxOverall=-1;
        int maxCountOverall=-1;
        int maxBinary=-1;
        int maxCountBinary=-1;
        HashMap<Integer,Integer> countOverall = new HashMap<>();
        HashMap<Integer,Integer> countBinary = new HashMap<>();
        for(ArrayList<Variable> clause : input){
            for(Variable var:clause){
                if(!countOverall.containsKey(var.number)){
                    countOverall.put(var.number, 0);
                }
                countOverall.put(var.number,countOverall.get(var.number)+1);
                if(maxCountOverall<countOverall.get(var.number)){
                    maxOverall=var.number;
                    maxCountOverall=countOverall.get(var.number);
                }
                if(clause.size()==2){
                    if(!countBinary.containsKey(var.number)){
                        countBinary.put(var.number, 0);
                    }
                    countBinary.put(var.number,countBinary.get(var.number)+1);
                    if(maxCountBinary<countBinary.get(var.number)){
                        maxBinary=var.number;
                        maxCountBinary=countOverall.get(var.number);
                    }
                }
            }
        }
        if(maxCountBinary>0){
            return -1*maxBinary;
        }
        return maxOverall;
    }
    
    ArrayList<ArrayList<Variable>> applyAssignments(ArrayList<ArrayList<Variable>> input, HashMap<Integer,Boolean> currentAssignments){
         ArrayList<ArrayList<Variable>> clauseForDeletion = new ArrayList();
        for(ArrayList<Variable> clause : input){
            ArrayList<Variable> varForDeletion = new ArrayList<>(clause.size());
            for(Variable v: clause){
                if(currentAssignments.containsKey(v.number)){
                    if(currentAssignments.get(v.number)==v.isTrue){
                        clauseForDeletion.add(clause);
                    }
                    else{
                        varForDeletion.add(v);
                    }

                }                                
            }
            clause.removeAll(varForDeletion); 
            if(clause.isEmpty()){
                clauseForDeletion.add(clause);
            }
        }
        input.removeAll(clauseForDeletion);
        return input;
    }
    /**
     * if there are clauses with exactly one variable, set the value of that variable for all the clauses in the input
     * 
     * return true if updated input, false if did not update,
     * or null if unsat
     * requires no vars in curr assn to be present in input
     * todo: in a hardcore engineering world, these operations would have a common interface and wrapping class
     * basically function implements functional interface type thing
     * @param input
     * @return 
     */
    Boolean setSoloVars(ArrayList<ArrayList<Variable>> input, HashMap<Integer,Boolean> currentAssignments){
        int assignmentsMade=0;
        for(ArrayList<Variable> clause : input){
            if(clause.size()==1){
                Variable var=clause.get(0);
                if(currentAssignments.containsKey(var.number)){
                    if(var.isTrue!=currentAssignments.get(var.number)){
                        return null;
                    }
                }
                else{
                    currentAssignments.put(var.number,var.isTrue);
                    assignmentsMade++;
                }
            }
        }
        return assignmentsMade!=0;
    }
    /**
     * remove clauses containing a pure litieral (a variable for which the negation is not present in some other clause)
     * @param input
     * @param currentAssignments
     * @return true if anything was removed, false otherwiseu
     */
    boolean purify(ArrayList<ArrayList<Variable>> input, HashMap<Integer,Boolean> currentAssignments){
        HashMap<Integer, Integer> purity=new HashMap<>();
        for(ArrayList<Variable> clause : input){

            for(Variable var:clause){
                if(purity.containsKey(var.number)){
                    if(var.isTrue){
                        if(purity.get(var.number)==-1){
                            purity.put(var.number,0);
                        }
                    }
                    else{
                        if(purity.get(var.number)==1){
                            purity.put(var.number,0);
                        }
                    }
                }
                else{
                    int score=(var.isTrue?1:-1);
                    purity.put(var.number,score);
                }
            }
        }
        int numClauses=input.size();
        for(ArrayList<Variable> clause : input){

            for(Variable var:clause){
                if(purity.containsKey(var.number)&&purity.get(var.number)!=0){
                    
                    currentAssignments.put(var.number, purity.get(var.number)>0);
                }
            }
        }
        
        return purity.size()!=0;
    }

    
}
