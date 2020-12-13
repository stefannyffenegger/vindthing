FROM openjdk:16-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
LABEL "traefik.enable"="true"
LABEL "traefik.http.routers.vindthing-api.rule"="Host(`vindthing-api.scientists.ch`)"
LABEL "traefik.http.routers.vindthing-api.entrypoints"="websecure"
LABEL "traefik.http.routers.vindthing-api.tls.certresolver"="myresolver"