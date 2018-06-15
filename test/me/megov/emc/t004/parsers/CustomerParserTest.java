/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.parsers;

import java.io.BufferedReader;
import java.util.List;
import me.megov.emc.t004.entities.CustomerLine;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.exceptions.T004FormatException;
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
    
    public String[] GOOD_CUSTOMER_LINE_STRINGS = new String[]{
        "0.0.0.0/0 Customer0",
    };
    
    public CustomerLine[] GOOD_CUSTOMER_LINES = new CustomerLine[]{
        
    };
    
    
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

    @Test
    public void testParseLine() throws Exception {
        System.out.println("parseLineV4");
        CustomerParser p = new CustomerParser();
        assertEquals(new CustomerLine("0.0.0.0", 0, "Customer"), 
                p.parseLine(1, "0.0.0.0/0  Customer   "));
        assertEquals(new CustomerLine("10.10.10.0", 24, "Customer10"), 
                p.parseLine(1, "  10.10.10.0/24  Customer10   "));
        assertEquals(new CustomerLine("23.23.23.23", 32, "Customer32"), 
                p.parseLine(1, "23.23.23.23/32  Customer32   "));
        try {
            assertEquals(new CustomerLine("10.10.10.10", 24, "Customer10Bad"), 
                p.parseLine(1, "  10.10.10.10/24  Customer10Bad   "));
        } catch (T004FormatException ex) {
            
        }
    }

    @Test
    public void testNetMaskSort() {
        System.out.println("netMaskSort");
    }

    /**
     * Test of readFrom method, of class CustomerParser.
     */
    @Test
    public void testReadFrom_List() throws Exception {
        System.out.println("readFrom");
    }

    /**
     * Test of readFrom method, of class CustomerParser.
     */
    @Test
    public void testReadFrom_BufferedReader() throws Exception {
        System.out.println("readFrom");
    }
    
}
