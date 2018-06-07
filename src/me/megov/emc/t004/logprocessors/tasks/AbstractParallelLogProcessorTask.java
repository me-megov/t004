/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.logprocessors.tasks;


import java.util.concurrent.Callable;
import me.megov.emc.t004.entities.LogProcessorParams;
import me.megov.emc.t004.entities.LogSegment;
import me.megov.emc.t004.entities.LogProcessorResult;

/**
 *
 * @author megov
 */
public abstract class AbstractParallelLogProcessorTask implements Callable<LogProcessorResult> {

    protected LogProcessorParams params;
    protected LogSegment segment;
    protected String taskName;
    
    public AbstractParallelLogProcessorTask() {
    }

    public LogProcessorParams getParams() {
        return params;
    }

    public AbstractParallelLogProcessorTask setParams(LogProcessorParams _params) {
        this.params = _params;
        return this;
    }

    public LogSegment getSegment() {
        return segment;
    }

    public AbstractParallelLogProcessorTask setSegment(LogSegment _segment) {
        this.segment = _segment;
        this.taskName = String.format("T%08X", segment.getStartPos());
        return this;
    }

    public String getTaskName() {
        return taskName;
    }

    public AbstractParallelLogProcessorTask setTaskName(String _taskName) {
        this.taskName = _taskName;
        return this;
    }

}
