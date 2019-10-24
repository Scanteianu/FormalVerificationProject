/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formalverification;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author danie
 */
public class SatSolverTest {
    
    public SatSolverTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of buildOrBdd method, of class SatSolver.
     */
    @Test
    public void testBuildOrBdd() {
        System.out.println("buildOrBdd");
        Variable av = new Variable();
        av.isTrue=true;
        av.number=0;
        Variable bv = new Variable();
        bv.isTrue=false;
        bv.number=1;
        Variable cv = new Variable();
        cv.isTrue=true;
        cv.number=2;
        
        List<Variable> orExpression = new ArrayList(3);
        SatSolver ss = new SatSolver();
        orExpression.add(av);
        orExpression.add(bv);
        orExpression.add(cv);
        
        BDDNode an = new BDDNode();
        an.varNum=av.number;
        BDDNode bn = new BDDNode();
        bn.varNum=bv.number;
        BDDNode cn = new BDDNode();
        cn.varNum = cv.number;
        an.rightChild=ss.trueNode;
        an.leftChild=bn;
        bn.rightChild=cn;
        bn.leftChild=ss.trueNode;
        cn.leftChild=ss.falseNode;
        cn.rightChild=ss.trueNode;
        
        BDDNode result = ss.buildOrBdd(orExpression);
        Assert.assertTrue(an.equals(result));
    }

    /**
     * Test of findSolution method, of class SatSolver.
     */
//    @Test
//    public void testFindSolution() {
//        System.out.println("findSolution");
//        List<List<Variable>> cnfInput = null;
//        SatSolver instance = new SatSolver();
//        List<Variable> expResult = null;
//        List<Variable> result = instance.findSolution(cnfInput);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
