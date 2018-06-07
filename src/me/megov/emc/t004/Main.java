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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.megov.emc.t004.config.Config;
import me.megov.emc.t004.config.ConfigParam;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.CustomerLine;
import me.megov.emc.t004.entities.LogStats;
import static me.megov.emc.t004.helpers.FmtHelper.DF;
import me.megov.emc.t004.parsers.CustomerParser;

/**
 *
 * @author megov
 * 
 * TODO:
 *  - ParallelLogProcessor with splitting log by N parts and processing in different threads
 *  + Generators - for generating really big files to test
 *  + sorting by netmask or some other way to ensure proper relations in customers
 *  - Fix IPv6 in Range F*CKUP or migrate to InetAddr/byte[]/BigInt
 *  - add tests to other modules
 *  + missing unknown customers counting = NullPointerException
 *  - profiling for memory and speed(?)
 * 
 */

public class Main {
    
    private static Map<String,Long> processLog(LogProcessor _logProcessor, 
                                               Customer _custroot, 
                                               String _dataDir, 
                                               String _fileName,
                                               LogStats _stats,
                                               PrintStream _debugOut) throws Exception {
        long ctStart = System.currentTimeMillis();
        File logfile = new File(_dataDir, _fileName);
        System.out.println("Reading log from " + logfile.getCanonicalPath());
        if (!(logfile.exists() && logfile.isFile())) {
            throw new IOException("No log file: " + _fileName);
        }
        HashMap<String,Long> hmResult = new HashMap<>();
        long totalTraffic = _logProcessor.process(logfile, _custroot, hmResult, _stats, _debugOut);
        long ctEnd = System.currentTimeMillis();
        System.out.println(
                    String.format("Done reading log in %d millis, got %s log lines",
                    ctEnd - ctStart,
                    DF.format(_stats.getTotalLines()))
                    );
        System.out.println("Got "+DF.format(hmResult.size())+
                          " customer records for "+DF.format(totalTraffic)+" bytes");
        return Collections.unmodifiableMap(hmResult);
    }
    
    private static Customer readCustomerTree(String _dataDir, String _fileName) throws Exception {
        long ctStart = System.currentTimeMillis();
        File custfile = new File(_dataDir, _fileName);
        System.out.println("Reading customer tree from " + custfile.getCanonicalPath());
        if (!(custfile.exists() && custfile.isFile())) {
            throw new IOException("No customer file: " + _fileName);
        }
        Customer custroot = Customer.getRootCustomer();
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

    private static long storeOutput(Map<String,Long> _result, 
                                    String _dataDir, 
                                    String _fileName,
                                    LogStats _stats) throws Exception {
        long soStart = System.currentTimeMillis();
        long totalTraffic = _stats.getTotalUnknownTraffic();
        File resultfile = new File(_dataDir, _fileName);
        File tempfile = new File(_dataDir, _fileName+".tmp");
        System.out.println("Writing results to " + resultfile.getCanonicalPath());
        PrintWriter bw = new PrintWriter(new FileWriter(tempfile));
        try {
            if ( !tempfile.canWrite() ) {
                throw new IOException("Cant write result file: " + _fileName);
            }        
            List<String> keys = new ArrayList<>(_result.keySet());
            Collections.sort(keys);
            for (String custName:keys) {
                Long bytes = _result.get(custName);
                totalTraffic+=bytes;
                bw.println(custName+" "+bytes.toString());
            }
            bw.println("UNKNOWN "+_stats.getTotalUnknownTraffic());
            bw.flush();
        } finally {
            bw.close();
        }
        tempfile.renameTo(resultfile);  
        return totalTraffic;
    }
    
    public static void main(String[] args) {
        
        String cfgFileName = null;
        String logProcessorName = me.megov.emc.t004.SequentalLogProcessor.class.getCanonicalName();
        //String logProcessorName = me.megov.emc.t004.ParallelLogProcessor.class.getCanonicalName();

        if (args.length > 0) {
            logProcessorName = args[0];
        }
        
        if (args.length > 1) {
            cfgFileName = args[1];
        }

        System.out.println("Starting with config: " + (cfgFileName == null ? "DEFAULT" : cfgFileName));
        System.out.println("Using " + logProcessorName);

        Config cfg;
        try {
            if (cfgFileName != null) {
                cfg = new Config(cfgFileName);
            } else {
                cfg = new Config();
            }
            System.out.println("Using data directory: " + cfg.getValue(ConfigParam.DATADIR));
            System.out.println("Using output directory: " + cfg.getValue(ConfigParam.OUTPUTDIR));
            Customer custroot = readCustomerTree(
                    cfg.getValue(ConfigParam.DATADIR),
                    cfg.getValue(ConfigParam.CUSTOMERFILE)
            );
            
            Class cl = Class.forName(logProcessorName);
            LogProcessor lp = (LogProcessor)cl.newInstance();
            
            LogStats stats = new LogStats();
            Map<String,Long> result = processLog(
                    lp,
                    custroot,
                    cfg.getValue(ConfigParam.DATADIR),
                    cfg.getValue(ConfigParam.LOGFILE),
                    stats,
                    System.out
                    );
            
            long totalTraffic = storeOutput(result, 
                    cfg.getValue(ConfigParam.OUTPUTDIR),
                    cfg.getValue(ConfigParam.OUTPUTFILE),
                    stats
                    );
            System.out.println("Total traffic accounted: " + DF.format(totalTraffic));

        } catch (Throwable th) {
            System.err.println(th.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

}
