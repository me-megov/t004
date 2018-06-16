/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;

import java.io.PrintWriter;

/**
 *
 * @author megov
 */
public class ProcessingStatsWriter {

    public static void writeStats(ProcessingStatsResults _stats, PrintWriter _bw) {
        double logProcPerf = 0;
        if (_stats.getLogProcessingMillis() > 0) {
            logProcPerf = _stats.getStats().getTotalLines() * 1000 / _stats.getLogProcessingMillis();
        }

        _bw.format("Total processing time in millis:     %d\n", _stats.getCustReadingMillis()
                + _stats.getLogProcessingMillis()
                + _stats.getResultStoredMillis());
        _bw.format("Log processing performance in rec/s: %.2f\n", logProcPerf);
        _bw.format("Log processing time in millis:       %d\n", _stats.getLogProcessingMillis());
        _bw.format("Source log lines:      %d\n", _stats.getStats().getTotalLines());
        _bw.format("Source log file size:  %d\n", _stats.getLogFileSize());
        _bw.format("Source total traffic:  %d\n", _stats.getStats().getTotalTraffic());
        _bw.format("Result lines:          %d\n", _stats.getResultTotalTrafficRecords());
        _bw.format("Result total traffic:  %d\n", _stats.getResultTotalBytesStored());
        _bw.format("Traffic accounting:    %s\n", 
                (_stats.getResultTotalBytesStored()==_stats.getStats().getTotalTraffic())
                ?"SUCCESS":"FAILED");

        _bw.format("Customers count:       %d\n", _stats.getCustCount());
        _bw.format("Customers file size:   %d\n", _stats.getCustFileSize());
        _bw.format("Customers tree depth:  %d\n", _stats.getCustLevels());

    }

}
