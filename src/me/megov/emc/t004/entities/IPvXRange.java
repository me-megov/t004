/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.entities;

/**
 *
 * @author megov
 */
public class IPvXRange {
    
    private IPvXTuple lowerBound;
    private IPvXTuple upperBound;    

    public IPvXRange(IPvXTuple _lowerBound, IPvXTuple _upperBound) {
        this.lowerBound = _lowerBound;
        this.upperBound = _upperBound;
    }
    /**
     * @return the lowerBound
     */
    public IPvXTuple getLowerBound() {
        return lowerBound;
    }

    /**
     * @param lowerBound the lowerBound to set
     */
    public void setLowerBound(IPvXTuple lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * @return the upperBound
     */
    public IPvXTuple getUpperBound() {
        return upperBound;
    } 

    /**
     * @param upperBound the upperBound to set
     */
    public void setUpperBound(IPvXTuple upperBound) {
        this.upperBound = upperBound;
    }
    
    @Override
    public String toString() {
        return "["+lowerBound+" - "+upperBound+"]";
    } 
    
    public boolean isIntersectWith(IPvXRange _range) {
        boolean isBefore = 
                 (lowerBound.compareTo(_range.getLowerBound())>0) && 
                 (lowerBound.compareTo(_range.getUpperBound())>0);
        boolean isAfter = 
                 (upperBound.compareTo(_range.getLowerBound())<0) && 
                 (upperBound.compareTo(_range.getUpperBound())<0);
        
        return (!(isBefore||isAfter));
    }
    
}
