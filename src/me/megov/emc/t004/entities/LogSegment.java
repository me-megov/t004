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
public class LogSegment {

    private long startPos;
    private long length;

    public LogSegment(long _startPos, long _length) {
        this.startPos = _startPos;
        this.length = _length;
    }

    public long getNextSegmentPos() {
        return startPos + length;
    }

    /**
     * @return the length
     */
    public long getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return String.format("SEG: %08X+%08X=%08X ", startPos, length, getNextSegmentPos());
    }

    /**
     * @return the startPos
     */
    public long getStartPos() {
        return startPos;
    }

    /**
     * @param startPos the startPos to set
     */
    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

}
