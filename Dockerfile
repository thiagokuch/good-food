FROM openjdk:11
EXPOSE 8080
ENV JAVA_OPTS -Duser.timezone=America/Sao_Paulo
ADD build/libs/good-food*.jar /opt/api.jar
ENTRYPOINT exec java -jar /opt/api.jar