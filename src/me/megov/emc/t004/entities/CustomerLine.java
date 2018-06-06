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

import me.megov.emc.t004.parsers.IPvXAddrParser;
import com.google.common.collect.Range;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.exceptions.T004FormatException;

/**
 *
 * @author megov
 */
public class CustomerLine {
    
    private final String name;
    private final IPvXAddrParser netAddr;
    private final IPvXTuple lowerAddr;
    private final IPvXTuple upperAddr;
    private final int mask;
    
    public CustomerLine(String _netAddr, int _mask, String _name) throws T004Exception {
        this.name = _name;
        this.mask = _mask;
        this.netAddr = new IPvXAddrParser(_netAddr);
        this.lowerAddr = this.netAddr.getAddr().getLowerAddr(mask);
        if (!this.lowerAddr.equals(this.netAddr.getAddr())) {
            throw new T004FormatException("Customer address "+_netAddr+" is not specifies network "+lowerAddr.toString());
        }
        this.upperAddr = this.netAddr.getAddr().getUpperAddr(mask);
    }  

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the ipaddrRange
     */
    public IPvXTuple getLowerAddr() {
        return lowerAddr;
    }
    
    public IPvXTuple getUpperAddr() {
        return upperAddr;
    }

    /**
     * @return the ipaddrRange
     */
    public Range<IPvXTuple> getNetRange() {
        return Range.closed(lowerAddr,upperAddr);
    }    

    /**
     * @return the mask
     */
    public int getMask() {
        return mask;
    }
    
}
