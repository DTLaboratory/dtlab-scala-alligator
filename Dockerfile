FROM openjdk:17-alpine

EXPOSE 8080

RUN mkdir -p /app

COPY target/scala-2.13/*.jar /app/

WORKDIR /app

# override CMD from your orchestrator with appropriate jvm args, -Xms1024m -Xmx15360m etc...
CMD java -Dlog4j2.formatMsgNoLookups=true -Xms256m -Xmx512m -jar ./DtLab.jar

