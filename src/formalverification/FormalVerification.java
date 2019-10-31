/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formalverification;

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
        SatSolver solver = new SatSolver();
        List<Variable> sol = solver.findSolution(DimacsParser.parse("res/hole6.cnf"));
        if(sol.isEmpty()){
            System.out.println("No solution");
        }
        for(Variable var:sol){
            if(!var.isTrue)
                System.out.print("-");
            System.out.print(var.number+",");
        }
        System.out.println();
    }
    
}
