FROM openjdk:13-alpine

EXPOSE 8080

RUN mkdir -p /app

COPY target/scala-2.12/*.jar /app/

WORKDIR /app

CMD java -jar ./DtLab.jar
# override CMD from your orchestrator with appropriate jvm args, -Xms1024m -Xmx15360m etc...

