package com.test.performance.dynamodb.events;

import com.test.performance.dynamodb.repository.ReportDynamoDBRepository;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Component
@Slf4j
public class AppReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    final ReportDynamoDBRepository reportDynamoDBRepository;

    @Autowired
    public AppReadyEvent(ReportDynamoDBRepository reportDynamoDBRepository) {
        this.reportDynamoDBRepository = reportDynamoDBRepository;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
           reportDynamoDBRepository.createIfNotExists();
        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
