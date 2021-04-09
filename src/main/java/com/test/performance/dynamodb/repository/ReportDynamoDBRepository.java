package com.test.performance.dynamodb.repository;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Repository
public class ReportDynamoDBRepository {

    private static final String TABLE_NAME = "report";
    private static final String ID_COLUMN = "id";

    final DynamoDbAsyncClient dynamoDbAsyncClient;

    @Autowired
    public ReportDynamoDBRepository(DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public CompletableFuture<CreateTableResponse> createTable(String tableName) {
        KeySchemaElement keySchemaElement = KeySchemaElement
            .builder()
            .attributeName(ID_COLUMN)
            .keyType(KeyType.HASH)
            .build();

        AttributeDefinition attributeDefinition = AttributeDefinition
            .builder()
            .attributeName(ID_COLUMN)
            .attributeType(ScalarAttributeType.S)
            .build();

        CreateTableRequest request = CreateTableRequest.builder()
                                                       .tableName(tableName)
                                                       .keySchema(keySchemaElement)
                                                       .attributeDefinitions(attributeDefinition)
                                                       .billingMode(BillingMode.PAY_PER_REQUEST)
                                                       .build();

        return dynamoDbAsyncClient.createTable(request);
    }


    public CompletableFuture<DeleteTableResponse> deleteTable(String tableName) {
        DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                                                                  .tableName(tableName)
                                                                  .build();
        return dynamoDbAsyncClient.deleteTable(deleteTableRequest);
    }

    private CompletableFuture<ListTablesResponse> listTables() {
        ListTablesRequest request = ListTablesRequest
            .builder()
            .exclusiveStartTableName(TABLE_NAME)
            .build();
        return dynamoDbAsyncClient.listTables(request);

    }

    public void createIfNotExists() throws ExecutionException, InterruptedException {
        CompletableFuture<ListTablesResponse> listTableResponse = listTables();

        CompletableFuture<CreateTableResponse> createTableRequest = listTableResponse
            .thenCompose(response -> {
                boolean tableExist = response
                    .tableNames()
                    .contains(TABLE_NAME);

                if (!tableExist) {
                    return createTable(TABLE_NAME);

                } else {
                    return CompletableFuture.completedFuture(null);

                }
            });

        //Wait in synchronous manner for table creation
        createTableRequest.get();

    }

    public CompletableFuture<PutItemResponse> insert(Map<String, AttributeValue> item) {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                                                      .tableName(TABLE_NAME)
                                                      .item(item)
                                                      .build();

        return dynamoDbAsyncClient.putItem(putItemRequest);
    }

    public CompletableFuture<Map<String, AttributeValue>> getById(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(ID_COLUMN, AttributeValue.builder()
                                    .s(id)
                                    .build());

        GetItemRequest getRequest = GetItemRequest.builder()
                                                  .tableName(TABLE_NAME)
                                                  .key(key)
//                                                  .attributesToGet(BODY_COLUMN)
                                                  .build();
        return dynamoDbAsyncClient.getItem(getRequest)
                                  .thenApply(item -> {
                                      if (!item.hasItem()) {
                                          return null;
                                      } else {
                                          return item.item();
                                      }
                                  });
    }
}