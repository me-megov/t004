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
package me.megov.emc.t004.parsers;

import me.megov.emc.t004.exceptions.T004FormatException;
import me.megov.emc.t004.helpers.IPv4Helper;
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
public class IPvXAddrParserTest {
    
    public static final String[] IPV4_GOOD_STRINGS = new String[]{
        "192.168.0.16",
        "192.168.0.1",
        "8.8.8.8",
        "255.255.255.0",
        "224.0.0.1",
        "0.0.0.0",
        "255.255.255.255"
    };

    public static final String[] IPV4_BAD_STRINGS = new String[]{
        "192.168.0.277",
        "192.168..1",
        "8.8.8.Z",
        "255-255.255.0",
        "224:0.0.1",
        "0.0.0,0",
        "255\255.255.255"
    };

    //To sucessfully test string representation equality, v6 addressed must 
    //be specified without leading zeroes in digit groups and with hex numbers 
    //in ay case. Compressed forms with :: also supported. Mixed semicolon
    //and dotted notation for "v4 in v6" range are NOT SUPPORTED!!!
    public static final String[] IPV6_GOOD_STRINGS = new String[]{
        "::",
        "::1",        
        "FF01::101",
        "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789",
        "2001:DB8:0:0:8:800:200C:417A",
        "FF01:0:0:0:0:0:0:101",
        "0:0:0:0:0:0:0:1",
        "0:0:0:0:0:0:0:0",
        "2001:DB8::8:800:200C:417A",
        //!!! not supported "0:0:0:0:0:0:13.1.68.3",
        //!!! not supported "0:0:0:0:0:FFFF:129.144.52.38",
        //!!! not supported "::13.1.68.3",
        //!!! not supported "FFFF:129.144.52.38",
        "2001:DB8:0:CD30::",
        "2001:DB8:0:CD30:0:0:0:0",
        "2001:DB8::CD30:0:0:0:0"
    };

    public static final String[] IPV6_BAD_STRINGS = new String[]{
        "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789:9999",
        "2001:DB8Z:0:0:8:800:200C:417A",
        "FF01:0::0:0::0:101",
        "0:0:0:0:0:0:0.1",
        "0:0:0:0:0:0:0",
        "2001:DB8::8:800Z:200C:417A",
        "FF01:::101",
        "FFFF:329.144.52.38"
    };

    public IPvXAddrParserTest() {
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
    public void simpleTestV4() {
        System.out.println("IPvXAddr: simple v4 test");
        assertEquals(true, new IPvXAddrParser(0L, IPv4Helper.IPV4_IN_V6_LOPREFIX).getAddr().isV4Addr());
        assertEquals(true, new IPvXAddrParser(0L, IPv4Helper.IPV4_IN_V6_LOPREFIX | 0xC0A80010L).getAddr().isV4Addr());
        assertEquals(true, new IPvXAddrParser(0L, IPv4Helper.IPV4_IN_V6_LOPREFIX | 0xFFFFFFFFL).getAddr().isV4Addr());
        System.out.println("IPvXAddr: simple v4 test OK");
    }

    @Test
    public void simpleTestV6() {
        System.out.println("IPvXAddr: simple v6 test");

        assertEquals(false, new IPvXAddrParser(0L, 0L).getAddr().isV4Addr());
        assertEquals(false, new IPvXAddrParser(0L, 0xFFC0A80010L).getAddr().isV4Addr());
        assertEquals(false, new IPvXAddrParser(0L, 0xFFFFFFC0A80010L).getAddr().isV4Addr());
        assertEquals(false, new IPvXAddrParser(0L, 0xFFFFFFFFC0A80010L).getAddr().isV4Addr());

        assertEquals(false, new IPvXAddrParser(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFC0A80010L).getAddr().isV4Addr());
        assertEquals(false, new IPvXAddrParser(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL).getAddr().isV4Addr());

        System.out.println("IPvXAddr: simple v6 test OK");
    }

    @Test
    public void stringTestV4() throws Exception {
        System.out.println("IPvXAddr: string v4 test");
        for (String s : IPV4_GOOD_STRINGS) {
            IPvXAddrParser addr = new IPvXAddrParser(s);
            assertEquals(true, addr.getAddr().isV4Addr());
            System.out.println("IPvXAddr: " + s + "=" + addr.toString());
            assertEquals(true, s.equals(addr.toString()));
        }

        for (String s : IPV4_BAD_STRINGS) {
            try {
                IPvXAddrParser addr = new IPvXAddrParser(s);
                assertEquals(false, true);
            } catch (T004FormatException ex) {
                System.out.println("IPvXAddr: BAD=" + s);
            }
        }
        System.out.println("IPvXAddr: string v4 test OK");
    }

    @Test
    public void stringTestV6() throws Exception {
        System.out.println("IPvXAddr: string v6 test");
        for (String s : IPV6_GOOD_STRINGS) {
            IPvXAddrParser addr = new IPvXAddrParser(s);
            assertEquals(false, addr.getAddr().isV4Addr());
            System.out.println("IPvXAddr: " + s + "=" + addr.toString());
            //assertEquals(true, s.toUpperCase().equals(addr.toString()));
        }
        for (String s : IPV6_BAD_STRINGS) {
            try {
                IPvXAddrParser addr = new IPvXAddrParser(s);
                assertEquals(false, true);
            } catch (T004FormatException ex) {
                System.out.println("IPvXAddr: BAD=" + s);
            }
        }
        System.out.println("IPvXAddr: string v6 test OK");

    }
}
