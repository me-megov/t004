/*
 * Copyright 2018 megov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.megov.emc.t004.entities;

import me.megov.emc.t004.helpers.CustomerGenerator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.megov.emc.t004.exceptions.T004BadDataException;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.exceptions.T004FormatException;
import me.megov.emc.t004.helpers.CustomerTreeHelper;
import me.megov.emc.t004.parsers.CustomerParser;
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
public class CustomerTest {

    public static String[] badFormat0Custometrs = new String[]{
        "10.5.96.0/20  ITShop*"
    };

    public static String[] badFormat1Custometrs = new String[]{
        "10.5.96.0/34  ITShop"
    };
    
    public static String[] badFormat2Custometrs = new String[]{
        "10.5.97.0/20  ITShop"
    };
    

    public static String[] goodCustometrs = new String[]{
  //      "2001:0db8:85a3:0000::0/64		 Pepsi	",
  //      "2001:0db8:85a3:0000:0100::0/72		 SubPepsi0	",        
  //      "2001:0db8:85a3:0000:1000::0/72		 SubPepsi1	",
  //      "2001:0db8:85a3:0100::0/72		 NearPepsi0	",        
  //      "2001:0db8:85a3:1000::0/72		 NeadPepsi1	",
  //      "2001:0db8:85a3::0/80		 Goverment	",
        "10.5.96.0/20  ITShop.0",
        "10.5.99.0/25  Cafe.1",
        "10.5.100.128/25     Zoo.1 ",
        "10.5.100.160/30     Serpentarium.2 ",
        "10.5.5.0/24				   CocaCola.0",
        "10.5.6.128/25					  CocaCola.0",
        "10.5.6.160/29  MicroColas.1"
    };
    
    
public static String[] unorderedGoodCustometrs = new String[]{
  //      "2001:0db8:85a3:0000::0/64		 Pepsi	",
  //      "2001:0db8:85a3:0000:0100::0/72		 SubPepsi0	",        
  //      "2001:0db8:85a3:0000:1000::0/72		 SubPepsi1	",
  //      "2001:0db8:85a3:0100::0/72		 NearPepsi0	",        
  //      "2001:0db8:85a3:1000::0/72		 NeadPepsi1	",
  //      "2001:0db8:85a3::0/80		 Goverment	",
        "10.5.100.160/30     Serpentarium.2 ",
        "10.5.100.128/25     Zoo.1 ",
        "10.5.99.0/25  Cafe.1",
        "10.5.96.0/20  ITShop.0",
        "10.5.6.160/29  MicroColas.1",
        "10.5.5.0/24				   CocaCola.0",
        "10.5.6.128/25					  CocaCola.0"
    };    
    
    public static String[] crossOverlappedCustometrs = new String[]{
        "10.5.96.0/20  ITShop",
        "10.5.94.0/23     Cafe"
    };
  

    public CustomerTest() {
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

    public Customer iterateCustomerList(String[] arr) throws T004Exception {
        Customer root = new Customer("");
        CustomerParser parser = new CustomerParser();
        List<CustomerLine> listCl = parser.readFrom(Arrays.asList(arr));
        for (CustomerLine cl : listCl) root.addSubCustomer(cl);
        return root;
    }

    @Test
    public void testGoodCustomerTree() throws T004Exception {
        System.out.println("---test good customers");
        Customer root = iterateCustomerList(goodCustometrs);
        CustomerTreeHelper.printTree(root, 0, System.out);
        checkCustomerTree(root);
    }
    
    @Test
    public void testUnorderedCustomerTree() throws T004Exception {
        System.out.println("---test unordered customers");
        Customer root = iterateCustomerList(unorderedGoodCustometrs);
        CustomerTreeHelper.printTree(root, 0, System.out);
        checkCustomerTree(root);
    }
    

    @Test
    public void testBadFmtCustomerTree() throws T004Exception {
        System.out.println("---test bad format customers");
        try {
            Customer root = iterateCustomerList(badFormat0Custometrs);
        } catch (IllegalStateException ex) {
            System.out.println("Illegal customer name - passed");
        }

        try {
            Customer root = iterateCustomerList(badFormat1Custometrs);
        } catch(T004BadDataException ex) { 
            System.out.println("Illegal v4 bimask - passed");
        
        }
        try {
            Customer root = iterateCustomerList(badFormat2Custometrs);
        } catch(T004FormatException ex) { 
            System.out.println("Non-network boundary address - passed");
        }
        Customer root = iterateCustomerList(crossOverlappedCustometrs);

    }
    
    @Test
    public void testGeneratedCustomerTree() throws T004Exception {
        Customer custroot = CustomerTreeHelper.generateCustomerTree(10, new Customer(""), System.out);
        checkCustomerTree(custroot);
    }
   
    public void checkCustomerTree(Customer custroot) throws T004Exception {
            boolean isBadTreeOrdering = false;
            isBadTreeOrdering = CustomerGenerator.debugCheckTree(custroot, 0, System.err, isBadTreeOrdering);
            if (isBadTreeOrdering) throw new T004Exception("Bad customer tree ordering");
    }
    
    

}
