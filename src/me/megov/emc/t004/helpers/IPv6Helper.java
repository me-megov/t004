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

import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class IPv6Helper {
    
    public static final long IPV6_64BIT_MASK = 0xFFFFFFFFFFFFFFFFL;

    public static IPvXTuple getV6BitMask(int _mask) throws T004BadDataException {
        if ((_mask < 0) || (_mask > 128)) {
            throw new T004BadDataException("getV6BitMask: Invalid bitmask " + _mask);
        }
        if (_mask<64) return new IPvXTuple(IPV6_64BIT_MASK, IPV6_64BIT_MASK << (64 - _mask));
        else if (_mask==64) return new IPvXTuple(IPV6_64BIT_MASK, 0);
        else if (_mask<128) return new IPvXTuple(IPV6_64BIT_MASK << (64 - _mask), 0 );
        else return new IPvXTuple(0, 0);
    }
    
    public static IPvXTuple toLowerBound(IPvXTuple v6addr, int _mask) throws T004BadDataException {
        return v6addr.maskWith(getV6BitMask(_mask));
    }
    
    public static IPvXTuple toUpperBound(IPvXTuple v6addr, int _mask) throws T004BadDataException {
        IPvXTuple bitmask = getV6BitMask(_mask);
        return (v6addr.maskWith(getV6BitMask(_mask))).orWithNot(bitmask);    
    }
    
    public static IPvXTuple toHostPart(IPvXTuple v6addr, int _mask) throws T004BadDataException {
        IPvXTuple bitmask = getV6BitMask(_mask);
        return bitmask; //(v6addr & IPV4_LO_MASK & (~bitmask) );    
    }
         
    public static IPvXTuple getRandomV6Segment(IPvXTuple _parent, int _mask) throws T004BadDataException {
        return new IPvXTuple(0, 0);
    }
    
}
