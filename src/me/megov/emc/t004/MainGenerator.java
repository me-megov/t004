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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.entities.IPvXTupleWithMask;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.helpers.CustomerGenerator;
import me.megov.emc.t004.helpers.CustomerTreeHelper;
import me.megov.emc.t004.helpers.LogGenerator;

/**
 *
 * @author megov
 */
public class MainGenerator {

    private static final DecimalFormat DF = new DecimalFormat("#,##0");

    public static final int DEFAULT_MAX_BYTES_PER_LOG_LINE = 10000;
    public static final int DEFAULT_COMPLETELY_RANDOM_ADDR_RATE = 100;
    public static final int DEFAULT_PROGRESS_REPORT = 1000000;

    public static List<String> generateCustomers(
            int _custCount, 
            Path _outputFile, 
            List<IPvXTupleWithMask> _v4netList,
            List<IPvXTupleWithMask> _v6netList,
            PrintStream _debugOut) throws T004Exception, IOException {
        long ctStart = System.currentTimeMillis();
        System.out.println("Generating " + _custCount + " top-customers to " + _outputFile.toString());
        List<String> custStrings = CustomerTreeHelper
                .generateCustomerList(
                        _custCount, 
                        _custCount, 
                        _v4netList,
                        _v6netList,
                        _debugOut);
        Files.write(_outputFile, custStrings, Charset.defaultCharset());
        long ctEnd = System.currentTimeMillis();
            System.out.println(
                    String.format("Done in %d millis, got %s customers, file size %s",
                    ctEnd - ctStart,
                    DF.format(custStrings.size()),
                    DF.format(_outputFile.toFile().length()))
            );
        return custStrings;
    }

    public static long generateLog(
            long _totalTraffic, 
            Path _outputFile, 
            List<IPvXTupleWithMask> _v4netList,
            List<IPvXTupleWithMask> _v6netList,
            PrintStream _debugOut) throws T004Exception, FileNotFoundException {
        long ctStart = System.currentTimeMillis();
        System.out.println("Generating " + DF.format(_totalTraffic) + " bytes traffic to " + _outputFile.toString());
        LogGenerator lg = new LogGenerator();
        PrintStream ps = new PrintStream(_outputFile.toFile());
        try {
            long totalV4Traffic = _totalTraffic/2;
            long totalV6Traffic = _totalTraffic-totalV4Traffic;
            long totalLinesV4 = lg.generateLog(totalV4Traffic,
                    DEFAULT_MAX_BYTES_PER_LOG_LINE,
                    _v4netList,
                    true,
                    DEFAULT_COMPLETELY_RANDOM_ADDR_RATE,
                    DEFAULT_PROGRESS_REPORT,
                    ps,
                    _debugOut
            );
            long totalLinesV6 = lg.generateLog(totalV4Traffic,
                    DEFAULT_MAX_BYTES_PER_LOG_LINE,
                    _v6netList,
                    false,
                    DEFAULT_COMPLETELY_RANDOM_ADDR_RATE,
                    DEFAULT_PROGRESS_REPORT,
                    ps,
                    _debugOut
            );
            ps.flush();
            long ctEnd = System.currentTimeMillis();
            System.out.println(
                    String.format("Done in %d millis, got %s log lines, log file size %s",
                    ctEnd - ctStart,
                    DF.format(totalLinesV4+totalLinesV6),
                    DF.format(_outputFile.toFile().length())
                    )
            );
            return totalLinesV4+totalLinesV6;
        } finally {
            ps.close();
        }

    }

    public static void main(String[] args) {
        String outputDirectory;
        int custCount;
        long totalTraffic;

        try {
            if (args.length != 3) {
                System.err.println("Usage: ");
                System.err.println("  java -cp <app.jar> me.megov.emc.t004.Maingenerator <outputDir> <topCustomersCount> <totalTrafficInBytes>");
                System.err.println("  The app will generate a random customer tree with ");
                for (String s:args) System.err.println("ARG="+s);
                System.exit(1);
                return;
            } 
            
            outputDirectory = args[0];
            custCount = Integer.parseInt(args[1],10);
            totalTraffic = Long.parseUnsignedLong(args[2], 10);

            Path custPath = Paths.get(outputDirectory, "customers.txt");
            ArrayList<IPvXTupleWithMask> v4netList = new ArrayList<>();
            ArrayList<IPvXTupleWithMask> v6netList = new ArrayList<>();
            generateCustomers(custCount, custPath, v4netList, v6netList, System.out);

            Path logPath = Paths.get(outputDirectory, "log.txt");
            generateLog(totalTraffic, logPath, v4netList, v6netList, System.out);

        } catch (Throwable th) {
            System.err.println(th.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
