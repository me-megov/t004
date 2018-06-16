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
package me.megov.emc.t004.helpers;

import java.io.PrintStream;
import java.util.List;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.entities.IPvXTupleWithMask;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class LogGenerator { 

    public LogGenerator() {
    }

    public long generateLog(long _totalBytes,
            int _maxBytesPerLine,
            List<IPvXTupleWithMask> _netList,
            boolean _ipv4,
            int _unknownAddrRate,
            int _debugLogRate,
            PrintStream _ps,
            PrintStream _debugOut) throws T004BadDataException {
        long counter = 0;
        IPvXTuple fullAddr;
        long remainBytes = _totalBytes;

        while (remainBytes > 0) {
              if (_ipv4) {
                    fullAddr = IPvXTuple.getV4RandomAddr();
                } else {
                    fullAddr = IPvXTuple.getV6RandomAddr();
                }            
            if ((counter % _unknownAddrRate) != 0) {
                int idx = (int) Math.round(Math.random()*_netList.size()*1.2);
                if (idx>=_netList.size()) idx = idx-_netList.size();
                IPvXTupleWithMask gotAddr = _netList.get(idx);
                IPvXTuple net = gotAddr.getAddr().getLowerBound(gotAddr.getMask());
                fullAddr = net.orWith(fullAddr.getHostPart(gotAddr.getMask()));
            }

            if ((_debugOut != null) && ((counter % (_debugLogRate))==0) ) {
                    _debugOut.println("..." + counter + " lines and " + remainBytes + " bytes remaining");
            }
            
            int bytes = (int) Math.round(Math.random() * _maxBytesPerLine);
            if ((long) bytes > remainBytes) {
                bytes = (int) remainBytes;
            }
            _ps.println(fullAddr.toString() + " " + bytes);
            remainBytes -= bytes;
            counter++;
        }
        return counter;
    }

}
