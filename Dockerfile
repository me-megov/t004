FROM openjdk:8-jdk-alpine
ADD build/libs /app
ADD files/runner.sh /app/runner.sh
RUN mkdir /data
VOLUME ["/data"]
ENV JAVA_OPTS ""
ENTRYPOINT ["sh", "-c", "/app/runner.sh /app/midrange-test-004.jar"]
