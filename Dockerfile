FROM adoptopenjdk/openjdk11:ubi
EXPOSE 8070
RUN mkdir /opt/app
COPY target/*.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]
