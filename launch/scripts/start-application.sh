#!/bin/sh

cd /etc/app || exit 1
java -Dspring.datasource.password=$FLAME_COACH_DB_PASSWORD -Dlog4j.configurationFile=log4j2.xml -Dflamecoach.rest.debug.enable=false -jar \
	/etc/app/flame-coach.jar > /dev/null 2> /dev/null < /dev/null &
