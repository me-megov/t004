/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.entities;

import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class IPvXTupleWithMask {
    
       private IPvXTuple addr = new IPvXTuple(0, 0);
        private int mask = 0;

        public IPvXTupleWithMask(IPvXTuple _addr, int _mask) {
            addr = _addr;
            mask = _mask;
        }

        public IPvXRange getRange() throws T004BadDataException {
            return new IPvXRange(getAddr().getLowerBound(getMask()),
                    getAddr().getUpperBound(getMask()));
        }

        @Override
        public String toString() {
            return getAddr().toString() + "/" + getMask();
        }

    /**
     * @return the addr
     */
    public IPvXTuple getAddr() {
        return addr;
    }

    /**
     * @return the mask
     */
    public int getMask() {
        return mask;
    }

    
}
