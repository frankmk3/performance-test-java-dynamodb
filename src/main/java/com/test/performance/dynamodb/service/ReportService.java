package com.test.performance.dynamodb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.performance.dynamodb.model.Report;
import com.test.performance.dynamodb.repository.ReportDynamoDBRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
public class ReportService {

    final ReportDynamoDBRepository reportDynamoDBRepository;
    final ObjectMapper objectMapper;

    @Autowired
    public ReportService(ReportDynamoDBRepository reportDynamoDBRepository) {
        this.reportDynamoDBRepository = reportDynamoDBRepository;
        objectMapper = new ObjectMapper();
    }

    public Mono<Report> create(final Report report) {
        Map<String, AttributeValue> item = ((Map<String, Object>) objectMapper.convertValue(report, Map.class))
            .entrySet()
            .stream()
            .filter(e -> e.getValue() != null)
            .collect(Collectors.toMap(Entry::getKey, v -> AttributeValue.builder()
                                                                        .s(v.getValue()
                                                                            .toString())
                                                                        .build()));

        return Mono.fromCompletionStage(reportDynamoDBRepository.insert(item))
                   .map(r -> report);

    }

    public Mono<Report> getById(final String id) {
        return Mono.fromCompletionStage(reportDynamoDBRepository.getById(id))
                   .map(itemAttr -> itemAttr != null ? objectMapper.convertValue(
                       itemAttr
                           .entrySet()
                           .stream()
                           .collect(
                               Collectors.toMap(
                                   Entry::getKey,
                                   a -> a.getValue()
                                         .s()
                               )
                           ),
                       Report.class) : null);

    }
}