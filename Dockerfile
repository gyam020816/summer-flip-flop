FROM openjdk:11-jre-slim

RUN mkdir /work
WORKDIR /work
ADD ./deployable-vertx/target/summer-flip-flop.jar /work/

CMD ["java", "-jar", "/work/summer-flip-flop.jar", "-Xmx256m", "-Xms32m"]
