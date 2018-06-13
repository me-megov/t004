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

import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class IPv4Helper {

    public static final long IPV4_LO_MASK = 0x00000000FFFFFFFFL;
    public static final long IPV4_IN_V6_LOPREFIX = 0x0000FFFF00000000L;

    public static String getV4AddressString(long v4) {
        Long l = v4 & IPV4_LO_MASK;
        StringBuilder sb = new StringBuilder(15);   
        for (int i = 24; i >= 0; i -= 8) {
            sb.append((l >> i) & 0xFF);
            if (i != 0) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public static int getV4BitMask(int _mask) throws T004BadDataException {
        if ((_mask < 0) || (_mask > 32)) {
            throw new T004BadDataException("getV4BitMask: Invalid bitmask " + _mask);
        }
        if (_mask>0) return 0xFFFFFFFF << (32 - _mask);
        else return 0x0;
    }

    public static long toLowerBound(long v4addr, int _mask) throws T004BadDataException {
        return (v4addr & IPV4_LO_MASK & getV4BitMask(_mask));
    }
    
    public static long toUpperBound(long v4addr, int _mask) throws T004BadDataException {
        int bitmask = getV4BitMask(_mask);
        return (v4addr & IPV4_LO_MASK & bitmask) | (~bitmask);    
    }
    
    public static long toHostPart(long v4addr, int _mask) throws T004BadDataException {
        int bitmask = getV4BitMask(_mask);
        return (v4addr & IPV4_LO_MASK & (~bitmask) );    
    }
      
    
    public static long toLowerBoundMappedToV6(long v4addr, int _mask) throws T004BadDataException {
        return toLowerBound(v4addr, _mask) | IPV4_IN_V6_LOPREFIX;
    }
    
    public static long toUpperBoundMappedToV6(long v4addr, int _mask) throws T004BadDataException {
        return toUpperBound(v4addr, _mask) | IPV4_IN_V6_LOPREFIX;    
    }
    
    
}
