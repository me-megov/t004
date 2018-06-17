# t004 - test exercise

## Overview

This code was written to complete T004 exercise. It was developed with Java8 x64,
using Netbeans 8.1 as IDE and Gradle 2.14 as a build system.

### Lookup tables

The project has two implementations of network address range to customer lookup table.
The first is based on TreeRangeMap (TRM) from Google's Guava, so there is a single 
external dependency from Guava 23. The second one is based on my own partial 
implementation of augmented binary tree (AUT).

The TRM implementaion is faster, use mature code base, but initially have some 
problems with Range objects, holding 128 bit IPv6 address as a Long+Long pair 
incapsulated inside a class. The AUT implementation is maybe buggy, somewhat 
incomplete and untested, but IPv6 in a Long+Long representation is working.

### IPvX address processing

The IP address processing are designed to be unified between IPv4 and IPv6. All IPv4 
addresses are represented internally as standard overlayed IPv4 over IPv6 (::FFFF:FFFF:<IPv4>).

### Log processors

There are two log processor engines:

 - SEQ - simple sequential log scan. It is 'most common denominator' for performance
and the simplest method to analyze traffic log. But, surprisingly, it's not the 
slowest one.

 - PAR - an attempt to parallelize log processing by dividing log file into some 
line-rounded segments and process them in parallel. All segment processing results 
are merged together after tasks are finished. Merged traffic records list stored 
into a report file along with some statistics. The statistics can also be saved
into file for analisys. The parallel approach helps to utilize more than one CPU 
core and also produce parallel IO streams, than can successfully processed by
modern solid state storages or storage arrays, as they naturally support parallel 
reading.

### IO methods

The SEQ log processing does not need a complicated IO code, so using the standard 
BufferedReader is the best effort.

Parallel log processing, on the other side, needs more complicated IO processing.
In this project are implemented the next IO methods:

 - BUF - standard buffered reading from a file. With sequental scan it works very well.
On the other side, parallel buffered read of multiple segments in a random access
file is completely broken. Read ahead capability of BufferedReader moves the real 
file position inside a file segment far forward, than the data, that actually 
returned to a higher levels. So, the processing loose some log records between segments.
Thant why, there are no tests of PAR processing with BUF IO method.

 - NIO - NIO/ByteBuffer low level approach. Just now it has suboptimal char-by-char 
reading procedure, so singlethread performance is -20% worse, than SEQ:BUF. 
But NIO methos have good parallelized results - up to 3 times faster than sequental 
on i7-920 with 4/8 parallel tasks.

 - MMAP - same as pt.2 but using memory mapping. Maybe could be faster than NIO, 
but it is stub and unimplemented yet..

## Starting up and running

### Overview

Application is solely console based, its behavior is controlled by optionally
supplied configuration file and a commanline parametera. Any parameter from 
command line will override the value, specifies in configuration file.
It makes a flexible usage scenarios possible. There are two main parts compiled 
both into a single application: 
 - a traffic log processor, as stated in the excercise
 - a test data generator. 

The log processor developed to analyze a huge amounts of data in both aspects of traffic 
log size and customer network definitions, so the test data generator is a 'must have' 
tool for debugging and performance measures.

### Test generator

The test generator usage:

```
java -cp <test.jar> <mainClass> <dataDir> <topCustCount> <childFactor> <trafficInBytes>
```
 - test.jar - full or relative path to compiled application JAR
 - mainClass = me.megov.emc.t004.MainGenerator
 - dataDir - full or relative path to output directory for generated test data
 - topCustCount - number of top-level customers
 - childFactor - int [2-7] value, how many childs will be generated for a customer
 - trafficInBytes - total traffic in bytes for all test data

The more convenient way is to run test generator from the Gradle environment. 
There are three predefined generator profiles, that have their appropriate Gradle task names:

 - genSmall - 3+3 top-level customers (v4+v6), 10Mb of traffic, childFactor=3
 - genMid - 10+10 top-level customers (v4+v6), 100Gb of traffic, childFactor=4
 - genLarge - 75+75 top-level customers (v4+v6), 1Tb of traffic, childFactor=4

Customer tree is generated in random/recursive manner, so there is no way to exactly
limit customer count. Using top-level customer count parameter, we can indirectly
control the customer tree size. ChildFactor is the maximum number of subCustomers,
that can be generated to a single customer. The smaller values produce more 'thin'
tree, the larger values more 'fatter' customer tree.

The customer names are generated with a counter, V4/V6 prefix and a postfix, that
denotes the tree level at the generation time. It helps to check a tree consistency
at an analysis stage. 

Total amount of traffic, specified as the generator's parameter is a total bytes
that are used to generate a traffic log. The traffic is distributed by a random 
size packets (<10000b), each packet are assigned to some random, but still belonging 
to a customer tree address. For simulating 'unknown customer' traffic, 1/1000 
of packets are designated to completely random address. This method can support 
generation of a quite large, but random data volumes. 

The 'Small' test case is really small (<100k of size, <1000 customers), the 'Mid'
test case produce >600Mb log size and 5-6k customers, the 'Large' - >5Gb log size
and 40-50k customers.

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
 - --isDebug=0                  - Enable (1) or disable (0) debug output
 - --statsFile=stats.txt	- Statistics report filename
 - --isSaveStats=0              - Enable (1) or disable (0) save statistics report
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

All Gradle tasks are configured with debug and save statistics report enabled.
With enabled debug, the real output filenames (report and stats) are postfixed 
with log processor type, task count, task class and lookup table type.
It helps to gather results and stats from different run with different profiles.


## Configuration file

A confiuration file can be optionally supplied in command line to define 
default values. The parameter names are the same as specified above, but 
in configuration file they used without double-dashes. The '=' character 
delimits parameter name and parameter value. Any parameter, provided in 
command line takes over that supplied in configuration file.

## Performance

### Testbed hardware

The workstation with i7-920, 12G RAM, 80Gb SSD Intel DC S3510.
OS: Debian Stretch 9.4, x86_64.
Java: Oracle JDK 1.8 upd 101

### Common considerations

After analysing performance in different configurations, there are some 
considerations:
 - BUF IO method is faster than NIO in singlethreaded environment, my own
current 'readLine from ByteBuffer' implementation is dumb and slow.
 - AUT is slower than TRM. Yes, Google's code is better! ;)
 - PAR IO method effectively increase the performance. When the tasks count
matches the CPU core count, the performance is the best. Further increasing
tasks count do not give any effect. Of course, the disk subsystem have to
do well parallel IO processing. I use SSD so in was not an issue.

### Detailed test results

The baseline is formed by SEQ:BUF implementation. The large test case give 
processing speen of 380k log records per second. Due to unoptimized inner 
loop in NIO, the singlethreaded performance with TRM was 286k log records
per second. My own AUT implementation is slower - 247k log records per 
second.
Parallel log processing largely improves the results of NIO backend. Using 
two tasks it reaches 567k in TRM and 471k in AUT. Further increasing tasks 
count are also increases the perfomance: 4 tasks gives 973/727k log records 
(TRM/AUT), 8 tasks gives 1200/945k log records (TRM/AUT). 
The test stats reports are included in 'files/*/stats.txt.*'

## Docker containerization

To containerize the example I use gradle-docker-plugin v3.3.4 from bmushko.
The appropriate Gradle tasks areж
 - dckCreateDockerfile - creates Dockerfile
 - dckBuildImage - builds a complete image. This task is not dependant of 
Gradle's build task, so you have to do "gradle build" before running any
docker tasks.
 - dckCreateContainer - creates the container
 - dckLog - run the whole installation in docker with loogin out stdout.

## IMPORTANT, BUT UNIMPLEMENTED YET:
 - there is no parsing v4-into-v6 string representation with both colons
and dots, like '::FFFF:10.10.10.10'

