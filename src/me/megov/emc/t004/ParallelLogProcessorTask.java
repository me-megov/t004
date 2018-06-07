/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;


import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import me.megov.emc.t004.entities.LogLine;
import me.megov.emc.t004.entities.LogSegment;
import me.megov.emc.t004.parsers.LogLineParser;

/**
 *
 * @author megov
 */
public class ParallelLogProcessorTask implements Callable<Map<String, Long>> {

    private File logFile;
    private LogSegment segment;
    private String taskName; 
    private int reportPeriod;
    private PrintStream debugOut;
    
    public ParallelLogProcessorTask(String _taskName, 
                                    File _logFile, 
                                    LogSegment _segment,
                                    int _reportPeriod,
                                    PrintStream _debugOut) {
        this.logFile = _logFile;
        this.segment = _segment;
        this.taskName = _taskName;
        this.reportPeriod = _reportPeriod;
        this.debugOut = _debugOut;
    }
    
    @Override
    public Map<String, Long> call() throws Exception {
//        CharSource cs = Files.asCharSource(logFile, Charset.forName("UTF-8"));
        RandomAccessFile raf = new RandomAccessFile(logFile, "r");
        LogLineParser logParser = new LogLineParser();
        raf.seek(segment.getStartPos());
        String line;
        long lines = 0;
        while ((line = raf.readLine()) != null) {
                LogLine logLine = logParser.parseLine(lines+1, line);
                lines+=1L;          
                if (raf.getFilePointer()>(segment.getNextSegmentPos())) {
                    break;
                }
                if ((debugOut!=null) && ((lines % reportPeriod)==0)) {
                    debugOut.println("..."+taskName+":"+lines+" lines ");
                }                
        }
        return new HashMap<>();
    }
    
}
