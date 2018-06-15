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
package me.megov.emc.t004.entities;

import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class IPvXTuple implements Comparable<IPvXTuple> {
    
    public static final long IPV6_64BIT_MASK = 0xFFFFFFFFFFFFFFFFL;
    public static final long IPV4_LO_MASK = 0x00000000FFFFFFFFL;
    public static final long IPV4_IN_V6_LOPREFIX = 0x0000FFFF00000000L;

    private long hi;
    private long lo;
    private int v6GroupsSkip = 0;
    
    public IPvXTuple() {
        this.hi = 0L;
        this.lo = 0L;
    }

    public IPvXTuple(long _hi, long _lo) {
        this.hi = _hi;
        this.lo = _lo;
    }
    
    public IPvXTuple(long _v4) {
        this.hi = 0L;
        this.lo = (_v4 & IPV4_LO_MASK) | IPV4_IN_V6_LOPREFIX;
    }
    
    
    public IPvXTuple(long _hi, long _lo, int _v6GroupsSkip) {
        this.hi = _hi;
        this.lo = _lo;
        this.v6GroupsSkip = _v6GroupsSkip;
    }
    
    public IPvXTuple(IPvXTuple _tu) {
        this.hi = _tu.hi;
        this.lo = _tu.lo;
    }

    public long getHi() {
        return hi;
    }

    public void setHi(long hi) {
        this.hi = hi;
    }

    public long getLo() {
        return lo;
    }

    public void setLo(long lo) {
        this.lo = lo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (int) (this.hi ^ (this.hi >>> 32));
        hash = 61 * hash + (int) (this.lo ^ (this.lo >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IPvXTuple other = (IPvXTuple) obj;
        if (this.hi != other.hi) {
            return false;
        }
        if (this.lo != other.lo) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(IPvXTuple o) {
        if (Long.compareUnsigned(this.hi, o.hi)<0) {
            return -1;
        } else if (Long.compareUnsigned(this.hi, o.hi)>0) {
            return 1;
        } else if (Long.compareUnsigned(this.lo, o.lo)<0) {
            return -1;
        } else if (Long.compareUnsigned(this.lo, o.lo)>0) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isV4Addr() {
        return ((hi == 0L) && ((lo & (~IPV4_LO_MASK)) == IPV4_IN_V6_LOPREFIX));
    }
    
    public IPvXTuple maskWith(IPvXTuple mask) {
        return new IPvXTuple(this.hi & mask.hi, this.lo & mask.lo);
    }
    
    public IPvXTuple orWith(IPvXTuple mask) {
        return new IPvXTuple(this.hi | mask.hi, this.lo | mask.lo);
    }
    
    public IPvXTuple maskWithNot(IPvXTuple mask) {
        if (isV4Addr()) {
            return new IPvXTuple(this.hi & (~mask.hi), (this.lo & (~mask.lo)) | IPV4_IN_V6_LOPREFIX );
        } else {
            return new IPvXTuple(this.hi & (~mask.hi), this.lo & (~mask.lo));
        }
    }

    public IPvXTuple orWithNot(IPvXTuple v6mask) {
        return new IPvXTuple(this.hi | (~v6mask.hi), this.lo | (~v6mask.lo));
    }
    
    public IPvXTuple getHostPart(int _mask) throws T004BadDataException {
        if (isV4Addr()) {
            IPvXTuple mask = getV4BitMask(_mask);
            return maskWithNot(mask);
        } else {
            IPvXTuple mask = getV6BitMask(_mask);
            return maskWithNot(mask);
        }
    }
        
    public IPvXTuple getLowerBound(int _mask) throws T004BadDataException {
        if (isV4Addr()) {
            IPvXTuple mask = getV4BitMask(_mask);
            return maskWith(mask);
        } else {
            IPvXTuple mask = getV6BitMask(_mask);
            return maskWith(mask);
        }
    }

    public IPvXTuple getUpperBound(int _mask) throws T004BadDataException {
        if (isV4Addr()) {
            IPvXTuple mask = getV4BitMask(_mask);
            return orWithNot(mask);
        } else {
            IPvXTuple mask = getV6BitMask(_mask);
            return orWithNot(mask);
        }
    }

    @Override
    public String toString() {
        if (isV4Addr()) {
            return getV4AddressString(lo);
        } else {
            return getV6AddressString();
        }
    }
//-------------------------------------------------------------
    private String getV6AddressString() {
        StringBuilder sb = new StringBuilder(15);
        int iElem;
        boolean isSkip;
        boolean oldSkip=false;
        for (int i=7; i>=0; i--) {
            if (i>=4) {
                iElem = (int)((this.hi >> (16*(i-4))) & 0xFFFF);
            } else {
                iElem = (int)((this.lo >> (16*i)) & 0xFFFF);
            }
            isSkip = (v6GroupsSkip & (1<<i))!=0;
            if ((!oldSkip) && (isSkip)) {       //beginning of compressed part
                sb.append("::");
            } else if ((oldSkip) && (!isSkip)) { //ending of compressed part
                sb.append(String.format("%X", iElem));
            } else if ((!oldSkip) && (!isSkip)) {
                int lastCharIndex = sb.length()-1;
                if ((lastCharIndex>=0) && (sb.charAt(lastCharIndex)!=':')) {
                    sb.append(":");
                }
                sb.append(String.format("%X", iElem));
            }
            oldSkip = isSkip;
        }
        return sb.toString();
    }        

    public static IPvXTuple getV6BitMask(int _mask) throws T004BadDataException {
        if ((_mask < 0) || (_mask > 128)) {
            throw new T004BadDataException("getV6BitMask: Invalid bitmask " + _mask);
        }
        if (_mask==0) return new IPvXTuple(0L, 0L);
        else if (_mask<64) return new IPvXTuple(IPV6_64BIT_MASK << (64 - _mask), 0L);
        else if (_mask==64) return new IPvXTuple(IPV6_64BIT_MASK, 0);
        else if (_mask<128) return new IPvXTuple(IPV6_64BIT_MASK, IPV6_64BIT_MASK << (64 - _mask) );
        else return new IPvXTuple(IPV6_64BIT_MASK, IPV6_64BIT_MASK);
    }
    
    public static int getIntV4BitMask(int _mask) throws T004BadDataException {
        if ((_mask < 0) || (_mask > 32)) {
            throw new T004BadDataException("getV4BitMask: Invalid bitmask " + _mask);
        }
        if (_mask>0) return 0xFFFFFFFF << (32 - _mask);
        else return 0x0;
    }
    
    public static IPvXTuple getV4BitMask(int _mask) throws T004BadDataException {
        if ((_mask < 0) || (_mask > 32)) {
            throw new T004BadDataException("getV4BitMask: Invalid bitmask " + _mask);
        }
        return new IPvXTuple(IPV6_64BIT_MASK,  IPV6_64BIT_MASK << (32 - _mask));
    }
    
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

}
