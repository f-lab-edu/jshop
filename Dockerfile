FROM openjdk:17-oracle

RUN microdnf install findutils

WORKDIR /app

COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew build

CMD tail -f /dev/null