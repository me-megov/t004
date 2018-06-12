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
import me.megov.emc.t004.helpers.IPv4Helper;
import me.megov.emc.t004.helpers.IPv6Helper;

/**
 *
 * @author megov
 */
public class IPvXTuple implements Comparable<IPvXTuple> {

    private long hi;
    private long lo;

    public IPvXTuple(long _hi, long _lo) {
        this.hi = _hi;
        this.lo = _lo;
    }
    
    public IPvXTuple(IPvXTuple _tu) {
        this.hi = _tu.hi;
        this.lo = _tu.lo;
    }
    

    /**
     * @return the hi
     */
    public long getHi() {
        return hi;
    }

    /**
     * @param hi the hi to set
     */
    public void setHi(long hi) {
        this.hi = hi;
    }

    /**
     * @return the lo
     */
    public long getLo() {
        return lo;
    }

    /**
     * @param lo the lo to set
     */
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
        if (this.hi < o.hi) {
            return -1;
        } else if (this.hi > o.hi) {
            return 1;
        } else if (this.lo < o.lo) {
            return -1;
        } else if (this.lo > o.lo) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isV4Addr() {
        return ((hi == 0L) && ((lo & (~IPv4Helper.IPV4_LO_MASK)) == IPv4Helper.IPV4_IN_V6_LOPREFIX));
    }

    private IPvXTuple getV6BitMask(int _mask) throws T004BadDataException {
        if ((_mask < 0) || (_mask > 128)) {
            throw new T004BadDataException("getV6BitMask: Invalid bitmask " + _mask);
        }
        if (_mask < 64) {
            return new IPvXTuple(0xFFFFFFFFFFFFFFFFL << (64 - _mask), 0L);
        } else if (_mask < 64) {
            return new IPvXTuple(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL << (128 - _mask));
        } else {
            return new IPvXTuple(0xFFFFFFFFFFFFFFFFL, 0L);
        }
    }

    public IPvXTuple getLowerAddr(int _mask) throws T004BadDataException {
        if (isV4Addr()) {
            return new IPvXTuple(0, IPv4Helper.toLowerBoundMappedToV6(this.lo, _mask));
        } else {
            IPvXTuple mask = getV6BitMask(_mask);
            return new IPvXTuple(this.hi & mask.getHi(), this.lo & mask.getLo());
        }
    }

    public IPvXTuple getUpperAddr(int _mask) throws T004BadDataException {
        if (isV4Addr()) {
            return new IPvXTuple(0, IPv4Helper.toUpperBoundMappedToV6(this.lo, _mask));
        } else {
            IPvXTuple mask = getV6BitMask(_mask);
            return new IPvXTuple(this.hi & mask.getHi() | (~mask.getHi()), this.lo & mask.getLo() | (~mask.getLo()));
        }
    }

    @Override
    public String toString() {
        if (isV4Addr()) {
            return IPv4Helper.getV4AddressString(lo);
        } else {
            return getV6AddressString(false);
        }
    }

    private String getV6AddressString(boolean _isCompressed) {
        StringBuilder sb = new StringBuilder(15);
        Long l = hi;

        boolean skipMode = false;
        for (int i = 48; i >= 0; i -= 16) {
            int iItem = (int) (l >> i) & 0xFFFF;
            if (_isCompressed) {
                if (iItem == 0) {
                    skipMode = true;
                } else {
                    if (skipMode == true) {
                        sb.append(":");
                    }
                    skipMode = false;
                }
            }
            if (!skipMode) {
                sb.append(String.format("%X", iItem));
                sb.append(":");
            }
        }
        l = lo;
        for (int i = 48; i >= 0; i -= 16) {
            int iItem = (int) (l >> i) & 0xFFFF;
            if (_isCompressed) {
                if (iItem == 0) {
                    skipMode = true;
                } else {
                    if (skipMode == true) {
                        sb.append(":");
                    }
                    skipMode = false;
                }
            }
            if (!skipMode) {
                sb.append(String.format("%X", iItem));
                if (i != 0) {
                    sb.append(":");
                }
            }
        }
        return sb.toString();
    }

}
