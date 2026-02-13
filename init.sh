#!/bin/bash

set -e

# Start application with optimized JVM settings
exec java -jar app.jar \
  -XX:MaxRAMPercentage=80 \
  -XX:InitialRAMPercentage=75 \
  -XX:+ExitOnOutOfMemoryError \
  -XX:OnOutOfMemoryError='echo TICKETING_AGENT_SERVICE_OUT_OF_MEMORY_FAILURE_EXCEPTION;'

