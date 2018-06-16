/*
 * Copyright 2018 megov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.megov.emc.t004;

import me.megov.emc.t004.logprocessors.LogProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.megov.emc.t004.config.Config;
import me.megov.emc.t004.config.ConfigParam;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.CustomerLine;
import me.megov.emc.t004.entities.LogProcessorParams;
import me.megov.emc.t004.entities.LogProcessorResult;
import me.megov.emc.t004.entities.RangeLookupFactory;
import me.megov.emc.t004.exceptions.T004FormatException;
import static me.megov.emc.t004.helpers.FmtHelper.DF;
import me.megov.emc.t004.logprocessors.ParallelLogProcessor;
import me.megov.emc.t004.logprocessors.SequentalLogProcessor;
import me.megov.emc.t004.logprocessors.tasks.NioLogProcessorTask;
import me.megov.emc.t004.logprocessors.tasks.MMapParallelLogProcessorTask;
import me.megov.emc.t004.logprocessors.tasks.RandFileParallelLogProcessorTask;
import me.megov.emc.t004.parsers.CustomerParser;

/**
 *
 * @author megov
 * 
 * TODO:
 *  - add tests to other modules
 *  - profiling for memory and speed(?)
 * 
 * UNIMPLEMENTED:
 *  - IPv6 in RangeTree 
 *  - IPv6 in Generators
 *  - MemoryMap 
 * 
 */

public class Main {
    
    private static LogProcessorResult processLog(
                                               LogProcessor _logProcessor, 
                                               LogProcessorParams _params) throws Exception {
        
        long ctStart = System.currentTimeMillis();
        
        File fl = _params.getLogFile();
        System.out.println("Reading log from " + fl.getCanonicalPath());
        if (!(fl.exists() && fl.isFile())) {
            throw new IOException("No log file: " + fl.getCanonicalPath());
        }
        
        LogProcessorResult lpResult = _logProcessor.process(_params);
        
        long ctEnd = System.currentTimeMillis();
        System.out.println(
                    String.format("Done reading log in %d millis, got %s log lines",
                    ctEnd - ctStart,
                    DF.format(lpResult.getStats().getTotalLines()))
                    );
        System.out.println("Got "+DF.format(lpResult.getTrafficResult().size())+
                          " customer records for "+DF.format(lpResult.getStats().getTotalTraffic())+" bytes");
        return lpResult;
    }
    
    private static Customer readCustomerTree(String _dataDir, 
                                             String _fileName, 
                                             RangeLookupFactory _lookupFactory) throws Exception {
        long ctStart = System.currentTimeMillis();
        File custfile = new File(_dataDir, _fileName);
        System.out.println("Reading customer tree from " + custfile.getCanonicalPath());
        if (!(custfile.exists() && custfile.isFile())) {
            throw new IOException("No customer file: " + _fileName);
        }
        Customer custroot = Customer.getRootCustomer(_lookupFactory);
        CustomerParser custParser = new CustomerParser();
        BufferedReader custReader = new BufferedReader(new FileReader(custfile));
        List<CustomerLine> listCl = custParser.readFrom(custReader);
        for (CustomerLine cl: listCl) custroot.addSubCustomer(cl);
        long ctEnd = System.currentTimeMillis();
        System.out.println(
                    String.format("Done reading in %d millis, got %s customers",
                    ctEnd - ctStart,
                    DF.format(custroot.getSubCustomersCount()))
                    );        
        return custroot;
    }

    private static long storeOutput(String _dataDir,
                                    String _fileName,
                                    LogProcessorResult _lpResult) throws Exception {
        long soStart = System.currentTimeMillis();
        long totalTraffic = _lpResult.getStats().getTotalUnknownTraffic();
        File resultfile = new File(_dataDir, _fileName);
        File tempfile = new File(_dataDir, _fileName+".tmp");
        System.out.println("Writing results to " + resultfile.getCanonicalPath());
        PrintWriter bw = new PrintWriter(new FileWriter(tempfile));
        try {
            if ( !tempfile.canWrite() ) {
                throw new IOException("Cant write result file: " + _fileName);
            }        
            List<String> keys = new ArrayList<>(_lpResult.getTrafficResult().keySet());
            Collections.sort(keys);
            for (String custName:keys) {
                Long bytes = _lpResult.getTrafficResult().get(custName);
                totalTraffic+=bytes;
                bw.println(custName+" "+bytes.toString());
            }
            bw.println("UNKNOWN "+_lpResult.getStats().getTotalUnknownTraffic());
            bw.flush();
        } finally {
            bw.close();
        }
        tempfile.renameTo(resultfile);  
        return totalTraffic;
    }
    
    public static void main(String[] args) {
        
        String cfgFileName = null;

        if ((args.length > 0) && (args[0].startsWith("--cfgFile"))) {
            String arr[] = args[0].split("=");
            if (arr.length==2) {
                cfgFileName = arr[1];
            }
        }

        System.out.println("Starting with config: " + (cfgFileName == null ? "DEFAULT" : cfgFileName));

        Config cfg;
        try {
            if (cfgFileName != null) {
                cfg = new Config(cfgFileName);
            } else {
                cfg = new Config();
            }
            
            cfg.mergeCmdLine(args);
            
            String logProcessorName = cfg.getValue(ConfigParam.LOG_PROCESSOR);
            String logProcessorTaskName = cfg.getValue(ConfigParam.LOG_PROCESSOR_TASK);
            String logProcessorLookupName = cfg.getValue(ConfigParam.LOG_PROCESSOR_LOOKUP);
            int taskCount = cfg.getIntValue(ConfigParam.TASK_COUNT);

            String dataDir = cfg.getValue(ConfigParam.DATADIR);
            String outDir = cfg.getValue(ConfigParam.OUTPUTDIR);
          
            Class logProcessorClass;
            Class logProcessorTaskClass = null;
            RangeLookupFactory lookupFactory = new TreeRangeMapLookupFactory();
            
            if ("TRM".equals(logProcessorLookupName)) {
                lookupFactory = new TreeRangeMapLookupFactory();
            } else {
                lookupFactory = new AugmentedTreeLookupFactory();
            }
            
            if ("SEQ".equals(logProcessorName)) {
                logProcessorClass = SequentalLogProcessor.class;
                logProcessorTaskName = "BUF";
                taskCount = 0;
            } else if ("PAR".equals(logProcessorName)) {
                logProcessorClass = ParallelLogProcessor.class;
                if ("BUF".equals(logProcessorTaskName)) {
                    logProcessorTaskClass = RandFileParallelLogProcessorTask.class;
                } else if ("NIO".equals(logProcessorTaskName)) {
                    logProcessorTaskClass = NioLogProcessorTask.class;
                } else if ("MMAP".equals(logProcessorTaskName)) {
                    logProcessorTaskClass = MMapParallelLogProcessorTask.class;
                } else {
                    throw new T004FormatException("Bad log processor task in config: "+logProcessorTaskName);
                }
            } else {
                throw new T004FormatException("Bad log processor in config: "+logProcessorName);
            }

            System.out.println("Using log processor:"+logProcessorName+
                               ", log fetcher task:"+logProcessorTaskName+
                               ", log lookup:"+logProcessorLookupName);
            System.out.println("Using data directory: " + dataDir);
            System.out.println("Using output directory: " + outDir);
            
            Customer custRoot = readCustomerTree(dataDir,
                    cfg.getValue(ConfigParam.CUSTOMERFILE),
                    lookupFactory
            );
            
            LogProcessor lp = (LogProcessor)logProcessorClass.newInstance();

            LogProcessorParams lpp = new LogProcessorParams();
            lpp.setLogFile(new File(dataDir,cfg.getValue(ConfigParam.LOGFILE)))
               .setCustomerRoot(custRoot)
               .setDebugOut(System.out)
               .setReportInterval(cfg.getIntValue(ConfigParam.REPORT_INTERVAL))
               .setTaskCount(cfg.getIntValue(ConfigParam.TASK_COUNT))
               .setTaskClass(logProcessorTaskClass);
            
            LogProcessorResult lpResult = processLog(lp, lpp);
            
            long totalTraffic = storeOutput(
                    outDir, 
                    cfg.getValue(ConfigParam.OUTPUTFILE)+
                            "."+logProcessorName+taskCount+
                            "."+logProcessorTaskName+
                            "."+logProcessorLookupName,
                    lpResult
                    );
            System.out.println("Total traffic accounted: " + DF.format(totalTraffic));

        } catch (Throwable th) {
            System.err.println(th.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

}
