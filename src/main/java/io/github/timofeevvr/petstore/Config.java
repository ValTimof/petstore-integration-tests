package io.github.timofeevvr.petstore;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.io.IoBuilder;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;

import javax.annotation.PostConstruct;
import java.io.PrintStream;

import static io.github.timofeevvr.petstore.GsonObjectMapper.gson;

@Slf4j
@Configuration
public class Config {

    @PostConstruct
    void configureFramework() {
        // print AssertJ asserts descriptions
        Assertions.setPrintAssertionsDescription(true);

        // RestAssured global configuration
        RestAssured.objectMapper(gson());

        PrintStream logStream = IoBuilder.forLogger(log.getName()).buildPrintStream();
        RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(logStream));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new LoggingFilter());
    }

    /**
     * Add 'threadlocal' scope to create new object for each thread
     */
    @Bean
    public static CustomScopeConfigurer threadLocalScopeRegistration() {
        CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
        scopeConfigurer.addScope("threadlocal", new SimpleThreadScope());
        return scopeConfigurer;
    }

}
