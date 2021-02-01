package io.github.timofeevvr.petstore;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;

import javax.annotation.PostConstruct;

import static io.github.timofeevvr.petstore.GsonObjectMapper.gson;

@Slf4j
@Configuration
public class Config {

    @PostConstruct
    void configureFramework() {
        // print AssertJ asserts descriptions
        Assertions.setDescriptionConsumer(desc -> log.info("Assert: {}", desc));

        // RestAssured global configuration
        // Use Gson
        RestAssured.objectMapper(gson());
        // Add custom logger
        RestAssured.filters(new LoggingFilter());
        // Log ALL if validation fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Adds 'threadlocal' scope to create new object for each thread.
     */
    @Bean
    public static CustomScopeConfigurer threadLocalScopeRegistration() {
        CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
        scopeConfigurer.addScope("threadlocal", new SimpleThreadScope());
        return scopeConfigurer;
    }

}
