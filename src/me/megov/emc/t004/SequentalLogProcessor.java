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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Map;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.LogLine;
import me.megov.emc.t004.entities.LogStats;
import me.megov.emc.t004.parsers.LogLineParser;


/**
 *
 * @author megov
 */
public class SequentalLogProcessor implements LogProcessor {
    
    private static final int REPORT_PERIOD = 1000000;
    
     @Override
     public long process(File _logFile, 
                         Customer _custroot, 
                         Map <String,Long> _result, 
                         LogStats _stats,
                         PrintStream _debugOut) throws Exception {
        long unknownTraffic = 0L;
        long totalTraffic = 0L;
        LogLineParser logParser = new LogLineParser();
        FileReader fr = new FileReader(_logFile);
        BufferedReader logReader = new BufferedReader(fr);
        //RandomAccessFile logReader = new RandomAccessFile(_logFile, "r");
        _result.clear();
        try {
            String line;
            long lines = 0;
            while ((line = logReader.readLine()) != null) {
                LogLine logLine = logParser.parseLine(lines+1, line);
                lines+=1L;  
                totalTraffic+=logLine.getByteCount();
                Customer cust = null; // _custroot.getCustomerByAddr(logLine.getAddr());
                if (cust!=null) {
                    Long nowTraffic = _result.get(cust.getName());
                    if (nowTraffic==null) nowTraffic = logLine.getByteCount();
                    else nowTraffic += logLine.getByteCount();
                    _result.put(cust.getName(), nowTraffic);
                } else unknownTraffic+=logLine.getByteCount();
                if ((_debugOut!=null) && ((lines % REPORT_PERIOD)==0)) {
                    _debugOut.println("..."+lines+" lines and "+totalTraffic+" bytes processed");
                }
            }
            _stats.setTotalLines(lines);
            _stats.setTotalTraffic(totalTraffic);
            _stats.setTotalCustomersCount(_result.size());
            _stats.setTotalUnknownTraffic(unknownTraffic);
            return totalTraffic;
        } finally {
            logReader.close();
        }
    }
}
