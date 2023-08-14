FROM registry.access.redhat.com/ubi8/openjdk-17:1.11
USER 0
RUN useradd -r -g root -m -d /home/kahala -s /bin/bash kahala 

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

COPY --chown=kahala:root target/*.jar /deployments/kahala.jar

EXPOSE 8080
USER kahala

ENV JAVA_APP_JAR="/deployments/kahala.jar"
