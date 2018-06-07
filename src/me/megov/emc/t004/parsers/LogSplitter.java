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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import me.megov.emc.t004.entities.LogSegment;
import static me.megov.emc.t004.helpers.FmtHelper.DF;

/**
 *
 * @author megov
 */
public class LogSplitter {
    
    public LogSplitter() {
    }
    
    public List<LogSegment> analyze(File _logFile, int _partsCount, PrintStream _ps) throws Exception {
        List<LogSegment> segments = new ArrayList<>(_partsCount);
        try (RandomAccessFile raf = new RandomAccessFile(_logFile, "r")) {
            long logLength = _logFile.length();
            long baseSegmentSize = logLength/_partsCount;
        
            if (_ps!=null) {
                _ps.println("Log size: "+DF.format(logLength)+" baseSegmentSize: "+DF.format(baseSegmentSize));
            }

            long startSegmentPos = 0 ;
            while (startSegmentPos<logLength) {
                raf.seek(startSegmentPos+baseSegmentSize);
                raf.readLine();
                long correctedEndSegnmentPos = raf.getFilePointer();
                long correctedSegmentSize = correctedEndSegnmentPos-startSegmentPos;
                segments.add(new LogSegment(startSegmentPos, correctedSegmentSize));
                startSegmentPos+=correctedSegmentSize;
            }
        
            if (_ps!=null) {
                for (LogSegment seg:segments) {
                    _ps.println(seg.toString());
                }
            }
            
        }
        return segments;
    }
    
}
