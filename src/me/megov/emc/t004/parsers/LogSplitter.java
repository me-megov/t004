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

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import me.megov.emc.t004.entities.LogProcessorParams;
import me.megov.emc.t004.entities.LogSegment;
import static me.megov.emc.t004.helpers.FmtHelper.DF;

/**
 *
 * @author megov
 */
public class LogSplitter {
    
    private static final int NOSPLIT_LIMIT_KB = 1;//256;
     
    public static List<LogSegment> analyze(LogProcessorParams _params) throws Exception {
        List<LogSegment> segments = new ArrayList<>(_params.getTaskCount());
        try (RandomAccessFile raf = new RandomAccessFile(_params.getLogFile(), "r")) {
            long logLength = raf.length();
            long baseSegmentSize = logLength/_params.getTaskCount();
            
            //check minimum limit to split log
            if (logLength<_params.getTaskCount()*NOSPLIT_LIMIT_KB*1024) {
                baseSegmentSize = logLength;
            }
        
            if (_params.isDebug()) {
                _params.getDebugOut().println("Log size: "+DF.format(logLength)+" baseSegmentSize: "+DF.format(baseSegmentSize));
            }

            long startSegmentPos = 0 ;
            long correctedEndSegnmentPos;
            long correctedSegmentSize;
            
            while (startSegmentPos<logLength) {
                long newPosition = startSegmentPos+baseSegmentSize;
                if (raf.length()>newPosition) {
                    raf.seek(newPosition);
                    raf.readLine();
                    correctedEndSegnmentPos = raf.getFilePointer();
                    correctedSegmentSize = correctedEndSegnmentPos-startSegmentPos;
                } else {
                    correctedEndSegnmentPos = raf.length();
                    correctedSegmentSize = correctedEndSegnmentPos-startSegmentPos;
                }
                segments.add(new LogSegment(startSegmentPos, correctedSegmentSize));
                startSegmentPos+=correctedSegmentSize;
            }
        
            if (_params.isDebug()) {
                for (LogSegment seg:segments) {
                    _params.getDebugOut().println(seg.toString());
                }
            }
            
        }
        
        return segments;
    }
    
}
