/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.logprocessors.tasks;

import me.megov.emc.t004.entities.LogProcessorResult;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static java.nio.file.StandardOpenOption.READ;
import java.util.concurrent.Callable;

/**
 *
 * @author megov
 */
public class MMapParallelLogProcessorTask 
        extends AbstractParallelLogProcessorTask 
        implements Callable<LogProcessorResult> {

    public MMapParallelLogProcessorTask() {
    }
    
    @Override
    public LogProcessorResult call() throws Exception {
        FileChannel fc = FileChannel.open(params.getLogFile().toPath(), READ);
        MappedByteBuffer mbb = fc.map(
                FileChannel.MapMode.READ_ONLY,
                segment.getStartPos(),
                segment.getLength()
        );

        //BufferedInputStream bis = new BufferedInputStream(new FileInputStream(logFile));
        //bis.skip(segment.getStartPos());
        
        return new LogProcessorResult();
    }
        
    
}
