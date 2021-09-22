ARG VERSION=11.0.11

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew build

FROM openjdk:${VERSION}-jre

COPY --from=BUILD /src/build/libs/metronom-0.0.1-SNAPSHOT.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]
