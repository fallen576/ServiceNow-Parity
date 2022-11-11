#!/bin/bash

#build app
mvn clean package -DskipTests

docker compose up
