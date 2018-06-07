/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.logprocessors.tasks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.READ;
import java.util.concurrent.Callable;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.LogLine;
import me.megov.emc.t004.entities.LogProcessorResult;
import me.megov.emc.t004.entities.LogStats;
import me.megov.emc.t004.parsers.LogLineParser;

/**
 *
 * @author megov
 */
public class NioLogProcessorTask
        extends AbstractParallelLogProcessorTask
        implements Callable<LogProcessorResult> {
        
    private ByteBuffer bb = ByteBuffer.allocate(8192);
    private SeekableByteChannel bch;
    private int oldBufferPos;
    private long currentPos;

    private void beginRead() throws IOException {
        currentPos = segment.getStartPos();
        oldBufferPos = 0;
        bch.read(bb);
        bb.flip();
    }

    private String readLine() throws IOException {
        if (currentPos >= segment.getNextSegmentPos()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (;;) {
            if (!bb.hasRemaining()) {
                currentPos += (bb.position() - oldBufferPos);
                oldBufferPos = 0;
                bb.compact();
                bch.read(bb);
                bb.flip();
                continue;
            }
            //unoptimal char-by-char reading
            char ch = (char) bb.get();
            if (ch == '\n') {
                break;
            }
            sb.append(ch);
        }
        currentPos += (bb.position() - oldBufferPos);
        oldBufferPos = bb.position();
        return sb.toString();
    }

    @Override
    public LogProcessorResult call() throws Exception {
        LogLineParser logParser = new LogLineParser();
        LogProcessorResult segmentResult = new LogProcessorResult();
        segmentResult.setTaskName(taskName);

        bb = ByteBuffer.allocate(64);
        bch = Files.newByteChannel(params.getLogFile().toPath(), READ);
        bch.position(segment.getStartPos());

        Customer custRoot = params.getCustomerRoot();
        
        String line;
        String lastLine="";
        long lines = 0;
        long segmentTraffic = 0;
        long unknownSegmentTraffic = 0;
        
        beginRead();
        
        while ((line=readLine())!=null) {
            
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
