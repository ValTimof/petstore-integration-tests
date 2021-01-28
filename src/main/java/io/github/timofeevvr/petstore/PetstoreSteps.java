package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.api.PetApi;
import io.github.timofeevvr.petstore.api.StoreApi;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PetstoreSteps {

    @Value("${petstore.baseUrl}")
    private String baseUrl;
    public final ApiClient apiClient;

    public PetstoreSteps() {
        this.apiClient = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder()
                        .setBaseUri(baseUrl)
                        .addFilter(new ResponseLoggingFilter())
                        .addFilter(new RequestLoggingFilter())
                )
        );
    }

    public PetApi pet() {
        return apiClient.pet();
    }

    public StoreApi store() {
        return apiClient.store();
    }
}
