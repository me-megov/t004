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

import me.megov.emc.t004.entities.LogLine;
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
public class LogLineParserTest {

    public static String[] goodLogLines = new String[]{
        "  10.10.10.10   4455   ",
        "  10.10.10.0   1234567890123456   ",
        "192.168.0.1 10000",
        ":: 123",
        "::1   345",
        "FF01::101  4588888",
        "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789 321432543654765",
        "2001:DB8:0:0:8:800:200C:417A   \t 1 ",
        "FF01:0:0:0:0:0:0:101  444",
        "0:0:0:0:0:0:0:1 555",
        "0:0:0:0:0:0:0:0 777",
        "2001:DB8::8:800:200C:417A 888"
    };
    
    public static String[] badLogLines = new String[]{
        "  10.10.10.10   445Z5   ",
        "  10.10.1.0.   1234567890123456   ",
        "192.168.0:1 10000 ",
        "192.168.0,1 10000  234 ",        
        ":: 123",
        "::1   345",
        "FF01::101  4588888",
        "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789 321432543654765",
        "2001:DB8:0:0:8:800:200C:417A   \t 1 ",
        "FF01:0:0:0:0:0:0:101  444",
        "0:0:0:0:0:0:0:1 555",
        "0:0:0:0:0:0:0:0 777",
        "2001:DB8::8:800:200C:417A 888"
    };    

    private final LogLineParser lp = new LogLineParser();

    public LogLineParserTest() {
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
    public void testParseGoodLogLines() throws Exception {
        System.out.println("parseGoodLogLines");
        for (int i = 0; i < goodLogLines.length; i++) {
            LogLine ll = lp.parseLine(i + 1, goodLogLines[i]);
        }
    }
    
    @Test
    public void testParseBadLogLines() throws Exception {
        System.out.println("parseBadLogLines");
        for (int i = 0; i < badLogLines.length; i++) {
            try {
                LogLine ll = lp.parseLine(i + 1, badLogLines[i]);
            } catch (T004FormatException ex) {
            }
        }
    }
    

}
