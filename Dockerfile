FROM amazoncorretto:11-alpine-jdk
ADD service/build/libs/*SNAPSHOT.jar resource-processor.jar
ENTRYPOINT ["java","-jar","resource-processor.jar"]