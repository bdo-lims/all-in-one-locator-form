FROM maven:3-jdk-11 as maven-build
COPY ./pom.xml /locator-form-webapp/
COPY ./src /locator-form-webapp/src/
WORKDIR /locator-form-webapp/
RUN mvn clean install -DskipTests

#switch to this once their is an official jdk 11 alpine image
#FROM openjdk:11-jre-alpine
FROM alpine
RUN apk --no-cache add openjdk11
RUN apk --no-cache add bash
RUN apk --no-cache add curl

# RUN addgroup --gid 8443 -S spring && adduser --uid 8443 -S spring-user -G spring
# USER spring-user:spring

COPY --from=maven-build /locator-form-webapp/target/*.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
