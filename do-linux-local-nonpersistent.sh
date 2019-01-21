#!/bin/bash

export DB_NAME=summer && export DB_USER=postgres && export DB_PASSWORD=test123 && export DB_URL=jdbc:postgresql://localhost:16099/summer && docker-compose up -d && mvn install -DskipTests -f ./pom.xml && mvn exec:java -DskipTests -Dexec.mainClass="eu.ha3.x.sff.deployable.SMainNoEventBusPostgresKt" -f ./deployable-vertx/pom.xml
