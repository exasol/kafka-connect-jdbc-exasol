# kafka-connect-jdbc-exasol

[![Build Status](https://github.com/exasol/kafka-connect-jdbc-exasol/actions/workflows/ci-build.yml/badge.svg)](https://github.com/exasol/kafka-connect-jdbc-exasol/actions/workflows/ci-build.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Akafka-connect-jdbc-exasol&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Akafka-connect-jdbc-exasol)

## Deprecation Warning

⚠ This project is discontinued in favor of [exasol/kafka-connector-extension](https://github.com/exasol/kafka-connector-extension). This repository is archived.

## Overview

[Exasol][exasol] database dialect example setup for [Kafka Confluent JDBC
Connector][kafka-jdbc].

**Please bear in mind that this only works with Kafka Connect JDBC version
5.0+**

* [Production Setup](#production-setup)
* [Testing Locally](#testing-locally)
* [Dependencies and Services](#dependencies-and-services)
* [Gotchas](#gotchas)

## Information for Users

* [Changelog](doc/changes/changelog.md)

## Information for Developers

* [Dependencies](dependencies.md)

## Production setup

If you already have an running Confluent Kafka Connect cluster, you need setup
Exasol source or sink configuration (or both). You can find example
configurations for [exasol-source](./exasol-source.json) and
[exasol-sink](./exasol-sink.json). Please upload these to Kafka Connect
connectors, for example,

```bash
curl -X POST \
     -H "Content-Type: application/json" \
     --data @exasol-source.json kafka.connect.host:8083/connectors
```

Additionally, you need to upload the Exasol JDBC
[jars](./kafka-connect-exasol/jars) to the connect plugin path. The plugin paths
are possibly `/usr/share/java` or `/etc/kafka-connect/jars`. However, please
check that these paths are on Kafka classpath.

You can find more information on Confluent documentation pages. Some relevant
documentations are listed below.

* [Kafka Connectors](https://docs.confluent.io/current/connect/managing/index.html)
* [Kafka Connect JDBC Connector](https://docs.confluent.io/5.0.0/connect/kafka-connect-jdbc/index.html)
* [Configuring Connectors](https://docs.confluent.io/5.0.0/connect/managing/configuring.html)
* [Manually Installing Community Connectors](https://docs.confluent.io/5.0.0/connect/managing/community.html)

## Testing locally

For testing we are going to use [docker][docker] and
[docker-compose][docker-compose]. Please set them up accordingly on your local
machine. For running [Exasol docker-db][dh-exadb] you need root privileges.

**Additionally, if you are using non Linux machine, please obtain the ip address
for docker or docker-machine**. For example, in MacOS, with the following
command:

```bash
docker-machine ip
```

For the rest of documentation, when we refer to `localhost`, substitute it with
ip address resulted from above command.

We need to open several terminals for dockerized testing.

* First clone this repository and start all the services:

```bash
git clone https://github.com/EXASOL/kafka-connect-jdbc-exasol.git

cd kafka-connect-jdbc-exasol

# If you're running docker in a virtual environment you might
# need to run the following command before docker-compose up:

# export COMPOSE_TLS_VERSION=TLSv1_2

docker-compose up

```

* Now we should create an exasol sample schema and table. This example creates a
  `country` table inside `country_schema` and inserts couple of records into it.
  **This step should happen before Kafka connector configurations setup because
  Kafka immediately starts to look for the Exasol tables.**

```bash
docker exec -it exasol-db exaplus -c localhost:8563 -u sys -P exasol -f /test/country.sql
```

### Testing Connect Source (Exasol -> Kafka)

* Once the schema and table is created, open another terminal and upload
  connecter configuration file to kafka connect:

```bash
# Create and add a new Kafka Connect Source
curl -X POST \
     -H "Content-Type: application/json" \
     --data @exasol-source.json localhost:8083/connectors

# You can see all available connectors with the command:
curl localhost:8083/connectors/

# Similarly, you can see the status of a connector with the command:
curl localhost:8083/connectors/exasol-source/status
```

* Lastly, on terminal four, let us consume some data from Kafka. For the purpose
  of this example we are going to use kafka console consumer:

```bash
docker exec -it kafka02 /bin/bash

# List available Kafka topics, we should see the 'EXASOL_COUNTRY' listed.
kafka-topics --list --zookeeper zookeeper.internal:2181

# Start kafka console consumer
kafka-console-consumer \
    --bootstrap-server kafka01.internal:9092 \
    --from-beginning \
    --topic EXASOL_COUNTRY
```

* You should see two records inserted from other terminal. Similarly, if you
  insert new records into `country` table in Exasol, they should be listed on
  kafka consumer console.

* In order to see the console results in a structured way, you can consume them
  using Avro console consumer from `schema-registry` container:

```bash
docker exec -it schema-registry /bin/bash

kafka-avro-console-consumer \
    --bootstrap-server kafka01.internal:9092 \
    --from-beginning \
    --topic EXASOL_COUNTRY
```

### Testing Connect Sink (Kafka -> Exasol)

* Now we should create an Exasol sample schema and table. This example creates a
  `country_population` table inside `country_schema` and will be the destination for the kafka topic records.
  **This step should happen before Kafka connector configurations setup otherwise it will not find the sink table in Exasol**

```bash
docker exec -it exasol-db exaplus -c localhost:8563 -u sys -P exasol -f /test/country_population.sql
```

* In first terminal, upload Kafka Connect Exasol Sink configurations:

```bash
curl -X POST \
     -H "Content-Type: application/json" \
     --data @exasol-sink.json \
     localhost:8083/connectors

## Check that sink connector is running
curl localhost:8083/connectors/exasol-sink/status
```

* In second terminal open the kafka avro producer and produce some records for
  an example topic `country_population`:

```bash
docker exec -it schema-registry /bin/bash

kafka-avro-console-producer \
    --broker-list kafka01.internal:9092 \
    --topic COUNTRY_POPULATION \
    --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"COUNTRY_NAME","type":"string"},{"name":"POPULATION", "type": "long"}]}'
```

* Type some records in JSON format:

{"COUNTRY_NAME": "France", "POPULATION": 67}
{"COUNTRY_NAME": "Croatia", "POPULATION": 4}```bash

```

* In another terminal, ensure that the records are available in Exasol table:

```bash
docker exec -it exasol-db bash -c 'exaplus -c localhost:8563 -u sys -P exasol -sql "SELECT * FROM country_schema.country_population;"'
```

## Dependencies and Services

For this example setup we depend on several jar files:

* [Exasol JDBC Driver][exa-jdbc-driver]
* Exasol Kafka Connect JDBC Jar
  * You can create Kafka Connect Exasol JDBC jar via `mvn clean package` that
    will create jar file in `target/`. Then copy it into
    `kafka-connect-image/jars/`.
* [Kafka Connect JDBC Connector][kafka-jdbc] (5.0+ version)
  * You can find installation guide here at [doc/install-kafka](doc/install-kafka.md).

Additionally, we are using docker-compose based Exasol and Kafka Connect
services. The Kafka Connect is configured for [distributed
mode][kafka-dist-mode].

| Service Name      | Versions                                             | Description                                                            |
| :---------------- | :--------------------------------------------------- | :--------------------------------------------------------------------- |
| `exasol-db`       | [dockerhub/exasol/docker-db][dh-exadb]               | An Exasol docker db. Please note that we use stand-alone cluster mode. |
| `zookeeper`       | [dockerhub/confluentinc/cp-zookeeper][dh-cpzk]       | A single node zookeeper instance.                                      |
| `kafka`           | [dockerhub/confluentinc/cp-kafka][dh-cpkf]           | A kafka instance. We have three kafka node setup.                      |
| `schema-registry` | [dockerhub/confluentinc/cp-schema-registry][dh-cpsr] | A schema-registry instance.                                            |
| `kafka-connect`   | [dockerhub/confluentinc/cp-kafka-connect][dh-cpkc]   | Custom configured kafka-connect instance.                              |

## Gotchas

There are several tips and tricks to consider when setting up the Kafka Exasol
connector.

* The timestamp and incrementing column names should be in upper case, for
  example, `"timestamp.column.name": "UPDATED_AT"`. This is due to fact that
  Exasol makes all fields upper case and Kafka connector is case sensitive.

* The `tasks.max` should be more than the number of tables in production
  systems. That is in jdbc connectors each table is sourced per partition then
  handled by single task.

* The `incrementing` or `timestamp` column names in Kafka Connect configuration,
  should have a `NOT NULL` constraint when creating a table definition.

## Troubleshooting

### Batch upserts

The batch mode together with upsert is not supported at the moment. We transform
the Kafka upserts into Exasol specific `MERGE` statements that does not support
batches.

You can read more about it at
[issue #5](https://github.com/exasol/kafka-connect-jdbc-exasol/issues/5).

[exasol]: https://www.exasol.com/en/
[kafka-jdbc]: https://github.com/confluentinc/kafka-connect-jdbc
[kafka-connect]: http://kafka.apache.org/documentation.html#connect
[kafka-dist-mode]: https://docs.confluent.io/current/connect/userguide.html#distributed-mode
[docker]: https://www.docker.com/
[docker-compose]: https://docs.docker.com/compose/
[dh-exadb]: https://hub.docker.com/r/exasol/docker-db/
[dh-cpzk]: https://hub.docker.com/r/confluentinc/cp-zookeeper/
[dh-cpkf]: https://hub.docker.com/r/confluentinc/cp-kafka/
[dh-cpsr]: https://hub.docker.com/r/confluentinc/cp-schema-registry/
[dh-cpkc]: https://hub.docker.com/r/confluentinc/cp-kafka-connect/
[exa-jdbc-driver]: https://maven.exasol.com/artifactory/webapp/#/artifacts/browse/tree/General/exasol-releases/com/exasol/exasol-jdbc/6.0.8/exasol-jdbc-6.0.8.jar
