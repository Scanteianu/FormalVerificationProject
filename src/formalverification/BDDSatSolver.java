/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formalverification;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;


/**
 *
 * @author danie
 */
public class BDDSatSolver extends SatSolver {
    public BDDNode falseNode = new BDDNode();
    public BDDNode trueNode = new BDDNode();
    Map<Entry<BDDNode,BDDNode>,BDDNode> dpTbl=new HashMap<>();
    public BDDSatSolver(){
        super();
        falseNode.terminalValue=false;
        trueNode.terminalValue=true;
    }
    @Override
    public String getName(){
        return "BDD";
    }
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
    public List<Variable> findSolutionHelp(ArrayList<ArrayList<Variable>> cnfInput){
        List<BDDNode> roots = new ArrayList();
        for(List<Variable> ors:cnfInput){
            roots.add(buildOrBdd(ors));
        }
        
        while(roots.size()>1){
            BDDNode left = roots.get(0);
            BDDNode right = roots.get(1);
            roots.remove(0);
            roots.remove(0);
            roots.add(bddAnd(left,right));
        }
        setParentRefs(roots.get(0));
        //System.out.println(roots.get(0).toString());
        List<Variable> solution = getSolution();
        return solution;
    }
    /*this really needs to be an array list*/
    public BDDNode buildOrBdd(List<Variable> orExpression){
        Collections.sort(orExpression);
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
    public BDDNode bddAnd(BDDNode a, BDDNode b){
        Entry<BDDNode,BDDNode>dpKey=new AbstractMap.SimpleEntry<>(a,b);
        if(dpTbl.containsKey(dpKey)){
            return dpTbl.get(dpKey);
        }
        BDDNode result=bddAndImpl(a,b);
        dpTbl.put(dpKey,result);
        return result;
    }
    public BDDNode bddAndImpl(BDDNode a, BDDNode b){
        //base case
//        System.out.println("inputs:");
//        System.out.println(a.toString());
//        System.out.println(b.toString());
        if(a.terminalValue!=null && b.terminalValue!=null){
            if(a.terminalValue&&b.terminalValue)
                return trueNode;
            return falseNode;
        }
        if(a.terminalValue!=null || b.terminalValue!=null){
            if(a.terminalValue!=null && a.terminalValue==true){
                return b;
            }
            if(b.terminalValue!=null && b.terminalValue==true){
                return a;
            }
            return falseNode;
        }
        if(a.equals(b)){//todo: optimizes
            return a;
        }
        if(a.varNum!=b.varNum){
            BDDNode lesser;
            BDDNode greater;
            if(a.varNum<b.varNum){
                lesser=a;
                greater=b;
            }
            else{
                lesser=b;
                greater=a;
            }
            BDDNode returnNode = new BDDNode();
            returnNode.varNum=lesser.varNum;
            //todo: memoize
            BDDNode leftChild = bddAnd(lesser.leftChild,greater);
            BDDNode rightChild = bddAnd(lesser.rightChild,greater);
            returnNode.leftChild=leftChild;
            returnNode.rightChild=rightChild;
            return returnNode;
            
        }
        BDDNode returnNode = new BDDNode();
        BDDNode leftChild = bddAnd(a.leftChild,b.leftChild);
        BDDNode rightChild = bddAnd(a.rightChild,b.rightChild);
//        System.out.println("recursion return:");
//        System.out.println(leftChild.toString());
//        System.out.println(rightChild.toString());
        if(leftChild.equals(rightChild)){
            return leftChild;
        }
        returnNode.leftChild=leftChild;
        returnNode.rightChild=rightChild;
        returnNode.varNum=a.varNum;
        return returnNode;
    }
    public void setParentRefs(BDDNode root){
        if(root.terminalValue==null){
            root.leftChild.parentNode=root;
            root.rightChild.parentNode=root;
            setParentRefs(root.leftChild);
            setParentRefs(root.rightChild);
        }
        
    }
    public List<Variable> getSolution(){
        List<Variable> solution = new LinkedList<>();
        BDDNode current = trueNode;
        while(current.parentNode!=null){
            Variable var = new Variable();
            var.number=current.parentNode.varNum;
            var.isTrue=current.parentNode.rightChild==current;
            solution.add(var);
            current=current.parentNode;
        }
        return solution;
    }
}
