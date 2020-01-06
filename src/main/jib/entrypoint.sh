#!/bin/sh

echo "The application will start in ${ALTIMETRIK_SLEEP}s..." && sleep ${ALTIMETRIK_SLEEP}
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.altimetrik.greetingservice.GreetingServiceApplication"  "$@"
