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
import java.util.Map;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.LogStats;
import me.megov.emc.t004.parsers.LogSplitter;

/**
 *
 * @author megov
 */
public class ParallelLogProcessor implements LogProcessor {
    
    private LogSplitter logSplitter = new LogSplitter();

    @Override
    public long process(File _logFile, 
                        Customer _custroot, 
                        Map<String, Long> _result, 
                        LogStats _stats,
                        PrintStream _debugOut) throws Exception {
        logSplitter.analyze(_logFile, 5, _debugOut);
        return 0;
    }
    
}
