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
    //todo1: smart varchoice
    //todo2: split
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
        //try true
        List<Variable> subSol = trySubSat(updatedInput,nextAssignment,true,currentStepAssignments);
        if(!subSol.isEmpty()){
            solution.addAll(subSol);
            updateSolution(solution,currentStepAssignments);
        }
        //try false
        subSol = trySubSat(updatedInput,nextAssignment,false,currentStepAssignments);
        if(!subSol.isEmpty()){
            solution.addAll(subSol);
            updateSolution(solution,currentStepAssignments);
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
    int pickNextAssignment(ArrayList<ArrayList<Variable>> input){
        return input.get(0).get(0).number;
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
