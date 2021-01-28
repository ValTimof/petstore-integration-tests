package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.model.Order;
import io.github.timofeevvr.petstore.model.Pet;
import io.restassured.filter.log.ErrorLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@Tag("store")
class StoreApiIntegrationTest extends TestBase {

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 5, 10})
    void givenExistingPet_whenPlaceOrderForThePet_thenOrderStatusIsPlaced(long orderId) {
        log.info("WHEN. FIND AVAILABLE PET ID");
        Long existingPetId = petstoreSteps.pet()
                .reqSpec(r -> r.addFilter(new ErrorLoggingFilter()))
                .findPetsByStatus()
                .statusQuery(Pet.StatusEnum.AVAILABLE)
                .execute(response -> response)
                .then().log().status()
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .body("size()", greaterThan(0))
                .extract()
                .as(Pet[].class)[0]
                .getId();
        Order order = new Order()
                .id(orderId)
                .petId(existingPetId)
                .quantity(1)
                .status(Order.StatusEnum.PLACED);
        log.info("WHEN. PLACE ORDER");
        petstoreSteps.store().placeOrder()
                .body(order)
                .execute(response -> response)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("petId", equalTo(order.getPetId()));
        log.info("THEN. CHECK ORDER IS PLACED");
        petstoreSteps.store().getOrderById()
                .orderIdPath(orderId)
                .execute(response -> response)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("status", equalTo(Order.StatusEnum.PLACED.getValue()));
    }
}