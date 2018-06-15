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
package me.megov.emc.t004.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.entities.LogLine;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.exceptions.T004FormatException;

/**
 *
 * @author megov
 */
public class LogLineParser {
    
    private static final String LOG_REGEXP = "^\\s*(\\S+)\\s+([0-9]+)\\s*$";
    private static final Pattern LOG_PATTERN = Pattern.compile(LOG_REGEXP);    
    
    public LogLine parseLine(long lineNum, String line) throws T004Exception {
        
        try {
            Matcher mat = LOG_PATTERN.matcher(line);
            if (!mat.matches() && (mat.groupCount()!=2)) {
                throw new T004FormatException("Invalid log line format at line "+lineNum+". Log line: "+line);
            }
            IPvXTuple netAddr = IPvXAddrParser.parseAddress(mat.group(1));
            return new LogLine(netAddr, 
                               Long.parseLong(mat.group(2),10));
        } catch (IllegalStateException | IndexOutOfBoundsException  ex) {
            throw new T004FormatException("Error parsing log at line "+lineNum+". Log line: "+line, ex);
        }
    }
    
    
}
