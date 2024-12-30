FROM openjdk:21
LABEL authors="trongnb02"
EXPOSE 8080
ADD target/schoolmanagement.jar schoolmanagement.jar

ENTRYPOINT ["java", "-jar", "/schoolmanagement.jar"]