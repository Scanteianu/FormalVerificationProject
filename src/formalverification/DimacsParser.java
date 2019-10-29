/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formalverification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danie
 */
public class DimacsParser {
    public static ArrayList<ArrayList<Variable>> parse(String filename){
        ArrayList<ArrayList<Variable>> cnf = new ArrayList();//todo: optimize size
        String line;
        try {
            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNextLine()){
                line=sc.nextLine();
                if(!line.startsWith("p")&&!line.startsWith("c")){
                    String[] tokens = line.split(" ");
                    ArrayList row = new ArrayList(tokens.length);
                    for(String token:tokens){
                        int num = Integer.parseInt(token);
                        if(num!=0){
                            Variable var = new Variable();
                            var.isTrue=num>0;
                            var.number=Math.abs(num);
                            row.add(var);
                        }
                    }
                    cnf.add(row);
                }
            }
            sc.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DimacsParser.class.getName()).log(Level.SEVERE, "File not found", ex);
        }
        return cnf;
    }
}
