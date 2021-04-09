package com.test.performance.dynamodb.controller;

import static org.hamcrest.core.IsEqual.equalTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.performance.dynamodb.common.Constants;
import com.test.performance.dynamodb.common.ReportDataGenerator;
import com.test.performance.dynamodb.dto.ReportCreation;
import com.test.performance.dynamodb.model.Report;
import com.test.performance.dynamodb.service.ReportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(ReportController.class)
class ReportControllerTest {

    private static final int PAGE = 0;
    private static final int SIZE = 20;
    private static final int MAX_SIZE = 100;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private ReportService reportService;

    @MockBean
    private Constants constants;

    @BeforeEach
    void init() {
        Mockito.when(constants.getPaginatorPage())
               .thenReturn(PAGE);
        Mockito.when(constants.getPaginatorSize())
               .thenReturn(SIZE);
        Mockito.when(constants.getPaginatorMaxSize())
               .thenReturn(MAX_SIZE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5", "6"})
    void canCallReportsEndpoint_whenNoParameterProvided_returnReportsUsingDefaultPager(final String group) {
        Report report = ReportDataGenerator.generateReport(group);

        Mockito.when(
            reportService.getById(
                report.getId()
            )).thenReturn(Mono.just(report));

        webTestClient.get()
                     .uri(String.format("/reports/%s", report.getId()))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(Report.class)
                     .value(response -> response, equalTo(report));
    }


    @ParameterizedTest
    @ValueSource(longs = {1, 10, 5, 2, 0})
    void canCallCreateReportEndpoint_whenGroupV_returnReportsUsingDefaultPager(final long elements) {

        String group = "group" + elements;
        ReportCreation reportCreation = new ReportCreation(elements, group);

        Mockito.when(reportService.create(ArgumentMatchers.any(Report.class)))
               .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        webTestClient.post()
                     .uri("/reports")
                     .bodyValue(reportCreation)
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(ReportCreation.class)
                     .value(r -> r, equalTo(reportCreation));
    }
}