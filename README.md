# performance-test-java-dynamodb
Create a basic application to measure the performance using a DynamoDb webflux java spring boot application.

## Installation and Getting started

### Pre requisites
Java JDK 11 

#### Optional
To run the DynamoDB instance using [Docker](https://docs.docker.com) and build and start the application run:
```shell script
 sh dockerbuild.sh 
```

Replace the environment variable for the DynamoDB access in the docker-compos.yml file:
```
      AWS_ACCESSKEY: "fakemykeyid"
      AWS_SECRETKEY: "fakesecretaccesskey"
```

### Build 
```shell script
sh gradlew build
```

### Run application 
```shell script
java -jar build/libs/performance-test-java-dynamodb.jar
``` 

#### Application url ####
One the service starts all the endpoints will be available in swagger:
 
 - Swagger [http://localhost:1401/v1/test/dynamodb/swagger-ui](http://localhost:1401/v1/test/dynamodb/swagger-ui)

