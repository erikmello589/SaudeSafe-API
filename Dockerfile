#Construindo imagem para a aplicação (Spring Boot)

FROM openjdk

WORKDIR /saudesafeapp

COPY target/tcc-0.0.1-SNAPSHOT.jar /saudesafeapp/saudesafespring-app.jar 

ENTRYPOINT [ "java", "-jar", "saudesafespring-app.jar" ]