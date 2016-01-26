#!/bin/bash
classpath=\
~/.m2/repository/org/jooq/jooq/3.6.1/jooq-3.6.1.jar:\
~/.m2/repository/org/jooq/jooq-meta/3.6.1/jooq-meta-3.6.1.jar:\
~/.m2/repository/org/jooq/jooq-codegen/3.6.1/jooq-codegen-3.6.1.jar:\
~/.m2/repository/mysql/mysql-connector-java/5.1.34/mysql-connector-java-5.1.34.jar

java -classpath $classpath org.jooq.util.GenerationTool ./jooq-config.xml
