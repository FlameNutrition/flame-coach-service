#!/bin/sh

cd /etc/app || exit 1
java -Dspring.datasource.password=$FLAME_COACH_DB_PASSWORD \
  -Dserver.ssl.key-store=classpath:cert/flame-coach.p12 \
  -Dserver.ssl.key-store-password=$FLAME_COACH_SSL_PASSWORD \
  -Dlog4j.configurationFile=log4j2.xml \
  -Dflamecoach.rest.debug.enable=false \
  -Dspring.mail.username=$FLAME_COACH_SMTP_USERNAME \
  -Dspring.mail.password=$FLAME_COACH_SMTP_PASSWORD \
  -Dflamecoach.rest.debug.enable=false \
  -Dflamecoach.rest.security.password=$FLAME_COACH_SECURITY_AUTH \
  -jar /etc/app/flame-coach.jar > /dev/null 2> /dev/null < /dev/null &
