/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.helpers;

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
public class IPv4HelperTest {
    
    public IPv4HelperTest() {
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

    private String internalTestV4AddressString(long v4) {
        return IPv4Helper.getV4AddressString(v4);
    }
    
    /**
     * Test of getV4AddressString method, of class IPv4Helper.
     */
    @Test
    public void testGetV4AddressString() {
        System.out.println("getV4AddressString");
        assertEquals("0.0.0.0", internalTestV4AddressString(0L));
        assertEquals("10.10.10.10", internalTestV4AddressString(0x0A0A0A0AL));
        assertEquals("172.0.0.0", internalTestV4AddressString(0xAC000000L));
        assertEquals("192.168.10.1", internalTestV4AddressString(0xC0A80A01L));        
        assertEquals("224.224.224.224", internalTestV4AddressString(0xE0E0E0E0L));
        assertEquals("255.255.0.0", internalTestV4AddressString(0xFFFF0000L));
        assertEquals("255.255.255.255", internalTestV4AddressString(0xFFFFFFFFL));
        
    }

    /**
     * Test of getV4BitMask method, of class IPv4Helper.
     */
    @Test
    public void testGetV4BitMask() throws Exception {
        System.out.println("getV4BitMask");
        assertEquals(IPv4Helper.getV4BitMask(32), 0xFFFFFFFF);
        assertEquals(IPv4Helper.getV4BitMask(31), 0xFFFFFFFE);
        assertEquals(IPv4Helper.getV4BitMask(30), 0xFFFFFFFC);        
        assertEquals(IPv4Helper.getV4BitMask(28), 0xFFFFFFF0);        
        assertEquals(IPv4Helper.getV4BitMask(24), 0xFFFFFF00);
        assertEquals(IPv4Helper.getV4BitMask(16), 0xFFFF0000);
        assertEquals(IPv4Helper.getV4BitMask(8), 0xFF000000);
        assertEquals(IPv4Helper.getV4BitMask(4), 0xF0000000);
        assertEquals(IPv4Helper.getV4BitMask(2), 0xc0000000);
        assertEquals(IPv4Helper.getV4BitMask(1), 0x80000000);
        assertEquals(IPv4Helper.getV4BitMask(0), 0x00000000);
    }

    private String internalGetLowerBoundHex(long addr, int mask) throws Exception{
        long lowBound = IPv4Helper.toLowerBound(addr, mask);
        return Long.toHexString(lowBound);
    }

     private String internalGetLowerBoundV6Hex(long addr, int mask) throws Exception{
        long lowBound = IPv4Helper.toLowerBoundMappedToV6(addr, mask);
        return Long.toHexString(lowBound);
    }

    private String internalGetUpperBoundHex(long addr, int mask) throws Exception{
        long upBound = IPv4Helper.toUpperBound(addr, mask);
        return Long.toHexString(upBound);
    }

    private String internalGetUpperBoundV6Hex(long addr, int mask) throws Exception{
        long upBound = IPv4Helper.toUpperBoundMappedToV6(addr, mask);
        return Long.toHexString(upBound);
    }

    
    
    private String internalGetHostPartHex(long addr, int mask) throws Exception{
        long hostPart = IPv4Helper.toHostPart(addr, mask);
        return Long.toHexString(hostPart);
    }    

    /**
     * Test of toLowerBound method, of class IPv4Helper.
     */
    @Test
    public void testToLowerBound() throws Exception {
        System.out.println("toLowerBound");
        assertEquals(internalGetLowerBoundHex(0xC0A80012L, 24), Long.toHexString(0xC0A80000L));
        assertEquals(internalGetLowerBoundHex(0x12345678L, 4), Long.toHexString(0x10000000L));
        assertEquals(internalGetLowerBoundHex(0xE0E0E0E0L, 16), Long.toHexString(0xE0E00000L));
        assertEquals(internalGetLowerBoundHex(0xE0E0E0FFL, 31), Long.toHexString(0xE0E0E0FEL));
        assertEquals(internalGetLowerBoundHex(0xABCDEF01L, 32), Long.toHexString(0xABCDEF01L));
        assertEquals(internalGetLowerBoundHex(0x12345678L, 13), Long.toHexString(0x12300000L));
    }

    /**
     * Test of toUpperBound method, of class IPv4Helper.
     */
    @Test
    public void testToUpperBound() throws Exception {
        System.out.println("toUpperBound");
        assertEquals(internalGetUpperBoundHex(0xC0A80012L, 24), Long.toHexString(0xC0A800FFL));
        assertEquals(internalGetUpperBoundHex(0x12345678L, 4), Long.toHexString(0x1FFFFFFFL));
        assertEquals(internalGetUpperBoundHex(0xE0E0E0E0L, 16), Long.toHexString(0xE0E0FFFFL));
        assertEquals(internalGetUpperBoundHex(0xE0E0E0FFL, 31), Long.toHexString(0xE0E0E0FFL));
        assertEquals(internalGetUpperBoundHex(0xABCDEF01L, 32), Long.toHexString(0xABCDEF01L));
        assertEquals(internalGetUpperBoundHex(0x12345678L, 13), Long.toHexString(0x1237FFFFL));
    }

    /**
     * Test of toHostPart method, of class IPv4Helper.
     */
    @Test
    public void testToHostPart() throws Exception {
        System.out.println("toHostPart");
        assertEquals(internalGetHostPartHex(0xC0A80012L, 24), Long.toHexString(0x12L));
        assertEquals(internalGetHostPartHex(0x12345678L, 4), Long.toHexString(0x02345678L));
        assertEquals(internalGetHostPartHex(0xE0E0E0E0L, 16), Long.toHexString(0xE0E0L));
        assertEquals(internalGetHostPartHex(0xE0E0E0FFL, 31), Long.toHexString(0x1L));
        assertEquals(internalGetHostPartHex(0xABCDEF01L, 32), Long.toHexString(0x0L));
        assertEquals(internalGetHostPartHex(0x12345678L, 13), Long.toHexString(0x45678L));

    }

    /**
     * Test of toLowerBoundMappedToV6 method, of class IPv4Helper.
     */
    @Test
    public void testToLowerBoundMappedToV6() throws Exception {
        System.out.println("toLowerBoundMappedToV6");
        assertEquals(internalGetLowerBoundV6Hex(0xC0A80012L, 24), Long.toHexString(0xFFFFC0A80000L));
        assertEquals(internalGetLowerBoundV6Hex(0x12345678L,  4), Long.toHexString(0xFFFF10000000L));
        assertEquals(internalGetLowerBoundV6Hex(0xE0E0E0E0L, 16), Long.toHexString(0xFFFFE0E00000L));
        assertEquals(internalGetLowerBoundV6Hex(0xE0E0E0FFL, 31), Long.toHexString(0xFFFFE0E0E0FEL));
        assertEquals(internalGetLowerBoundV6Hex(0xABCDEF01L, 32), Long.toHexString(0xFFFFABCDEF01L));
        assertEquals(internalGetLowerBoundV6Hex(0x12345678L, 13), Long.toHexString(0xFFFF12300000L));
    }

    /**
     * Test of toUpperBoundMappedToV6 method, of class IPv4Helper.
     */
    @Test
    public void testToUpperBoundMappedToV6() throws Exception {
        System.out.println("toUpperBoundMappedToV6");
        assertEquals(internalGetUpperBoundV6Hex(0xC0A80012L, 24), Long.toHexString(0xFFFFC0A800FFL));
        assertEquals(internalGetUpperBoundV6Hex(0x12345678L, 4), Long.toHexString(0xFFFF1FFFFFFFL));
        assertEquals(internalGetUpperBoundV6Hex(0xE0E0E0E0L, 16), Long.toHexString(0xFFFFE0E0FFFFL));
        assertEquals(internalGetUpperBoundV6Hex(0xE0E0E0FFL, 31), Long.toHexString(0xFFFFE0E0E0FFL));
        assertEquals(internalGetUpperBoundV6Hex(0xABCDEF01L, 32), Long.toHexString(0xFFFFABCDEF01L));
        assertEquals(internalGetUpperBoundV6Hex(0x12345678L, 13), Long.toHexString(0xFFFF1237FFFFL));
    }
    
}
