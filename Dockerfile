FROM openjdk:17
COPY build/libs/mankala-game-api-0.0.1.jar .
CMD /usr/bin/java -Dlogging.path=/log/ -Xmx400m -Xms400m -jar mankala-game-api-0.0.1.jar
EXPOSE 8080