FROM openjdk:21-jdk-slim

# Copy Files
WORKDIR /usr/src/app
COPY . .

RUN ls -la

# Ensure mvnw is executable
RUN chmod +x ./mvnw

# clean up the file
RUN sed -i 's/\r$//' mvnw
# run with the SH path
RUN /bin/sh mvnw dependency:resolve -Dmaven.test.skip=true package

# Docker Run Command
EXPOSE 8080
CMD ["java","-jar","/usr/src/app/target/playground-0.0.1-SNAPSHOT.jar"]