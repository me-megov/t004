/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.util.List;
import java.util.Map;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.IPvXRange;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.entities.RangeLookup;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class TreeRangeMapLookup implements RangeLookup {
    
    private final RangeMap<IPvXTuple,Customer> subCustomersRanges = TreeRangeMap.create();

    @Override
    public Customer getEntry(IPvXTuple _addr) throws T004BadDataException {
        Map.Entry<Range<IPvXTuple>,Customer> custRanges = subCustomersRanges.getEntry(_addr);
        if (custRanges!=null) {
            return custRanges.getValue();
        } else {
            return null;
        }
    }

    @Override
    public void put(IPvXRange _range, Customer _cust) {
        subCustomersRanges.put(Range.closed(_range.getLowerBound(), _range.getUpperBound()), _cust);
    }
    
}
