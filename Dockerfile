FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/BusTracker-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/BusTracker.war

EXPOSE 8080
