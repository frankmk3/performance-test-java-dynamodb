package com.test.performance.dynamodb.configuration;


import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
public class DynamodbConfig {

    @Value("${aws.accessKey}")
    String accessKey;

    @Value("${aws.secretKey}")
    String secretKey;

    @Value("${dynamodb.endpoint}")
    String dynamoEndpoint;

    @Bean
    AwsBasicCredentials awsBasicCredentials(){
        return AwsBasicCredentials.create(accessKey, secretKey);
    }

    @Bean
    DynamoDbAsyncClient dynamoDbAsyncClient(AwsBasicCredentials awsBasicCredentials){
        DynamoDbAsyncClientBuilder clientBuilder = DynamoDbAsyncClient.builder();
        clientBuilder
            .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials));
        if(!dynamoEndpoint.isEmpty()){
            clientBuilder.endpointOverride(URI.create(dynamoEndpoint));
        }
        return clientBuilder.build();
    }

}
