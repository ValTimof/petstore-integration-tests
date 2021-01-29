package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.api.PetApi;
import io.github.timofeevvr.petstore.api.StoreApi;
import io.restassured.builder.RequestSpecBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(value = "threadlocal", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PetstoreSteps {

    @Value("${petstore.baseUrl}")
    private String baseUrl;
    public final ApiClient apiClient;

    public PetstoreSteps() {
        this.apiClient = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setBaseUri(baseUrl))
        );
    }

    public PetApi pet() {
        return apiClient.pet();
    }

    public StoreApi store() {
        return apiClient.store();
    }
}
