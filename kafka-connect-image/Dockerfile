FROM confluentinc/cp-kafka-connect:4.1.1

ADD jars /jars

RUN cp /jars/exasol-jdbc-6.0.8.jar /usr/share/java/kafka-connect-jdbc/
RUN cp /jars/kafka-connect-jdbc-exasol-0.0.1-SNAPSHOT.jar /usr/share/java/kafka-connect-jdbc/
RUN cp /jars/kafka-connect-jdbc-5.0.0.jar /usr/share/java/kafka-connect-jdbc/

## Remove connector coming with docker image version 4.1.1
RUN rm /usr/share/java/kafka-connect-jdbc/kafka-connect-jdbc-4.1.1.jar
