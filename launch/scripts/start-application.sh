#!/bin/sh

cd /etc/app || exit 1
java -Dspring.datasource.password=$FLAME_COACH_DB_PASSWORD -Dlog4j.configurationFile=log4j2.xml -jar /etc/app/flame-coach.jar > /dev/null &
