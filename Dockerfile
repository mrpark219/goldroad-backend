FROM azul/zulu-openjdk-alpine:17-latest

WORKDIR /home/gradle/project

COPY . .

RUN ./gradlew clean build

CMD ["java", "-jar", "/home/gradle/project/build/libs/goldroad-0.0.1-SNAPSHOT.jar"]