/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.augmentedtree;

import me.megov.emc.t004.entities.IPvXRange;
import me.megov.emc.t004.entities.IPvXTuple;

/**
 *
 * @author megov
 * @param <T>
 */

public class AUTNode<T extends Object> implements Comparable<AUTNode> {
        
    IPvXRange bounds;
    IPvXTuple maxRight;
    
    private T tag;
    
    AUTNode left;
    AUTNode right;

    public AUTNode(IPvXRange _bounds, T _tag) {
        this.bounds = _bounds;
        this.maxRight = _bounds.getUpperBound();
        this.tag = _tag;
        this.right = null;
        this.left = null;
    }
    
    public AUTNode(IPvXTuple _lowerBound, IPvXTuple _upperBound, T _tag) {
        this(new IPvXRange(_lowerBound,_upperBound),_tag);
    }

    @Override
    public int compareTo(AUTNode o) {
        if (this.bounds.getLowerBound().compareTo(o.bounds.getLowerBound())<0) {
            return -1;
        }
        else if (this.bounds.getLowerBound().equals(o.bounds.getLowerBound())) {
            return (this.bounds.getUpperBound().compareTo(o.bounds.getUpperBound())<=0) ? -1 : 1;
        }
        else {
            return 1;
        }
    }     
    
    
    @Override
    public String toString() {
        return getTag()+" ["+this.bounds+"] M:"+this.maxRight;
    }

    /**
     * @return the tag
     */
    public T getTag() {
        return tag;
    }
    
}
