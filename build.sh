#!/bin/bash
mvn clean package -Dmaven.test.skip
cp -R target/dependency-jars ./
cp target/TheSoundOfMusic-1.0-SNAPSHOT.jar ./