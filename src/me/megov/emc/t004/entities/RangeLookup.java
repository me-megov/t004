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
public interface RangeLookup {
    
    public Customer getEntry(IPvXTuple _addr) throws T004BadDataException ;
    public void put(IPvXRange _range, Customer _cust);
    
}
