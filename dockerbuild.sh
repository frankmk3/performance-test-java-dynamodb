#!/usr/bin/env bash

#generate backend jar
sh gradlew build

#copy jar file
cp build/libs/performance-test-java-dynamodb.jar ./dockercompose/performance-test-java-dynamodb/app.jar

#start the containers
docker-compose up -d