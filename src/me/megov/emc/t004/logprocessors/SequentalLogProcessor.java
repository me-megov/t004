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

import java.io.BufferedReader;
import java.io.FileReader;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.LogLine;
import me.megov.emc.t004.entities.LogProcessorParams;
import me.megov.emc.t004.entities.LogProcessorResult;
import me.megov.emc.t004.entities.LogStats;
import me.megov.emc.t004.parsers.LogLineParser;


/**
 *
 * @author megov
 */
public class SequentalLogProcessor implements LogProcessor {
    
     @Override
     public LogProcessorResult process(LogProcessorParams _params) throws Exception {
        
        long unknownTraffic = 0L;
        long totalTraffic = 0L;
        LogLineParser logParser = new LogLineParser();
        LogProcessorResult result = new LogProcessorResult();
        Customer custRoot = _params.getCustomerRoot();
        
        FileReader fr = new FileReader(_params.getLogFile());
        BufferedReader logReader = new BufferedReader(fr);
        try {
            String line;
            long lines = 0;
            while ((line = logReader.readLine()) != null) {
                
                LogLine logLine = logParser.parseLine(lines+1, line);
                lines+=1L;  
                totalTraffic+=logLine.getByteCount();
                
                Customer cust = custRoot.getCustomerByAddr(logLine.getAddr());
                if (cust!=null) {
                    Long nowTraffic = result.getTrafficResult().get(cust.getName());
                    if (nowTraffic==null) nowTraffic = logLine.getByteCount();
                    else nowTraffic += logLine.getByteCount();
                    result.getTrafficResult().put(cust.getName(), nowTraffic);
                } else {
                    unknownTraffic+=logLine.getByteCount();
                }
                
                if ((_params.isDebug()) && ((lines % _params.getReportInterval())==0)) {
                    _params.getDebugOut().println("..."+lines+" lines and "+totalTraffic+" bytes processed");
                }
                
            }
            
            LogStats ls = result.getStats();
            ls.setTotalLines(lines);
            ls.setTotalTraffic(totalTraffic);
            ls.setTotalCustomersCount(result.getTrafficResult().size());
            ls.setTotalUnknownTraffic(unknownTraffic);
            return result;
            
        } finally {
            logReader.close();
        }
    }
}
