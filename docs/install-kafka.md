# Install Kafka Connect JDBC v5.0.0-rc1

Exasol Kafka Connect dialect only works with Kafka Connect JDBC version
`5.0.0-rc1`.  However, this version is not released yet and Confluent does not
publish snapshot release publicly. Therefore, we have to build the jars
ourselves.

* Build Kafka

```bash
# Git clone Apache Kafka

git clone https://github.com/confluentinc/kafka.git

git pull origin --tags && git checkout tags/v5.0.0-rc1

# Run gradle if building first time

gradle

# Run install

./gradlew clean installAll
```

* Build Confluent Common Repository

```bash
# Git clone common repository

git clone https://github.com/confluentinc/common.git

git pull origin --tags && git checkout tags/v5.0.0-rc1

# Run install

mvn clean install -DskipTests
```

* Build Kafka Connect JDBC

```bash
# Git clone kafka-connect-jdbc

git clone https://github.com/confluentinc/kafka-connect-jdbc.git

git pull origin --tags && git checkout tags/v5.0.0-rc1

# Run install

mvn clean package -DskipTests

# This creates a jar file inside target/ folder, copy it into
# kafka-connect-image/jars folder.
```
