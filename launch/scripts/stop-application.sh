#!/bin/sh

mkdir /var/logs/flame-coach/scripts 2> /dev/null
touch /var/logs/flame-coach/scripts/waitAndRun.log 2> /dev/null
processId=$(/bin/ps -fu root | grep "flame-coach.jar" | grep -v "grep" | awk '{print $2}')

if [ "$processId" ]
then

  kill "$processId"

  while [ -e "/proc/$processId" ]
  do
      echo "Process: $processId is still running" >> /var/logs/flame-coach/scripts/waitAndRun.log
      sleep .6
  done

  echo "Process $processId has finished" >> /var/logs/flame-coach/scripts/waitAndRun.log

fi

exit 0
