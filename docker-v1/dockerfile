FROM ubuntu
ENV TZ=Europe/Moscow

RUN mkdir web
WORKDIR ./web

COPY /user-service .

RUN apt-get update
RUN apt-get install openjdk-17-jdk openjdk-17-jre -y
RUN apt-get install maven -y
RUN mvn clean install

CMD ["mvn", "spring-boot:run"]
