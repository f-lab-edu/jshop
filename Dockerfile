FROM openjdk:17-oracle

ARG VERSION

ARG MYSQL_HOST
ARG MYSQL_PORT
ARG MYSQL_PASSWORD
ARG MYSQL_USERNAME

ARG JWT_SECRET_KEY

ARG REDIS_HOST
ARG REDIS_PORT
ARG REDIS_PASSWORD
ARG LOGSTASH_HOST
ARG LOGSTASH_PORT

ENV VERSION=${VERSION}

ENV MYSQL_HOST=${MYSQL_HOST}
ENV MYSQL_PORT=${MYSQL_PORT}
ENV MYSQL_PASSWORD=${MYSQL_PASSWORD}
ENV MYSQL_USERNAME=${MYSQL_USERNAME}

ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}

ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PORT=${REDIS_PORT}
ENV REDIS_PASSWORD=${REDIS_PASSWORD}

ENV LOGSTASH_HOST=${LOGSTASH_HOST}
ENV LOGSTASH_PORT=${LOGSTASH_PORT}

RUN microdnf install findutils

WORKDIR /app

COPY . /app
RUN chmod +x ./gradlew

WORKDIR /app/web
RUN ./gradlew build -x test

CMD ["sh", "-c", "java -jar -Dspring.profiles.active=product,execution_time build/libs/web-${VERSION}.jar"]