# t004 - test exercise

This code was written to complete T004 exercise. It was developed with Java8 x64,
using Netbeans 8.1 as IDE and Gradle 2.14 as a build system.

The project has one dependency of Guava 23. The default input and output directory,
as specified in exercise is '/data' and it have to exist and to be writable by user
prior running any exercise code.

Application is solely console based, its behavior is controlled by optionally
supplied configuration file. Any parameter from configuration file can be overridden
from command line to make a flexible usage scenarios possible. There are two main 
parts in application: a traffic log analyzer and a test data generator. The log analyzer
must process huge amounts of data in both aspects: traffic log size and customer network
definitions. So, test data generator is a 'must have' tool.

The generator usage:
 java -cp <test.jar> <mainClass> <dataDir> <topCustCount> <trafficInBytes>
    - test.jar - full or relative path to compiled application JAR
    - mainClass = me.megov.emc.t004.MainGenerator
    - dataDir - full or relative path to output directory for generated test data
    - topCustCount - number of top-level customers
    - trafficInBytes - total traffic in bytes for all test data

There are three generator profiles, that have their appropriate Gradle task names:
 - genSmall - 2 top-level customers, 10Mb of traffic
 - genMid - 10 top-level customers, 100Gb of traffic
 - genLarge - 100 top-level customers, 1Tb of traffic

Customer tree is generated in random/recursive manner, there is no way to exactly
limit customer count. Using top-level customer count parameter we can indirectly
control the customer tree size. Total amount of traffic, specified as the generator
parameter is a simple and convenient method to quickly check up validity of log analysis.
Total amount of traffic is distributed to a random size packets (<10000b), each
of them are assigned to an address from customer tree. For simulating unknown customer
traffic, 1/1000 of packets are designated to completely random address.
This approach helps to generate a massive but random data volumes, that helps to test 
the main log analyzer engine. The 'small' test case is really small (<100k), the 'Mid'
test case produce >300Mb log, the 'Large' - >3.5Gb log.

Log processor is the main part of application. Its usage:
 java -cp <test.jar> <mainClass> [--cfgFile=<configFile>] [--<paramName>=<paramValue>]
    - test.jar - full or relative path to compiled application JAR
    - mainClass = me.megov.emc.t004.Main
    - configFile - full or relative path to config file
    - paramName - name of a parameter, overriding configuration file
    - paramValue - value of a parameter, overriding configuration file

Parameters with default values:
    --dataDir=/data              - Directory for data files (customers, log)
    --customerFile=customers.txt - Customers definition filename
    --logFile=log.txt            - Traffic log filename
    --outputDir=/data            - Output directory for report
    --outputFile=report.txt      - Report filename
    --isDebug=1                  - Enable (1) or disable (0) debug output
    --reportInterval=1000000     - Progress reporting interval (in processed records)
    --taskCount=2                - Task count for parallel processing
    --logProcessor=PAR           - Log processor (SEQ-sequental, PAR-parallel)
    --logProcessorTask=FCH       - Parallel Log processor task class:
            	    BUF 		- buffered segmented log analyzer
		    FCH			- NIO ByteBuffer segmented log analyzer
		    MMAP 		- MemoryMapped segmented log analyzer

Configuration file contains named parameter/value pairs, parameters names are specified without 
double-dashes. In command line any parameter are prefixed with '--' and character '='
delimits parameter name and parameter value. Any parameter, provided in command line is
overwrite the same parameter from configuration file.

There are two log analyzer engines:

1. SEQ - simple sequential log scan with BufferedReader. It is 'most common denominator'
and the simplest method to analyze traffic log. Yet not the slowest one.

2. PAR - an attempt to parallelize log processing by divide file into line-rounded
segments and process them in parallel with different approaches. Each segment 
processing results are merged after task completion into total report file and produce
total statistics record.


Parallel log processing can be done with next methods:

1. BUF - attempt to use buffered reading over random access file. It is broken,
because of read ahead by BufferedReader. The real file read position into a segment
are happens some time earlier, than real data was fetched to the processing.

2. FCH/NIO - NIO/ByteBuffer low level approach. Suboptimal in char-by-char reading buffer,
but have good parallelized results. Works.

3. MMAP - same as pt.2 but using memory mapping. Could be faster than NIO, but uncompleted
and untested yet.

There log processing also have their appropriate Gradle task names:
 - runSeq - one thread, buffered sequential processing
 - runPar1 - one task, parallel processing with NIO
 - runPar[2,4,8,12] - two/four/eight/twelve parallel task processing with NIO


IMPORTANT, BUT UNIMPLEMENTED YET:
 - IPv6. The stopping pit is that 128bit values in Long+Long class can't produce
Guava's Range, and can't participate TreeRangeMap. Also there are no implementation
of IPv6 generators and no parsing v4-into-v6 string representation with both colons
and dots.
 - no Docker containerization. Due to lack of time.

