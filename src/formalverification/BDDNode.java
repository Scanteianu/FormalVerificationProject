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
public class BDDNode {
    public int varNum;
    public int customHashCode;
    public BDDNode leftChild;
    public BDDNode rightChild;
    public BDDNode parentNode;//todo: second traversal to set this thing?
    Boolean terminalValue;
    @Override
    public boolean equals(Object other){
        if(!(other instanceof BDDNode)){
            return false;
        }
        BDDNode n = (BDDNode)other;
        if(terminalValue!=null){
            return terminalValue.equals(n.terminalValue);
        }
        if(varNum!=n.varNum){
            return false;
        }
        if(!leftChild.equals(n.leftChild)){
            return false;
        }
        if(!rightChild.equals(n.rightChild)){
            return false;
        }
        return true;
        
    }
    public String toString(){
        return toString("");
    }
    public String toString(String whiteSpace){
        if(terminalValue!=null){
            return whiteSpace+terminalValue+"\n";
        }
        return whiteSpace+varNum+"\n"+leftChild.toString(whiteSpace+"  ")+rightChild.toString(whiteSpace+"  ");
    }
    public void recursiveHashCode(){
        if(terminalValue!=null){
            computeHashCode();
            return;
        }
        leftChild.recursiveHashCode();
        rightChild.recursiveHashCode();
        computeHashCode();
    }
    public void computeHashCode(){
        if(terminalValue!=null){
            if(terminalValue)
                customHashCode=1;
            else{
                customHashCode=0;
            }
        }
        customHashCode+=7*varNum;
        customHashCode+=31*leftChild.customHashCode+19*rightChild.customHashCode;
    }
    
}
