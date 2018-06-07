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
package me.megov.emc.t004;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.LogSegment;
import me.megov.emc.t004.entities.LogStats;
import me.megov.emc.t004.parsers.LogSplitter;

/**
 *
 * @author megov
 */
public class ParallelLogProcessor implements LogProcessor {
    
    private LogSplitter logSplitter = new LogSplitter();
    
    private static int EXEC_COUNT = 4;
    private static final int REPORT_PERIOD = 1000000;

    @Override
    public long process(File _logFile, 
                        Customer _custroot, 
                        Map<String, Long> _result, 
                        LogStats _stats,
                        PrintStream _debugOut) throws Exception {
        
        List<LogSegment> segments = logSplitter.analyze(_logFile, EXEC_COUNT, _debugOut);
        
        ExecutorService es = Executors.newFixedThreadPool(EXEC_COUNT);
        List<Future<Map <String,Long>>> workerList = new ArrayList<>();
        for (LogSegment segment: segments.toArray(new LogSegment[]{}) ) {
            workerList.add(
                    es.submit(
                            new ParallelLogProcessorTask(
                                    String.format("T%08X",segment.getStartPos()),
                                    _logFile,
                                    segment,
                                    REPORT_PERIOD,
                                    _debugOut)
                    )
            );
        }
        //
        es.shutdown();
        try {
            while (es.awaitTermination(10, TimeUnit.SECONDS)==false)
            {
                int iterm=0;
                for (Future<Map <String,Long>> future:workerList) {
                    if (future.isDone()) {
                        try {
                            future.get();
                        } catch (ExecutionException ex) {
                            throw ex;
                        }
                        iterm++;
                    }
                }
            }
        } catch (InterruptedException  ex) {
            throw ex;
        } finally {
            es.shutdownNow();
        }
        return 0;
    }
    
}
