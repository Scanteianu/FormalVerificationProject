/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formalverification;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danie
 */
public abstract class SatSolver {
    abstract List<Variable> findSolution(ArrayList<ArrayList<Variable>> cnfInput);
    abstract String getName();
    public static boolean verifySolution(ArrayList<ArrayList<Variable>> cnf,List<Variable> sol){
        for(List<Variable> or:cnf){
            boolean found=false;
            if(!found){
                for(Variable var: sol){
                    if(or.contains(var)){
                        found=true;
                    }
                }
            }
            if(!found){
                return false;
            }
            
        }
        return true;
    }
    public static ArrayList<ArrayList<Variable>> cloneCnf(ArrayList<ArrayList<Variable>> input){
        ArrayList<ArrayList<Variable>> output = new ArrayList<>(input.size());
        for(ArrayList<Variable> clause : input){
            ArrayList<Variable> outClause=new ArrayList(clause.size());
            for(Variable v: clause){
                outClause.add(v.clone());
            }
            output.add(outClause);
        }
        return output;
    }
}
