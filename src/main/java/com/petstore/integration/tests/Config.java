package com.petstore.integration.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.io.IoBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;

@Slf4j
@Configuration
public class Config {
    @Value("${petstore.baseUrl}")
    private String baseUrl;

    @Bean
    public ApiClient petstoreClient() {
        //redirect out to log4j2 in case Report Portal will be used
        PrintStream logStream = IoBuilder.forLogger(log.getName()).buildPrintStream();

        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder()
                        .setBaseUri(baseUrl)
                        .setConfig(RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(logStream)))
                        .log(LogDetail.METHOD)
                        .log(LogDetail.URI)));
    }

}
