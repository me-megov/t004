/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;

import java.util.List;
import me.megov.emc.t004.augmentedtree.AUTNode;
import me.megov.emc.t004.augmentedtree.AUTree;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.IPvXRange;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.entities.RangeLookup;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class AugmentedTreeLookup implements RangeLookup{
    
    private final AUTree<Customer> tree = new AUTree();

    @Override
    public Customer getEntry(IPvXTuple _addr) throws T004BadDataException  {
        List<AUTNode<Customer>> ent = tree.containsTuple(_addr);
        switch  (ent.size()) {
            case 0: return null;
            case 1: return ent.get(0).getTag();
            default: throw new T004BadDataException("To many customers for address "+_addr);
        }
    }

    @Override
    public void put(IPvXRange _range, Customer _cust) {
        tree.addNode(new AUTNode<>(_range,_cust));
    }
    
}
