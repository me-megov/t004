/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.logprocessors.tasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.LogLine;
import me.megov.emc.t004.parsers.LogLineParser;
import me.megov.emc.t004.entities.LogProcessorResult;
import me.megov.emc.t004.entities.LogStats;

/**
 *
 * @author megov
 */
public class RandFileParallelLogProcessorTask
        extends AbstractParallelLogProcessorTask
        implements Callable<LogProcessorResult> {

    public RandFileParallelLogProcessorTask() {
    }

    @Override
    public LogProcessorResult call() throws Exception {
        LogLineParser logParser = new LogLineParser();
        LogProcessorResult segmentResult = new LogProcessorResult();
        segmentResult.setTaskName(taskName);
        RandomAccessFile randFile = new RandomAccessFile(params.getLogFile(), "r");
        FileReader fr = new FileReader(randFile.getFD());
        randFile.seek(segment.getStartPos());
        BufferedReader buffReader = new BufferedReader(fr);
        Customer custRoot = params.getCustomerRoot();
        String line;
        String lastLine="";
        long lines = 0;
        long segmentTraffic = 0;
        long unknownSegmentTraffic = 0;
        while ((line = buffReader.readLine()) != null) {
            LogLine logLine = logParser.parseLine(lines + 1, line);

            if (params.isDebug() && (lines == 0)) {
                params.getDebugOut().println("..." + taskName + ": FIRST:" + line);
            }

            lines += 1L;
            lastLine = line;
            
            segmentTraffic += logLine.getByteCount();
            Customer cust = custRoot.getCustomerByAddr(logLine.getAddr());
            if (cust != null) {
                Long nowTraffic = segmentResult.getTrafficResult().get(cust.getName());
                if (nowTraffic == null) {
                    nowTraffic = logLine.getByteCount();
                } else {
                    nowTraffic += logLine.getByteCount();
                }
                segmentResult.getTrafficResult().put(cust.getName(), nowTraffic);
            } else {
                unknownSegmentTraffic += logLine.getByteCount();
            }

            //ERROR - randfile position move forward by lookAhead
            //and task do not read all segment lines!
            if (randFile.getFilePointer()>(segment.getNextSegmentPos())) {
                break;
            }
            
            if (params.isDebug() && ((lines % params.getReportInterval()) == 0)) {
                params.getDebugOut().println("..." + taskName + ":" + lines + " lines ");
            }
        }
        if (params.isDebug()) {
            params.getDebugOut().println("..." + taskName + ": LAST:" + lastLine);
        }

        LogStats ls = segmentResult.getStats();
        ls.setTotalLines(lines);
        ls.setTotalTraffic(segmentTraffic);
        ls.setTotalCustomersCount(segmentResult.getTrafficResult().size());
        ls.setTotalUnknownTraffic(unknownSegmentTraffic);
        return segmentResult;
    }

}
