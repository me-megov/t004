/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.augmentedtree;

import java.util.List;
import me.megov.emc.t004.entities.IPvXTuple;
import static me.megov.emc.t004.entities.IPvXTuple.IPV4_IN_V6_LOPREFIX;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author megov
 */
public class AUTreeTest {
    
    public IPvXTuple[] GOOD_LOWER_ADDRESSES = new IPvXTuple[] {
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A80000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A81000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A82000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A83000)
    };

    public IPvXTuple[] GOOD_UPPER_ADDRESSES = new IPvXTuple[] {
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A800FF),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8107F),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8203F),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8301F)
    };
    
    public IPvXTuple[] CONTAINS_ADDRESSES = new IPvXTuple[] {
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A80011),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A80000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8007f),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A800fe),
        
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A81000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A81001),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8107e),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A81033),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8107f),
        
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A82000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A82001),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8203e),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8203f),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A82022),
        
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A83000),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A83001),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8301e),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A8301f),
        new IPvXTuple(0, IPV4_IN_V6_LOPREFIX | 0xC0A83010)
    };
    

    
    public AUTreeTest() {
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
    public void testSomeMethod() {
        // TODO review the generated test code and remove the default call to fail.
        AUTree aut = new AUTree();
        
        aut.dump(System.out);
        
        for (int i=0; i<GOOD_LOWER_ADDRESSES.length; i++) {
            aut.addNode(new AUTNode(GOOD_LOWER_ADDRESSES[i], 
                                    GOOD_UPPER_ADDRESSES[i],
                                    "USER"+Long.toHexString(GOOD_LOWER_ADDRESSES[i].getLo())  ));
        }
        
        aut.dump(System.out);
        
        for (IPvXTuple addr: CONTAINS_ADDRESSES) {
            List<AUTNode> list = aut.containsTuple(addr);
            if ((list==null) || (list.isEmpty()))  {
                fail("Node "+addr+" have to exist, but not found!");
            } else {
                System.out.println("RES="+addr);
                for (AUTNode node: list) {
                    System.out.println("->"+node);
                }
            }
        }
        
    }
    
}
