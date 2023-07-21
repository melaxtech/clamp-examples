#replace with path to application properties
#clamp-nlp-service.jar is created using `mvn clean package`
java -jar target/clamp-nlp-service.jar --spring.config.location=src/main/resources/application.properties
