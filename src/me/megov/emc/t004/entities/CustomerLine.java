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

import java.util.Objects;
import me.megov.emc.t004.parsers.IPvXAddrParser;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.exceptions.T004FormatException;

/**
 *
 * @author megov
 */
public class CustomerLine {
    
    private final String name;
    private final IPvXTuple netAddr;
    private final IPvXTuple lowerBound;
    private final IPvXTuple upperBound;
    private final int mask;

    @Override
    public int hashCode() {
        int hash = 3;
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
        final CustomerLine other = (CustomerLine) obj;
        if (this.mask != other.mask) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.netAddr, other.netAddr)) {
            return false;
        }
        if (!Objects.equals(this.lowerBound, other.lowerBound)) {
            return false;
        }
        if (!Objects.equals(this.upperBound, other.upperBound)) {
            return false;
        }
        return true;
    }
    
    public CustomerLine(String _netAddr, int _mask, String _name) throws T004Exception {
        this.name = _name;
        this.mask = _mask;
        this.netAddr = IPvXAddrParser.parseAddress(_netAddr);
        this.lowerBound = this.netAddr.getLowerBound(mask);
        if (!this.lowerBound.equals(this.netAddr)) {
            throw new T004FormatException("Customer address "+_netAddr+" is not specifies network "+lowerBound.toString());
        }
        this.upperBound = this.netAddr.getUpperBound(mask);
    }  

    public String getName() {
        return name;
    }

    public IPvXTuple getLowerBound() {
        return lowerBound;
    }
    
    public IPvXTuple getUpperBound() {
        return upperBound;
    }

    public IPvXRange getNetRange() {
        return new IPvXRange(lowerBound, upperBound);
    }    

    public int getMask() {
        return mask;
    }
    
    
}
