FROM gradle:jdk21 AS build

WORKDIR /app

COPY . /app

RUN ./gradlew build
RUN ./gradlew javadoc

FROM eclipse-temurin:21-jre AS run

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/my-app.jar
COPY --from=build /app/build/docs/javadoc/ /app/documentation
COPY --from=build /app/build/reports/jacoco/test/html/ /app/coverage
COPY --from=build /app/build/reports/tests/test/ /app/tests

ENTRYPOINT ["java","-jar","/app/my-app.jar"]