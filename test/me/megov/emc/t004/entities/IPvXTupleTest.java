/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.entities;

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
public class IPvXTupleTest {

    public static int[] IPV6MASK_BITS_128_65 = new int[]{
        128, 127, 126,
        124, 120, 112,
        104, 96, 88,
        80, 72, 65
    };

    public static long[] IPV6MASK_VALUES_128_65 = new long[]{
        0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFEL, 0xFFFFFFFFFFFFFFFCL,
        0xFFFFFFFFFFFFFFF0L, 0xFFFFFFFFFFFFFF00L, 0xFFFFFFFFFFFF0000L,
        0xFFFFFFFFFF000000L, 0xFFFFFFFF00000000L, 0xFFFFFF0000000000L,
        0xFFFF000000000000L, 0xFF00000000000000L, 0x8000000000000000L
    };

    public static int[] IPV6MASK_BITS_64_0 = new int[]{
        64, 63, 62,
        56, 48, 40,
        32, 24, 16,
        12, 8, 6,
        4, 2, 1,
        0
    };

    public static long[] IPV6MASK_VALUES_64_0 = new long[]{
        0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFEL, 0xFFFFFFFFFFFFFFFCL,
        0xFFFFFFFFFFFFFF00L, 0xFFFFFFFFFFFF0000L, 0xFFFFFFFFFF000000L,
        0xFFFFFFFF00000000L, 0xFFFFFF0000000000L, 0xFFFF000000000000L,
        0xFFF0000000000000L, 0xFF00000000000000L, 0xFC00000000000000L,
        0xF000000000000000L, 0xC000000000000000L, 0x8000000000000000L,
        0x0000000000000000L
    };

    public static int[] IPV4MASK_BITS = new int[]{
        32, 31, 30,
        28, 24, 16,
        8, 4, 2,
        1, 0
    };

    public static long[] IPV4MASK_VALUES = new long[]{
        0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFEL, 0xFFFFFFFFFFFFFFFCL,
        0xFFFFFFFFFFFFFFF0L, 0xFFFFFFFFFFFFFF00L, 0xFFFFFFFFFFFF0000L,
        0xFFFFFFFFFF000000L, 0xFFFFFFFFF0000000L, 0xFFFFFFFFC0000000L,
        0xFFFFFFFF80000000L, 0xFFFFFFFF00000000L
    };

    public static long[] IPV4_TEST_ADDRS = new long[]{
        0xC00A0000L, 0xC0A80012L, 0x12345678L, 0xE0E0E0E0L, 0xE0E0E0FFL,
        0xABCDEF01L, 0x12345678L, 0x10101010L, 0XFF00FF00L, 0xC8C8ADE3L
    };

    public static int[] IPV4_TEST_MASKS = new int[]{
        1, 24, 4, 16, 31,
        32, 13, 17, 23, 27
    };

    public static long[] IPV4_TEST_LOWER = new long[]{
        0x80000000L, 0xC0A80000L, 0x10000000L, 0xE0E00000L, 0xE0E0E0FEL,
        0xABCDEF01L, 0x12300000L, 0x10100000L, 0XFF00FE00L, 0xC8C8ADE0L
    };

    public static long[] IPV4_TEST_UPPER = new long[]{
        0xFFFFFFFFL, 0xC0A800FFL, 0x1FFFFFFFL, 0xE0E0FFFFL, 0xE0E0E0FFL,
        0xABCDEF01L, 0x1237FFFFL, 0x10107FFFL, 0XFF00FFFFL, 0xC8C8ADFFL
    };

    public static long[] IPV4_TEST_HOSTS = new long[]{
        0x400A0000L, 0x00000012L, 0x02345678L, 0x0000E0E0L, 0x00000001L,
        0x00000000L, 0x00045678L, 0x00001010L, 0X00000100L, 0x00000003L
    };

    public static IPvXTuple[] IPV6_TEST_ADDRS = new IPvXTuple[]{
        new IPvXTuple(0x2c16301b5b1ab83dL, 0x55cdd0e04efc1c52L), //0
        new IPvXTuple(0x7d1c5fbbf795bc0aL, 0xbfbd4b2fa6471356L), //1
        new IPvXTuple(0x28dada8e1c300c93L, 0x73bf6d02d788b033L), //2
        new IPvXTuple(0x6c6bb907fff8e4f2L, 0x65d4e1181d6a702bL), //8
        new IPvXTuple(0xd9b584b3c333639bL, 0x4e8251b2c330a82cL), //16
        new IPvXTuple(0xc0f1e3ef082c6f0cL, 0x4c3886bfefcb044aL), //32
        new IPvXTuple(0x954238b605e14b56L, 0x6d6ec06279a5d84cL), //63
        new IPvXTuple(0x1d5404380ac57c3cL, 0xcf8ae2233a829361L), //64
        new IPvXTuple(0xa0bae106a80d5753L, 0xbd81968a2cd1bfe2L), //65
        new IPvXTuple(0x82b424b06c578d1L, 0x85fee19682512b4aL), //72
        new IPvXTuple(0x58b5cc92d782282cL, 0x9543f6c2f4ed0aaL), //96
        new IPvXTuple(0x95f738212a5c13f4L, 0xdc06766d34ee3feaL), //112
        new IPvXTuple(0xf6ecae304f7437beL, 0xf76f7d39b2c987f0L), //120
        new IPvXTuple(0x85f79287e5f9db86L, 0x43cf8657aa1650e2L), //126
        new IPvXTuple(0x14905c60625ece9L, 0x4aa3a88bd315707eL), //127
        new IPvXTuple(0x1f718ba37d21d81fL, 0x980db2cfb160afe4L), //128
        new IPvXTuple(0xeb0512f5d3b723b9L, 0x13af63c83d93e856L), //120
        new IPvXTuple(0x6620dacbdac8883eL, 0xa538cdc12fc48789L), //65
        new IPvXTuple(0xb512b0de55ecbffdL, 0x41a7b945f023ccf3L), //110
        new IPvXTuple(0x5f1fb0c8a91a1c9aL, 0x9e1cb728bfcd6f03L), //28
        new IPvXTuple(0x4c1326c3f54b41dL, 0x56e9f77d9f114f5L), //73
        new IPvXTuple(0xbc71ada57f81b7a3L, 0x7891b454887dc801L), //67
        new IPvXTuple(0x6f28804759f1f888L, 0x7b35521a1bc18bcL), //117
        new IPvXTuple(0x389aae5dcf4c4447L, 0x5edf5c283e0ae897L), //86
        new IPvXTuple(0x8320c440dec20b88L, 0x2193316214f16824L), //6
        new IPvXTuple(0x3d2677ab52d03478L, 0xaeb7a5b7448e53ceL), //101
        new IPvXTuple(0x5547ce57126b803eL, 0xbf45495b3daaca9L), //121
        new IPvXTuple(0x8160c986d2da07f8L, 0xc3a2e802d474e3f7L), //20
        new IPvXTuple(0xb3e0ef17de3cfb0cL, 0xbc0cbfead233e37bL), //57
        new IPvXTuple(0x8dff7d0bdb57577eL, 0x831b4e4312de1745L) //32
    };

    public static int[] IPV6_TEST_MASKS = new int[]{
        0, 1, 2, 8, 16, 32, 63, 64, 65, 72, 96, 112, 120, 126, 127, 128, 120, 65, 110, 28, 73, 67, 117, 86, 6, 101, 121, 20, 57, 32
    };

    public static IPvXTuple[] IPV6_TEST_LOWER = new IPvXTuple[]{
        new IPvXTuple(0x0000000000000000L, 0x0000000000000000L), //0
        new IPvXTuple(0x0L, 0x0000000000000000L), //1
        new IPvXTuple(0x0L, 0x0000000000000000L), //2
        new IPvXTuple(0x6c00000000000000L, 0x0000000000000000L), //8
        new IPvXTuple(0xd9b5000000000000L, 0x0000000000000000L), //16
        new IPvXTuple(0xc0f1e3ef00000000L, 0x0000000000000000L), //32
        new IPvXTuple(0x954238b605e14b56L, 0x0000000000000000L), //63
        new IPvXTuple(0x1d5404380ac57c3cL, 0x0000000000000000L), //64
        new IPvXTuple(0xa0bae106a80d5753L, 0x8000000000000000L), //65
        new IPvXTuple(0x82b424b06c578d1L, 0x8500000000000000L), //72
        new IPvXTuple(0x58b5cc92d782282cL, 0x9543f6c00000000L), //96
        new IPvXTuple(0x95f738212a5c13f4L, 0xdc06766d34ee0000L), //112
        new IPvXTuple(0xf6ecae304f7437beL, 0xf76f7d39b2c98700L), //120
        new IPvXTuple(0x85f79287e5f9db86L, 0x43cf8657aa1650e0L), //126
        new IPvXTuple(0x14905c60625ece9L, 0x4aa3a88bd315707eL), //127
        new IPvXTuple(0x1f718ba37d21d81fL, 0x980db2cfb160afe4L), //128
        new IPvXTuple(0xeb0512f5d3b723b9L, 0x13af63c83d93e800L), //120
        new IPvXTuple(0x6620dacbdac8883eL, 0x8000000000000000L), //65
        new IPvXTuple(0xb512b0de55ecbffdL, 0x41a7b945f0200000L), //110
        new IPvXTuple(0x5f1fb0c000000000L, 0x0000000000000000L), //28
        new IPvXTuple(0x4c1326c3f54b41dL, 0x500000000000000L), //73
        new IPvXTuple(0xbc71ada57f81b7a3L, 0x6000000000000000L), //67
        new IPvXTuple(0x6f28804759f1f888L, 0x7b35521a1bc1800L), //117
        new IPvXTuple(0x389aae5dcf4c4447L, 0x5edf5c0000000000L), //86
        new IPvXTuple(0x8000000000000000L, 0x0000000000000000L), //6
        new IPvXTuple(0x3d2677ab52d03478L, 0xaeb7a5b740000000L), //101
        new IPvXTuple(0x5547ce57126b803eL, 0xbf45495b3daac80L), //121
        new IPvXTuple(0x8160c00000000000L, 0x0000000000000000L), //20
        new IPvXTuple(0xb3e0ef17de3cfb00L, 0x0000000000000000L), //57
        new IPvXTuple(0x8dff7d0b00000000L, 0x0000000000000000L) //32
    };

    public static IPvXTuple[] IPV6_TEST_UPPER = new IPvXTuple[]{
        new IPvXTuple(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL), //0
        new IPvXTuple(0x7fffffffffffffffL, 0xFFFFFFFFFFFFFFFFL), //1
        new IPvXTuple(0x3fffffffffffffffL, 0xFFFFFFFFFFFFFFFFL), //2
        new IPvXTuple(0x6cffffffffffffffL, 0xFFFFFFFFFFFFFFFFL), //8
        new IPvXTuple(0xd9b5ffffffffffffL, 0xFFFFFFFFFFFFFFFFL), //16
        new IPvXTuple(0xc0f1e3efffffffffL, 0xFFFFFFFFFFFFFFFFL), //32
        new IPvXTuple(0x954238b605e14b57L, 0xFFFFFFFFFFFFFFFFL), //63
        new IPvXTuple(0x1d5404380ac57c3cL, 0xFFFFFFFFFFFFFFFFL), //64
        new IPvXTuple(0xa0bae106a80d5753L, 0xffffffffffffffffL), //65
        new IPvXTuple(0x82b424b06c578d1L, 0x85ffffffffffffffL), //72
        new IPvXTuple(0x58b5cc92d782282cL, 0x9543f6cffffffffL), //96
        new IPvXTuple(0x95f738212a5c13f4L, 0xdc06766d34eeffffL), //112
        new IPvXTuple(0xf6ecae304f7437beL, 0xf76f7d39b2c987ffL), //120
        new IPvXTuple(0x85f79287e5f9db86L, 0x43cf8657aa1650e3L), //126
        new IPvXTuple(0x14905c60625ece9L, 0x4aa3a88bd315707fL), //127
        new IPvXTuple(0x1f718ba37d21d81fL, 0x980db2cfb160afe4L), //128
        new IPvXTuple(0xeb0512f5d3b723b9L, 0x13af63c83d93e8ffL), //120
        new IPvXTuple(0x6620dacbdac8883eL, 0xffffffffffffffffL), //65
        new IPvXTuple(0xb512b0de55ecbffdL, 0x41a7b945f023ffffL), //110
        new IPvXTuple(0x5f1fb0cfffffffffL, 0xFFFFFFFFFFFFFFFFL), //28
        new IPvXTuple(0x4c1326c3f54b41dL, 0x57fffffffffffffL), //73
        new IPvXTuple(0xbc71ada57f81b7a3L, 0x7fffffffffffffffL), //67
        new IPvXTuple(0x6f28804759f1f888L, 0x7b35521a1bc1fffL), //117
        new IPvXTuple(0x389aae5dcf4c4447L, 0x5edf5fffffffffffL), //86
        new IPvXTuple(0x83ffffffffffffffL, 0xFFFFFFFFFFFFFFFFL), //6
        new IPvXTuple(0x3d2677ab52d03478L, 0xaeb7a5b747ffffffL), //101
        new IPvXTuple(0x5547ce57126b803eL, 0xbf45495b3daacffL), //121
        new IPvXTuple(0x8160cfffffffffffL, 0xFFFFFFFFFFFFFFFFL), //20
        new IPvXTuple(0xb3e0ef17de3cfb7fL, 0xFFFFFFFFFFFFFFFFL), //57
        new IPvXTuple(0x8dff7d0bffffffffL, 0xFFFFFFFFFFFFFFFFL) //32
    };

    public static IPvXTuple[] IPV6_TEST_HOSTS = new IPvXTuple[]{
        new IPvXTuple(0x2c16301b5b1ab83dL, 0x55cdd0e04efc1c52L), //0
        new IPvXTuple(0x7d1c5fbbf795bc0aL, 0xbfbd4b2fa6471356L), //1
        new IPvXTuple(0x28dada8e1c300c93L, 0x73bf6d02d788b033L), //2
        new IPvXTuple(0x6bb907fff8e4f2L, 0x65d4e1181d6a702bL), //8
        new IPvXTuple(0x84b3c333639bL, 0x4e8251b2c330a82cL), //16
        new IPvXTuple(0x82c6f0cL, 0x4c3886bfefcb044aL), //32
        new IPvXTuple(0x0L, 0x6d6ec06279a5d84cL), //63
        new IPvXTuple(0x0000000000000000L, 0xcf8ae2233a829361L), //64
        new IPvXTuple(0x0000000000000000L, 0x3d81968a2cd1bfe2L), //65
        new IPvXTuple(0x0000000000000000L, 0xfee19682512b4aL), //72
        new IPvXTuple(0x0000000000000000L, 0x2f4ed0aaL), //96
        new IPvXTuple(0x0000000000000000L, 0x3feaL), //112
        new IPvXTuple(0x0000000000000000L, 0xf0L), //120
        new IPvXTuple(0x0000000000000000L, 0x2L), //126
        new IPvXTuple(0x0000000000000000L, 0x0L), //127
        new IPvXTuple(0x0000000000000000L, 0x0000000000000000L), //128
        new IPvXTuple(0x0000000000000000L, 0x56L), //120
        new IPvXTuple(0x0000000000000000L, 0x2538cdc12fc48789L), //65
        new IPvXTuple(0x0000000000000000L, 0x3ccf3L), //110
        new IPvXTuple(0x8a91a1c9aL, 0x9e1cb728bfcd6f03L), //28
        new IPvXTuple(0x0000000000000000L, 0x6e9f77d9f114f5L), //73
        new IPvXTuple(0x0000000000000000L, 0x1891b454887dc801L), //67
        new IPvXTuple(0x0000000000000000L, 0xbcL), //117
        new IPvXTuple(0x0000000000000000L, 0x283e0ae897L), //86
        new IPvXTuple(0x320c440dec20b88L, 0x2193316214f16824L), //6
        new IPvXTuple(0x0000000000000000L, 0x48e53ceL), //101
        new IPvXTuple(0x0000000000000000L, 0x29L), //121
        new IPvXTuple(0x986d2da07f8L, 0xc3a2e802d474e3f7L), //20
        new IPvXTuple(0xcL, 0xbc0cbfead233e37bL), //57
        new IPvXTuple(0xdb57577eL, 0x831b4e4312de1745L) //32
    };

    public IPvXTupleTest() {
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
    public void testGetV6BitMask() throws Exception {
        System.out.println("getV6BitMask");
        for (int i = 0; i < IPV6MASK_BITS_64_0.length; i++) {
            assertEquals(IPvXTuple.getV6BitMask(IPV6MASK_BITS_64_0[i]), new IPvXTuple(IPV6MASK_VALUES_64_0[i], 0L));
        }
        for (int i = 0; i < IPV6MASK_BITS_128_65.length; i++) {
            assertEquals(IPvXTuple.getV6BitMask(IPV6MASK_BITS_128_65[i]), new IPvXTuple(IPvXTuple.IPV6_64BIT_MASK, IPV6MASK_VALUES_128_65[i]));
        }
    }

    @Test
    public void testGetV4BitMask() throws Exception {
        System.out.println("getV4BitMask");
        for (int i = 0; i < IPV4MASK_BITS.length; i++) {
            assertEquals(IPvXTuple.getV4BitMask(IPV4MASK_BITS[i]), new IPvXTuple(IPvXTuple.IPV6_64BIT_MASK, IPV4MASK_VALUES[i]));
        }
    }

    @Test
    public void testMaskWith() throws Exception {
        System.out.println("maskWithV4");
        for (int i = 0; i < IPV4_TEST_ADDRS.length; i++) {
            IPvXTuple mask = IPvXTuple.getV4BitMask(IPV4_TEST_MASKS[i]);
            IPvXTuple addr = new IPvXTuple(IPV4_TEST_ADDRS[i]);
            IPvXTuple masked = addr.maskWith(mask);
            IPvXTuple result = new IPvXTuple(IPV4_TEST_LOWER[i]);
            assertEquals(masked, result);
        }
        System.out.println("maskWithV6");
        for (int i = 0; i < IPV6_TEST_ADDRS.length; i++) {
            IPvXTuple mask = IPvXTuple.getV6BitMask(IPV6_TEST_MASKS[i]);
            IPvXTuple addr = IPV6_TEST_ADDRS[i];
            IPvXTuple masked = addr.maskWith(mask);
            IPvXTuple result = IPV6_TEST_LOWER[i];
            assertEquals(masked, result);
        }

    }

    @Test
    public void testOrWithNot() throws Exception {
        System.out.println("orWithNotV4");
        for (int i = 0; i < IPV4_TEST_ADDRS.length; i++) {
            IPvXTuple mask = IPvXTuple.getV4BitMask(IPV4_TEST_MASKS[i]);
            IPvXTuple addr = new IPvXTuple(IPV4_TEST_ADDRS[i]);
            IPvXTuple masked = addr.orWithNot(mask);
            IPvXTuple result = new IPvXTuple(IPV4_TEST_UPPER[i]);
            assertEquals(masked, result);
        }
        System.out.println("orWithNotV6");
        for (int i = 0; i < IPV6_TEST_ADDRS.length; i++) {
            IPvXTuple mask = IPvXTuple.getV6BitMask(IPV6_TEST_MASKS[i]);
            IPvXTuple addr = IPV6_TEST_ADDRS[i];
            IPvXTuple masked = addr.orWithNot(mask);
            IPvXTuple result = IPV6_TEST_UPPER[i];
            assertEquals(masked, result);
        }
    }

    @Test
    public void testMaskWithNot() throws Exception {
        System.out.println("maskWithNotV4");
        for (int i = 0; i < IPV4_TEST_ADDRS.length; i++) {
            IPvXTuple mask = IPvXTuple.getV4BitMask(IPV4_TEST_MASKS[i]);
            IPvXTuple addr = new IPvXTuple(IPV4_TEST_ADDRS[i]);
            IPvXTuple masked = addr.maskWithNot(mask);
            IPvXTuple result = new IPvXTuple(IPV4_TEST_HOSTS[i]);
            assertEquals(masked, result);
        }
        System.out.println("maskWithNotV6");
        for (int i = 0; i < IPV6_TEST_ADDRS.length; i++) {
            IPvXTuple mask = IPvXTuple.getV6BitMask(IPV6_TEST_MASKS[i]);
            IPvXTuple addr = IPV6_TEST_ADDRS[i];
            IPvXTuple masked = addr.maskWithNot(mask);
            IPvXTuple result = IPV6_TEST_HOSTS[i];
            assertEquals(masked, result);
        }
        
    }

    @Test
    public void testGetLowerBound() throws Exception {
        System.out.println("getLowerBoundV4");
        for (int i = 0; i < IPV4_TEST_ADDRS.length; i++) {
            IPvXTuple addr = new IPvXTuple(IPV4_TEST_ADDRS[i]);
            assertEquals(addr.getLowerBound(IPV4_TEST_MASKS[i]), new IPvXTuple(IPV4_TEST_LOWER[i]));
        }
        System.out.println("getLowerBoundV6");
        for (int i = 0; i < IPV6_TEST_ADDRS.length; i++) {
            IPvXTuple addr = IPV6_TEST_ADDRS[i];
            assertEquals(addr.getLowerBound(IPV6_TEST_MASKS[i]), IPV6_TEST_LOWER[i]);
        }
    }

    @Test
    public void testGetUpperBound() throws Exception {
        System.out.println("getUpperBoundV4");
        for (int i = 0; i < IPV4_TEST_ADDRS.length; i++) {
            IPvXTuple addr = new IPvXTuple(IPV4_TEST_ADDRS[i]);
            assertEquals(addr.getUpperBound(IPV4_TEST_MASKS[i]), new IPvXTuple(IPV4_TEST_UPPER[i]));
        }
        System.out.println("getUpperBoundV6");
        for (int i = 0; i < IPV6_TEST_ADDRS.length; i++) {
            IPvXTuple addr = IPV6_TEST_ADDRS[i];
            assertEquals(addr.getUpperBound(IPV6_TEST_MASKS[i]), IPV6_TEST_UPPER[i]);
        }
    }
    
    @Test
    public void testGetHostPart() throws Exception {
        System.out.println("getHostPartV4");
        for (int i = 0; i < IPV4_TEST_ADDRS.length; i++) {
            IPvXTuple addr = new IPvXTuple(IPV4_TEST_ADDRS[i]);
            assertEquals(addr.getHostPart(IPV4_TEST_MASKS[i]), new IPvXTuple(IPV4_TEST_HOSTS[i]));
        }
        System.out.println("getHostPartV6");
        for (int i = 0; i < IPV6_TEST_ADDRS.length; i++) {
            IPvXTuple addr = IPV6_TEST_ADDRS[i];
            assertEquals(addr.getHostPart(IPV6_TEST_MASKS[i]), IPV6_TEST_HOSTS[i]);
        }
    }

    @Test
    public void testIsV4Addr() {
        System.out.println("IPvXTuple: simple isV4Addr test");
        assertEquals(true, new IPvXTuple(0L, IPvXTuple.IPV4_IN_V6_LOPREFIX).isV4Addr());
        assertEquals(true, new IPvXTuple(0L, IPvXTuple.IPV4_IN_V6_LOPREFIX | 0xC0A80010L).isV4Addr());
        assertEquals(true, new IPvXTuple(0L, IPvXTuple.IPV4_IN_V6_LOPREFIX | 0xFFFFFFFFL).isV4Addr());

        assertEquals(false, new IPvXTuple(0L, 0L).isV4Addr());
        assertEquals(false, new IPvXTuple(0L, 0xFFC0A80010L).isV4Addr());
        assertEquals(false, new IPvXTuple(0L, 0xFFFFFFC0A80010L).isV4Addr());
        assertEquals(false, new IPvXTuple(0L, 0xFFFFFFFFC0A80010L).isV4Addr());

        assertEquals(false, new IPvXTuple(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFC0A80010L).isV4Addr());
        assertEquals(false, new IPvXTuple(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL).isV4Addr());

        System.out.println("IPvXTuple: simple v4 test OK");
    }

    @Test
    public void testGetHi() {
        System.out.println("getHi");
        IPvXTuple instance = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF01L);
        assertEquals(instance.getHi(), 0xF234123456785678L);
    }

    @Test
    public void testSetHi() {
        System.out.println("setHi");
        IPvXTuple instance = new IPvXTuple();
        instance.setHi(0xF234123456785678L);
        assertEquals(instance.getHi(), 0xF234123456785678L);
    }

    @Test
    public void testGetLo() {
        System.out.println("getLo");
        IPvXTuple instance = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF01L);
        assertEquals(instance.getLo(), 0xABCDABCDEF01EF01L);
    }

    @Test
    public void testSetLo() {
        System.out.println("setLo");
        IPvXTuple instance = new IPvXTuple();
        instance.setLo(0xABCDABCDEF01EF01L);
        assertEquals(instance.getLo(), 0xABCDABCDEF01EF01L);
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        IPvXTuple instance1 = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF01L);
        IPvXTuple instance2 = new IPvXTuple();
        instance2.setHi(0xF234123456785678L);
        instance2.setLo(0xABCDABCDEF01EF01L);
        assertEquals(instance1.hashCode(), instance1.hashCode());
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        IPvXTuple instance1 = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF01L);
        IPvXTuple instance2 = new IPvXTuple();
        instance2.setHi(0xF234123456785678L);
        instance2.setLo(0xABCDABCDEF01EF01L);
        assertEquals(instance1, instance2);
    }

    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        IPvXTuple instance1 = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF01L);
        IPvXTuple instance2 = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF01L);
        assertEquals(instance1.compareTo(instance2),0);
        instance2 = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF00L);
        assertTrue(instance1.compareTo(instance2)>0);
        instance2 = new IPvXTuple(0xF234123456785678L, 0xABCDABCDEF01EF03L);
        assertTrue(instance1.compareTo(instance2)<0);
        instance2 = new IPvXTuple(0xF234123456785677L, 0xABCDABCDEF01EF01L);
        assertTrue(instance1.compareTo(instance2)>0);
        instance2 = new IPvXTuple(0xF234123456785679L, 0xABCDABCDEF01EF01L);
        assertTrue(instance1.compareTo(instance2)<0);
        instance2 = new IPvXTuple(0x0234123456785679L, 0xABCDABCDEF01EF01L);        
        assertTrue(instance1.compareTo(instance2)>0);
        instance2 = new IPvXTuple(0xF234123456785678L, 0xBBCDABCDEF01EF01L);
        assertTrue(instance1.compareTo(instance2)<0);
        instance2 = new IPvXTuple(0xF234123456785678L, 0x0BCDABCDEF01EF01L);
        assertTrue(instance1.compareTo(instance2)>0);
        
        instance1 = new IPvXTuple();
        instance2 = new IPvXTuple();
        assertTrue(instance1.compareTo(instance2)==0);
        instance1 = new IPvXTuple(0xC0A80101L);
        instance2 = new IPvXTuple(0xC0A80101L);
        assertTrue(instance1.compareTo(instance2)==0);
        instance2 = new IPvXTuple(0xC0A90101L);
        assertTrue(instance1.compareTo(instance2)<0);
        instance2 = new IPvXTuple(0xC0A70101L);
        assertTrue(instance1.compareTo(instance2)>0);
        
    }

    @Test
    public void testToString() {
        System.out.println("toStringV4");
        assertEquals("0.0.0.0", new IPvXTuple(0L).toString());
        assertEquals("10.10.10.10",  new IPvXTuple(0x0A0A0A0AL).toString());
        assertEquals("172.0.0.0", new IPvXTuple(0xAC000000L).toString());
        assertEquals("192.168.10.1", new IPvXTuple(0xC0A80A01L).toString());
        assertEquals("224.224.224.224", new IPvXTuple(0xE0E0E0E0L).toString());
        assertEquals("255.255.0.0", new IPvXTuple(0xFFFF0000L).toString());
        assertEquals("255.255.255.255", new IPvXTuple(0xFFFFFFFFL).toString());
        System.out.println("toStringV6");
    }
    
    @Test
    public void testGetIntV4BitMask() throws Exception {
        System.out.println("getIntV4BitMask");
        assertEquals(0xFFFFFFFF,IPvXTuple.getIntV4BitMask(32));
        assertEquals(0xFFFFFFFE,IPvXTuple.getIntV4BitMask(31));
        assertEquals(0xFFFFFFE0,IPvXTuple.getIntV4BitMask(27));
        assertEquals(0xFFFFFF00,IPvXTuple.getIntV4BitMask(24));
        assertEquals(0xFFFF0000,IPvXTuple.getIntV4BitMask(16));
        assertEquals(0xFF000000,IPvXTuple.getIntV4BitMask(8));
        assertEquals(0xF0000000,IPvXTuple.getIntV4BitMask(4));
        assertEquals(0xC0000000,IPvXTuple.getIntV4BitMask(2));
        assertEquals(0x80000000,IPvXTuple.getIntV4BitMask(1));
        assertEquals(0x00000000,IPvXTuple.getIntV4BitMask(0));
    }
    
    @Test
    public void testGetV4AddressString() {
        System.out.println("getV4AddressString");
        assertEquals("0.0.0.0", IPvXTuple.getV4AddressString(0L));
        assertEquals("10.10.10.10", IPvXTuple.getV4AddressString(0x0A0A0A0AL));
        assertEquals("172.0.0.0", IPvXTuple.getV4AddressString(0xAC000000L));
        assertEquals("192.168.10.1", IPvXTuple.getV4AddressString(0xC0A80A01L));
        assertEquals("224.224.224.224", IPvXTuple.getV4AddressString(0xE0E0E0E0L));
        assertEquals("255.255.0.0", IPvXTuple.getV4AddressString(0xFFFF0000L));
        assertEquals("255.255.255.255", IPvXTuple.getV4AddressString(0xFFFFFFFFL));
    }

    

}
