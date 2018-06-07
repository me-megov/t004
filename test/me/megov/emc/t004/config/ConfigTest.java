package me.megov.emc.t004.config;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class ConfigTest {
    
    public static List<String> completeTestConfig = new ArrayList<>(Arrays.asList(
            new String[] {
                    "dataDir                /testdata",
                    "customerFile             testcustomers.txt",
                    "logFile              testlog.txt",
                    "outputDir      /testoutput",
                    "outputFile     testreport.txt",
                    "isDebug   1" ,
                    "reportInterval 1000000",
                    "taskCount 12",
                    "logProcessor PAR",
                    "logProcessorTask MMAP"
                    
                }));
    
    public static List<String> badTestConfig0 = new ArrayList<>(Arrays.asList(
            new String[] {
                    "NOPARAM0    some.txt"
                }));
    
    public static List<String> badTestConfig1 = new ArrayList<>(Arrays.asList(
            new String[] {
                    "NOPARAM1    "
                }));
    
    
    public ConfigTest() {
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
    public void customConfigTest() throws Exception {
        System.out.println("Config: custom test");
        Config cfg = new Config(completeTestConfig);
        if (!"/testdata".equals(cfg.getValue(ConfigParam.DATADIR))) throw new Exception();
        if (!"/testoutput".equals(cfg.getValue(ConfigParam.OUTPUTDIR))) throw new Exception();
        if (!"testcustomers.txt".equals(cfg.getValue(ConfigParam.CUSTOMERFILE))) throw new Exception();
        if (!"testlog.txt".equals(cfg.getValue(ConfigParam.LOGFILE))) throw new Exception();        
        if (!"testreport.txt".equals(cfg.getValue(ConfigParam.OUTPUTFILE))) throw new Exception();
        System.out.println("Config: custom test OK");
    }
    
    @Test
    public void bad0ConfigTest() throws Exception {
        System.out.println("Config: bad0 test");
        try {
            Config cfg = new Config(badTestConfig0);
        } catch (T004FormatException cex) {
            System.out.println("Config: bad0 test OK");
        }
    }
    
    @Test
    public void bad1ConfigTest() throws Exception {
        System.out.println("Config: bad1 test");
        try {
            Config cfg = new Config(badTestConfig1);
        } catch (T004FormatException cex) {
            System.out.println("Config: bad1 test OK");
        }
    }
    
    @Test
    public void defaultConfigTest() throws Exception {
        System.out.println("Config: default test");
        Config cfg = new Config(new String[]{});
        System.out.println("Config: default test OK");
    }
    
    
    
}
