/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.parsers;

import java.io.BufferedReader;
import java.util.List;
import me.megov.emc.t004.entities.CustomerLine;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author megov
 */
public class CustomerParserTest {
    
    public CustomerParserTest() {
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
     * Test of parseLine method, of class CustomerParser.
     */
    @Test
    public void testParseLine() throws Exception {
        System.out.println("parseLine");
        int lineNum = 0;
        String line = "";
        CustomerParser instance = new CustomerParser();
        CustomerLine expResult = null;
        CustomerLine result = instance.parseLine(lineNum, line);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of netMaskSort method, of class CustomerParser.
     */
    @Test
    public void testNetMaskSort() {
        System.out.println("netMaskSort");
        List<CustomerLine> _in = null;
        List<CustomerLine> expResult = null;
        List<CustomerLine> result = CustomerParser.netMaskSort(_in);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readFrom method, of class CustomerParser.
     */
    @Test
    public void testReadFrom_List() throws Exception {
        System.out.println("readFrom");
        List<String> _listCl = null;
        CustomerParser instance = new CustomerParser();
        List<CustomerLine> expResult = null;
        List<CustomerLine> result = instance.readFrom(_listCl);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readFrom method, of class CustomerParser.
     */
    @Test
    public void testReadFrom_BufferedReader() throws Exception {
        System.out.println("readFrom");
        BufferedReader _breader = null;
        CustomerParser instance = new CustomerParser();
        List<CustomerLine> expResult = null;
        List<CustomerLine> result = instance.readFrom(_breader);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
