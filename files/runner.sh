#/bin/sh

JAR=$1

CPU_CORES=`grep -c ^processor /proc/cpuinfo`
#echo "Running test with ${CPU_CORES} tasks"

#echo "DATA-------"
#ls -l /data

#echo "APP-------"
#ls -lR /app

#echo "RUN-------"
java ${JAVA_OPTS} -jar ${JAR} --isSaveStats=0 --isDebug=0 --logProcessor=PAR --logProcessorTask=NIO --logProcessorLookup=TRM --taskCount=${CPU_CORES}

RESULT=$?
echo "Exit code: ${RESULT}"

return ${RESULT}

