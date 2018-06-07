/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.entities;

import java.io.File;
import java.io.PrintStream;

/**
 *
 * @author megov
 */
public class LogProcessorParams {

    private File logFile = null;
    private Customer customerRoot = null;
    private Class taskClass = null; //for parallel processing only
    private PrintStream debugOut = null;
    private int reportInterval = 1000000;
    private int taskCount = 4;

    public File getLogFile() {
        return logFile;
    }

    public boolean isDebug() {
        return debugOut != null;
    }

    public LogProcessorParams setLogFile(File logFile) {
        this.logFile = logFile;
        return this;
    }

    public Customer getCustomerRoot() {
        return customerRoot;
    }

    public LogProcessorParams setCustomerRoot(Customer customerRoot) {
        this.customerRoot = customerRoot;
        return this;
    }

    public Class getTaskClass() {
        return taskClass;
    }

    public LogProcessorParams setTaskClass(Class taskClass) {
        this.taskClass = taskClass;
        return this;
    }

    public PrintStream getDebugOut() {
        return debugOut;
    }

    public LogProcessorParams setDebugOut(PrintStream debugOut) {
        this.debugOut = debugOut;
        return this;
    }

    /**
     * @return the reportInterval
     */
    public int getReportInterval() {
        return reportInterval;
    }

    public LogProcessorParams setReportInterval(int reportInterval) {
        this.reportInterval = reportInterval;
        return this;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public LogProcessorParams setTaskCount(int taskCount) {
        this.taskCount = taskCount;
        return this;
    }

}
