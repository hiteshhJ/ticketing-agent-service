FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar


COPY ./init.sh /init.sh
RUN chmod +x /init.sh

ENTRYPOINT [ "/init.sh" ]
