# Install Kafka Connect JDBC v5.0.0

Exasol Kafka Connect dialect only works with Kafka Connect JDBC version
`5.0.0+`. However, these jars are not yet in [Confluent maven
repository](http://packages.confluent.io/maven/). Therefore, we have to build
them ourselves from source.

## Build Kafka

```bash
# Git clone Apache Kafka

git clone https://github.com/confluentinc/kafka.git

git pull origin --tags && git checkout tags/v5.0.0

# Run gradle if building first time

gradle

# Run install

./gradlew clean installAll
```

## Build Confluent Common Repository

```bash
# Git clone common repository

git clone https://github.com/confluentinc/common.git

git pull origin --tags && git checkout tags/v5.0.0

# Run install

mvn clean install -DskipTests
```

## Build Kafka Connect JDBC

```bash
# Git clone kafka-connect-jdbc

git clone https://github.com/confluentinc/kafka-connect-jdbc.git

git pull origin --tags && git checkout tags/v5.0.0

# Run install

mvn clean package -DskipTests
```

This creates jar files inside `target/` folder,

- `target/kafka-connect-jdbc-5.0.0.jar`
- `target/kafka-connect-jdbc-5.0.0-tests.jar`

copy them into `lib/` folder and build the Kafka Connect JDBC Exasol jar.
