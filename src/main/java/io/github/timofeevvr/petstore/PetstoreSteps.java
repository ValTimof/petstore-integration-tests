package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.model.Order;
import io.github.timofeevvr.petstore.model.Pet;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Validatable;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

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

    @Step
    @com.epam.reportportal.annotations.Step
    public Pet addPet(Pet pet) {
        return apiClient.pet().addPet()
                .body(pet)
                .execute(Validatable::then)
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .extract()
                .as(Pet.class);
    }

    @Step
    @com.epam.reportportal.annotations.Step
    public Pet getExistingPet(Long petId) {
        return getPet(petId)
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .as(Pet.class);
    }

    public ValidatableResponse getPet(Long petId) {
        return apiClient.pet().getPetById()
                .petIdPath(petId)
                .execute(Validatable::then);
    }

    @Step
    @com.epam.reportportal.annotations.Step
    public void deletePet(Pet pet) {
        deletePet(pet.getId());
    }

    public void deletePet(Long petId) {
        apiClient.pet().deletePet()
                .petIdPath(petId)
                .execute(Validatable::then)
                .statusCode(HTTP_OK);
    }


    @Step
    @com.epam.reportportal.annotations.Step
    public Pet[] findPetsByStatus(Pet.StatusEnum status) {
        return apiClient.pet()
                .findPetsByStatus()
                .statusQuery(status)
                .execute(Validatable::then)
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .body("size()", greaterThan(0))
                .extract()
                .as(Pet[].class);
    }


    @Step
    @com.epam.reportportal.annotations.Step
    public void placeOrder(Order order) {
        apiClient.store().placeOrder()
                .body(order)
                .execute(Validatable::then)
                .assertThat()
                .statusCode(HTTP_OK);
    }


    @Step
    @com.epam.reportportal.annotations.Step
    public ValidatableResponse getExistingOrder(Long orderId) {
        return getOrder(orderId)
                .assertThat()
                .statusCode(HTTP_OK);
    }

    public ValidatableResponse getOrder(Long orderId) {
        return apiClient.store().getOrderById()
                .orderIdPath(orderId)
                .execute(Validatable::then);
    }
}
