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

import java.util.Collection;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
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
        log.info("Adding pet {}", pet.getName());
        return apiClient.pet().addPet()
                .body(pet)
                .execute(Validatable::then)
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .extract()
                .as(Pet.class);
    }

    public Pet getExistingPet(Long petId) {
        return getPet(petId)
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .as(Pet.class);
    }

    @Step
    @com.epam.reportportal.annotations.Step
    public ValidatableResponse getPet(Long petId) {
        log.info("Getting pet {}", petId);
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
        log.info("Deleting pet {}", petId);
        apiClient.pet().deletePet()
                .petIdPath(petId)
                .execute(Validatable::then)
                .assertThat()
                // sometimes returns 404 probably because the pet was deleted by another user
                .statusCode(anyOf(is(HTTP_OK), is(HTTP_NOT_FOUND)));
    }

    public Pet[] findAllPets() {
        return findPetsByStatus(List.of(Pet.StatusEnum.values()));
    }

    public Pet[] findPetsByStatus(Pet.StatusEnum status) {
        return findPetsByStatus(singletonList(status));
    }

    @Step
    @com.epam.reportportal.annotations.Step
    public Pet[] findPetsByStatus(Collection<Pet.StatusEnum> statuses) {
        log.info("Find pet with statuses {}", statuses);
        return apiClient.pet()
                .findPetsByStatus()
                .statusQuery(statuses.toArray(new Object[0]))
                .execute(Validatable::then)
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .extract()
                .as(Pet[].class);
    }

    @Step
    @com.epam.reportportal.annotations.Step
    public Order placeOrder(Order order) {
        log.info("Placing order for pet with id {}", order.getPetId());
        return apiClient.store().placeOrder()
                .body(order)
                .execute(Validatable::then)
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .as(Order.class);
    }

    public ValidatableResponse getExistingOrder(Long orderId) {
        return getOrder(orderId)
                .assertThat()
                .statusCode(HTTP_OK);
    }

    @Step
    @com.epam.reportportal.annotations.Step
    public ValidatableResponse getOrder(Long orderId) {
        log.info("Getting order {}", orderId);
        return apiClient.store().getOrderById()
                .orderIdPath(orderId)
                .execute(Validatable::then);
    }
}
