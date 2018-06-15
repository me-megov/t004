/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.parsers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.MathContext;
import java.util.List;
import me.megov.emc.t004.entities.LogProcessorParams;
import me.megov.emc.t004.entities.LogSegment;
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
public class LogSplitterTest {
    
    public static String filename = "/tmp/splitTest";
    
    public LogSplitterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException {
        BufferedWriter wr = new BufferedWriter(new FileWriter(filename));
        for (int i=0; i<10000; i++) {
            String str = Long.toHexString(Math.round(Math.random() * Long.MAX_VALUE) + Math.round(Math.random() * 256));
            int iEnd = (int)Math.round(str.length()*Math.random())-1;
            if (iEnd>1) {
                str = str.substring(1, iEnd);
            }
            wr.write(str);
            wr.newLine();
        }
        wr.flush();
        wr.close();
    }
    
    @After
    public void tearDown() {
        new File(filename).delete();
    }

    @Test
    public void testAnalyze() throws Exception {
        System.out.println("analyze");
        LogProcessorParams _params = new LogProcessorParams();
        _params.setLogFile(new File(filename));
        _params.setTaskCount(10);
        List<LogSegment> result = LogSplitter.analyze(_params);
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        try {
            long lastPos=0;
            long newPos;
            for (LogSegment ls:result) {
                newPos = ls.getStartPos();
                if (lastPos!=newPos) {
                    throw new Exception("Non continuous segment at: "+lastPos);
                }
                if (newPos>0) {
                    raf.seek(newPos-1);
                    int s = raf.read();
                    int s1 = raf.read();
                    if (s!='\n') {
                        throw new Exception("No newline before pos: "+Long.toHexString(newPos));
                    }
                    System.out.println("pos="+newPos+
                                       " charBefore=0x"+Integer.toHexString(s)+
                                       " charAt="+Character.toString((char)s1));
                }
                
                lastPos = ls.getNextSegmentPos();
            }
            System.out.println("lastLength test");
            assertEquals(result.get(result.size()-1).getNextSegmentPos(), raf.length());
            System.out.println("lastLength test OK");
        } finally {
            raf.close();
        }
        
    }
    
}
