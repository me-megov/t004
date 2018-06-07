/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.entities;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author megov
 */
public class LogProcessorResult {
    
    private String taskName;
    private Map<String, Long> trafficResult = new HashMap<>();
    private LogStats stats = new LogStats();
    
    public LogProcessorResult() {
    }
    
    /**
     * @return the trafficResult
     */
    public Map<String, Long> getTrafficResult() {
        return trafficResult;
    }

    /**
     * @param trafficResult the trafficResult to set
     */
    public void setTrafficResult(Map<String, Long> trafficResult) {
        this.trafficResult = trafficResult;
    }

    /**
     * @return the stats
     */
    public LogStats getStats() {
        return stats;
    }

    /**
     * @param stats the stats to set
     */
    public void setStats(LogStats stats) {
        this.stats = stats;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public void merge(LogProcessorResult _serResults) {
        stats.merge(_serResults.getStats());
        _serResults.getTrafficResult().entrySet().stream().forEach((_item) -> {
            Long bytes = trafficResult.getOrDefault(_item.getKey(), 0L); 
            bytes+=_item.getValue();
            trafficResult.put(_item.getKey(), bytes);
            });
        }
            
    
}
