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
            long _network,
            int _mask,
            int _unknownAddrRate,
            int _debugLogRate,
            PrintStream _ps,
            PrintStream _debugOut) throws T004BadDataException {
        long counter = 0;
        long fullAddr;
        long remainBytes = _totalBytes;

        while (remainBytes > 0) {
            if ((counter % _unknownAddrRate) == 0) {
                fullAddr = Math.round(Math.random() * IPv4Helper.IPV4_LO_MASK);
            } else {
                long host = IPv4Helper.toHostPart(Math.round(Math.random() * IPv4Helper.getV4BitMask(_mask)),_mask);
                fullAddr = _network + host;
            }

            if ((_debugOut != null) && ((counter % (_debugLogRate))==0) ) {
                    _debugOut.println("..." + counter + " lines and " + remainBytes + " bytes remaining");
            }
            
            int bytes = (int) Math.round(Math.random() * _maxBytesPerLine);
            if ((long) bytes > remainBytes) {
                bytes = (int) remainBytes;
            }
            _ps.println(IPv4Helper.getV4AddressString(fullAddr) + " " + bytes);
            remainBytes -= bytes;
            counter++;
        }
        return counter;
    }

}
