FROM openjdk:17

WORKDIR /usrapp/bin

ENV PORT=8080

COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency
COPY src/main/java/edu/eci/arep/webserver/static src/main/java/edu/eci/arep/webserver/static

CMD ["java","-cp","./classes:./dependency/*","edu.eci.arep.webserver.MicroServer"]