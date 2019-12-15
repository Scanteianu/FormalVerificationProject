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
public class Variable implements Comparable {
    public int number;
    public boolean isTrue;

    @Override
    public int compareTo(Object o) {
        if(o instanceof Variable){
            return Integer.compare(number, ((Variable)o).number);
        }
        throw new IllegalArgumentException("Can't compare a variable with something else");
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Variable)){
            return false;
        }
        Variable v = (Variable)o;
        return v.isTrue==isTrue && v.number==number;
    }
    @Override
    public Variable clone(){
       Variable v = new Variable();
       v.isTrue=isTrue;
       v.number=number;
       return v;
    }
}
