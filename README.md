# t004 - test exercise

## Overview

This code was written to complete T004 exercise. It was developed with Java8 x64,
using Netbeans 8.1 as IDE and Gradle 2.14 as a build system.

### Lookup tables

The project has two implementations of network address range to customer lookup table.
The first is based on TreeRangeMap (TRM) from Google's Guava, so there is a single 
external dependency from Guava 23. The second one is based on my own implementation 
of augmented binary tree (AUT).

The TRM implementaion is faster, use mature code base, but I still don't make it
work right with IPv6. The AUT implementation is buggy, incomplete and untested, but
IPv6 is possible 'by design'.

### IPvX address processing

The IP address processing are designed to be unified between IPv4 and IPv6. All IPv4 
addresses are represented internally as standard overlayed IPv4 over IPv6 (::FFFF:FFFF:<IPv4>).

### Log processors

There are two log processor engines:

 - SEQ - simple sequential log scan. It is 'most common denominator' for performance
and the simplest method to analyze traffic log. But it's not the slowest one.

 - PAR - an attempt to parallelize log processing by dividing log file into some 
line-rounded segments and process them in parallel. All segment processing results 
are merged after finishing processing tasks into a complete report file and produce
total statistics record. This approach can utilize more CPU cores and also can utilize
modern solid state storges or storage arrays, than can support parallel reading.

### IO methods

The SEQ log processing does not need a complicated IO code, so the standard BufferedReader
approach is the best effort.

Parallel log processing, on the other side, needs more complicated IO processing.
In this project are implemented the next IO methods:

 - BUF - standard buffered reading from a file. With sequental scan it works ideally.
With parallel buffered reading of multiple segments of random access file, it is 
completely broken. Read ahead by BufferedReader moves the real file position inside a 
file segment more further, than the data, that actually returned back to a higher levels.

 - NIO - NIO/ByteBuffer low level approach. Just now have suboptimal char-by-char reading 
procedure, so singlethread performance is -20% worse, than SEQ:BUF. But have good 
parallelized results - up to 3 times faster than sequental on i7-920 with 4/8 parallel tasks

 - MMAP - same as pt.2 but using memory mapping. Maybe could be faster than NIO, but it is
uncompleted and untested yet.

## Starting up and running

### Overview

Application is solely console based, its behavior is controlled by optionally
supplied configuration file. Any parameter from configuration file can be overridden
from command line to make a flexible usage scenarios possible. There are two main 
parts compiled in single application: 
 - a traffic log processor, as stated in the excercise
 - a test data generator. 

As the log processor have to analyze a huge amounts of data in both aspects of traffic 
log size and customer network definitions, the test data generator is a 'must have' tool
for all software suite.

### Test generator

The test generator usage:

```
java -cp <test.jar> <mainClass> <dataDir> <topCustCount> <trafficInBytes>
```
 - test.jar - full or relative path to compiled application JAR
 - mainClass = me.megov.emc.t004.MainGenerator
 - dataDir - full or relative path to output directory for generated test data
 - topCustCount - number of top-level customers
 - trafficInBytes - total traffic in bytes for all test data

The more convenient way is to run test generator from the Gradle environment. 
There are three predefined generator profiles, that have their appropriate Gradle task names:

 - genSmall - 2 top-level customers, 10Mb of traffic
 - genMid - 10 top-level customers, 100Gb of traffic
 - genLarge - 100 top-level customers, 1Tb of traffic

Customer tree is generated in random/recursive manner, there is no way to exactly
limit customer count. Using top-level customer count parameter, we can indirectly
control the customer tree size. Total amount of traffic, specified as the generator's
parameter is a simple and convenient method to quickly check up validity of log analysis.

The traffic is distributed by a random size packets (<10000b), each of them are 
assigned to a some random address from customer tree. For simulating unknown customer
traffic, 1/1000 of packets are designated to completely random address, not belonging
to a customer tree addresses. This method can support generation of a quite large,
but random data volumes. The 'small' test case is really small (<100k og size), 
the 'Mid' test case produce >300Mb log size, the 'Large' - >3.5Gb log size.

*The default input and output directory, as specified in exercise is '/data' and it 
have to exist and to be writable by user prior running any exercise code.*

### Log processor

Log processor is the main part of application. Its usage:

```
java -cp <test.jar> <mainClass> [--cfgFile=<configFile>] [--<paramName>=<paramValue>]
```

 - test.jar - full or relative path to compiled application JAR
 - mainClass = me.megov.emc.t004.Main
 - configFile - full or relative path to config file
 - paramName - name of a parameter, overriding configuration file
 - paramValue - value of a parameter, overriding configuration file

Parameters with their default values:

 - --dataDir=/data              - Directory for data files (customers, log)
 - --customerFile=customers.txt - Customers definition filename
 - --logFile=log.txt            - Traffic log filename
 - --outputDir=/data            - Output directory for report
 - --outputFile=report.txt      - Report filename
 - --isDebug=1                  - Enable (1) or disable (0) debug output
 - --reportInterval=1000000     - Progress reporting interval (in processed records)
 - --taskCount=2                - Task count for parallel processing
 - --logProcessor=SEQ           - Log processor (SEQ-sequental, PAR-parallel)
 - --logProcessorTask=BUF       - Log processor task class:
  + BUF 		- buffered sequental/segmented log processing
  + NIO			- NIO ByteBuffer segmented log processing
  + MMAP 		- MemoryMapped segmented log processing (UNIMPLEMENTED)
 - --logProcessorLookup=TRM     - Log processor lookup table
  + TRM 		- Guava's TreeRangeMap
  + AUT			- Own's implementation of augmented binary tree

There log processing options are also have their appropriate Gradle task names:
 - runSeq - one thread, buffered sequential processing, TRM lookup
 - runSeqAut - one thread, buffered sequential processing, AUT lookup
 - runPar1 - one task, parallel processing with NIO, TRM lookup
 - runPar[2,4,8,12] - two/four/eight/twelve parallel tasks, NIO, TRM
 - runPar1Aut - one task, parallel processing with NIO, AUT lookup
 - runPar[2,4,8,12]Aut - two/four/eight/twelve parallel task, NIO, AUT

## Configuration file

A confiuration file can be optionall supplied to define default values.
The parameter names are the same as above, but without double-dashes.
The '=' character delimits parameter name and parameter value. Any parameter, 
provided in command line takes over that supplied in configuration file.

# IMPORTANT, BUT UNIMPLEMENTED YET:
 - IPv6. The stopping pit is that 128bit values in Long+Long IPvXTuple class 
can't produce valid Guava's Range, and can't participate TreeRangeMap. Maybe it
will be solved by AUT implementation.
 - there are no implementation of IPv6 generators and 
 - there is no parsing v4-into-v6 string representation with both colons and dots.
 - no Docker containerization. Due to lack of time.

