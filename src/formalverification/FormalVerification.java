/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formalverification;

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
        solver.findSolution(DimacsParser.parse("res/simple_v3_c2.cnf"));
    }
    
}
