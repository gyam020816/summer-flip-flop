
REM warning: if you write "set DB_USER=postgres && set DB_PASSW..."
REM   then DB_USER will be equal to "postgres " with a trailing space (9 chars)
REM   :(   https://youtu.be/3YxaaGgTQYM?t=11

set DB_NAME=summer&& set DB_USER=postgres&& set DB_PASSWORD=test123&& set DB_URL=jdbc:postgresql://localhost:16099/summer&& docker-compose up -d && mvn install -DskipTests -f ./pom.xml && mvn exec:java -DskipTests -Dexec.mainClass="eu.ha3.x.sff.deployable.SMainNoEventBusPostgresKt" -f ./deployable-vertx/pom.xml
