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
public class SatSolver {
    public BDDNode falseNode = new BDDNode();
    public BDDNode trueNode = new BDDNode();
    public SatSolver(){
        falseNode.terminalValue=false;
        trueNode.terminalValue=true;
    }
    
    public List<Variable> findSolution(ArrayList<ArrayList<Variable>> cnfInput){
        List<BDDNode> roots = new ArrayList();
        for(List<Variable> ors:cnfInput){
            roots.add(buildOrBdd(ors));
        }
        return null;
    }
    
    
    /*this really needs to be an array list*/
    public BDDNode buildOrBdd(List<Variable> orExpression){
        BDDNode root = null;
        BDDNode prev=null;
        for(int i=0; i<orExpression.size(); i++){
            BDDNode current = new BDDNode();
            current.varNum=orExpression.get(i).number;
            if(orExpression.get(i).isTrue){
                current.rightChild=trueNode;
            }
            else{
                current.leftChild=trueNode;
            }
            if(i==0){
                root=current;
            }
            else{
                if(prev.leftChild==null){
                    prev.leftChild=current;
                }
                else{
                    prev.rightChild=current;
                }
            }
            prev=current;
            
        }
        if(orExpression.get(orExpression.size()-1).isTrue){
            prev.rightChild=trueNode;
            prev.leftChild=falseNode;
        }
        else{
            prev.leftChild=trueNode;
            prev.rightChild=falseNode;
        }
        return root;
    }
    
    
}
