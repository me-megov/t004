/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;

import me.megov.emc.t004.entities.LogStats;

/**
 *
 * @author megov
 */
public class ProcessingStatsResults {
    
    private long custReadingMillis=0;
    private long custCount=0;
    private int  custLevels=0;
    private long custFileSize=0;
    
    private long logFileSize=0;
    private long logProcessingMillis=0;
    
    private long resultTotalTrafficRecords=0;
    private long resultStoredMillis=0;
    private long resultTotalBytesStored=0;    

    private LogStats stats = new LogStats();

    public long getCustReadingMillis() {
        return custReadingMillis;
    }

    public ProcessingStatsResults setCustReadingMillis(long custReadingMillis) {
        this.custReadingMillis = custReadingMillis;
        return this;
    }

    public long getCustCount() {
        return custCount;
    }

    public ProcessingStatsResults setCustCount(long custCount) {
        this.custCount = custCount;
        return this;
    }

    public long getCustLevels() {
        return custLevels;
    }

    public ProcessingStatsResults setCustLevels(int custLevels) {
        this.custLevels = custLevels;
        return this;
    }

    public long getCustFileSize() {
        return custFileSize;
    }

    public ProcessingStatsResults setCustFileSize(long custFileSize) {
        this.custFileSize = custFileSize;
        return this;
    }

    public long getLogFileSize() {
        return logFileSize;
    }

    public ProcessingStatsResults setLogFileSize(long logFileSize) {
        this.logFileSize = logFileSize;
        return this;
    }

    public long getLogProcessingMillis() {
        return logProcessingMillis;
    }

    public ProcessingStatsResults setLogProcessingMillis(long logProcessingMillis) {
        this.logProcessingMillis = logProcessingMillis;
        return this;
    }

    public LogStats getStats() {
        return stats;
    }

    public ProcessingStatsResults setStats(LogStats stats) {
        this.stats = stats;
        return this;
    }

    public long getResultTotalTrafficRecords() {
        return resultTotalTrafficRecords;
    }

    public ProcessingStatsResults setResultTotalTrafficRecords(long resultTotalTrafficRecords) {
        this.resultTotalTrafficRecords = resultTotalTrafficRecords;
        return this;
    }

    public long getResultStoredMillis() {
        return resultStoredMillis;
    }

    public ProcessingStatsResults setResultStoredMillis(long resultStoredMillis) {
        this.resultStoredMillis = resultStoredMillis;
        return this;
    }

    public long getResultTotalBytesStored() {
        return resultTotalBytesStored;
    }

    public ProcessingStatsResults setResultTotalBytesStored(long resultTotalBytesStored) {
        this.resultTotalBytesStored = resultTotalBytesStored;
        return this;
    }
    
    
}
