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
public class FormalVerification {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BDDSatSolver bddsolver = new BDDSatSolver();
        SimpleDPLLSolver dpllsolver = new SimpleDPLLSolver();
        ArrayList<ArrayList<Variable>> dimacsInput=DimacsParser.parse("res/reach3.cnf");
        solve(dpllsolver,SatSolver.cloneCnf(dimacsInput));
        solve(bddsolver,SatSolver.cloneCnf(dimacsInput));
        

    }
    static long solve(SatSolver solver, ArrayList<ArrayList<Variable>> dimacsInput){
        long start=System.currentTimeMillis();
        List<Variable> sol = solver.findSolution(dimacsInput);
        long time=System.currentTimeMillis()-start;
        if(sol.isEmpty()){
            //System.out.println("No solution");
        }
        else{
            if(!SatSolver.verifySolution(dimacsInput, sol)){
                System.out.println("Solution invalid from BDD");
            }
        }
        System.out.println("Solution found by "+solver.getName()+" in "+time+"ms:");
        if(sol.isEmpty()){
            System.out.println("Unsat");
            return time;
        }
        for(Variable var:sol){
            if(!var.isTrue)
                System.out.print("-");
            System.out.print(var.number+",");
        }
        System.out.println();
        return time;
    }
    
}
