# Kafka Connect JDBC Exasol 1.0.0, released 2021-11-24

## Code name: Moved to Github Actions and updated dependencies

## Summary

This release adds `project-keeper` maven plugin, migrates Continuous Integration (CI) from Travis CI to Github Actions and updates dependencies.

## Features

* #11: Added project-keeper maven plugin

## Refactoring

* #20: Removed CVE-2020-13692 vulnerability
* #25: Migrated to Github Actions from Travis CI

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:exasol-jdbc:7.1.2`

### Test Dependency Updates

* Added `io.confluent:kafka-connect-jdbc:10.2.5`
* Added `junit:junit:4.13.2`
* Added `org.jacoco:org.jacoco.agent:0.8.7`
* Added `org.mockito:mockito-all:1.10.19`

### Plugin Dependency Updates

* Added `com.exasol:artifact-reference-checker-maven-plugin:0.4.0`
* Added `com.exasol:error-code-crawler-maven-plugin:0.7.1`
* Added `com.exasol:project-keeper-maven-plugin:1.3.2`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.14`
* Added `org.apache.maven.plugins:maven-assembly-plugin:3.3.0`
* Added `org.apache.maven.plugins:maven-clean-plugin:3.1.0`
* Added `org.apache.maven.plugins:maven-compiler-plugin:3.8.1`
* Added `org.apache.maven.plugins:maven-dependency-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-deploy-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3`
* Added `org.apache.maven.plugins:maven-install-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-jar-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-resources-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-site-plugin:3.9.1`
* Added `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3`
* Added `org.codehaus.mojo:versions-maven-plugin:2.8.1`
* Added `org.jacoco:jacoco-maven-plugin:0.8.7`
* Added `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0`
