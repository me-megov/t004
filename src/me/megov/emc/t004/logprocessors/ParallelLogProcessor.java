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
package me.megov.emc.t004.logprocessors;

import me.megov.emc.t004.logprocessors.tasks.AbstractParallelLogProcessorTask;
import me.megov.emc.t004.entities.LogProcessorResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import me.megov.emc.t004.entities.LogProcessorParams;
import me.megov.emc.t004.entities.LogSegment;
import me.megov.emc.t004.exceptions.T004BadDataException;
import me.megov.emc.t004.parsers.LogSplitter;

/**
 *
 * @author megov
 */
public class ParallelLogProcessor implements LogProcessor {

    @Override
    public LogProcessorResult process(LogProcessorParams _params) throws Exception {

        List<LogSegment> segments = LogSplitter.analyze(_params);
        LogProcessorResult totalLPResult = new LogProcessorResult();

        if (_params.getTaskClass() == null) {
            throw new T004BadDataException("No task class specified for parallel processing");
        }

        ExecutorService es = Executors.newFixedThreadPool(_params.getTaskCount());
        List<AbstractParallelLogProcessorTask> workerList = new ArrayList<>();
        for (LogSegment segment : segments.toArray(new LogSegment[]{})) {
            AbstractParallelLogProcessorTask task = (AbstractParallelLogProcessorTask) _params.getTaskClass().newInstance();
            task.setParams(_params)
                    .setSegment(segment);
            workerList.add(task);
        }
        //
        try {
            for (Future<LogProcessorResult> future : es.invokeAll(workerList)) {
                if (future.isDone()) {
                    try {
                        LogProcessorResult segmentResult = future.get();
                        if (_params.isDebug()) {
                            _params.getDebugOut().println("Done: " + segmentResult.getTaskName());
                        }
                        totalLPResult.merge(segmentResult);
                    } catch (ExecutionException ex) {
                        throw ex;
                    }
                }
            }
        } catch (InterruptedException ex) {
            throw ex;
        } finally {
            es.shutdownNow();
        }
        return totalLPResult;
    }

}
