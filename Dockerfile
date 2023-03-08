FROM jfrogimg.private.registry/baseos_redhat/ubi8/openjdk-11-runtime:latest
WORKDIR /apps
ADD target/jasper-0.0.1.jar report.jar
ENTRYPOINT ["java", "-jar", "report.jar"]