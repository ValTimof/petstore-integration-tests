package io.github.timofeevvr.petstore;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.io.IoBuilder;
import org.assertj.core.api.Assertions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.PrintStream;

import static io.github.timofeevvr.petstore.GsonObjectMapper.gson;

@Slf4j
@Configuration
public class Config {

    @PostConstruct
    void configure() {
        Assertions.setPrintAssertionsDescription(true);

        PrintStream logStream = IoBuilder.forLogger(log.getName()).buildPrintStream();

        RestAssured.objectMapper(gson());
        RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(logStream));
    }

}
