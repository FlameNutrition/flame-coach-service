#!/bin/sh

cd /etc/app || exit 1
java -Dspring.datasource.password=$FLAME_COACH_DB_PASSWORD -Dserver.ssl.key-store=classpath:cert/flame-coach.p12 \
  -Dserver.ssl.key-store-password=$FLAME_COACH_SSL_PASSWORD -Dlog4j.configurationFile=log4j2.xml -Dflamecoach.rest.debug.enable=false -jar \
  /etc/app/flame-coach.jar > /dev/null 2> /dev/null < /dev/null &
