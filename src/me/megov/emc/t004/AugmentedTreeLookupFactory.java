/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;

import me.megov.emc.t004.entities.RangeLookup;
import me.megov.emc.t004.entities.RangeLookupFactory;

/**
 *
 * @author megov
 */
public class AugmentedTreeLookupFactory implements RangeLookupFactory {

    @Override
    public RangeLookup getNewLookup() {
        return new AugmentedTreeLookup();
    }
    
}
