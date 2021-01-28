package com.petstore.integration.tests;

import com.petstore.integration.tests.model.Order;
import com.petstore.integration.tests.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petstore.integration.tests.model.Order.StatusEnum.PLACED;
import static com.petstore.integration.tests.model.Pet.StatusEnum.AVAILABLE;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;

@Slf4j
@Tag("store")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreApiIntegrationTest {

    @Autowired
    private ApiClient petstoreClient;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 5, 10})
    void givenExistingPet_whenPlaceOrderForThePet_thenOrderStatusIsPlaced(long orderId) {
        log.info("WHEN. FIND AVAILABLE PET ID");
        Long existingPetId = petstoreClient.pet().findPetsByStatus()
                .statusQuery(AVAILABLE)
                .execute(response -> response)
                .then()
                .log()
                .status()
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
                .status(PLACED);
        log.info("WHEN. PLACE ORDER");
        petstoreClient.store().placeOrder()
                .body(order)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .log()
                .body()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("petId", equalTo(order.getPetId().intValue()));
        log.info("THEN. CHECK ORDER IS PLACED");
        petstoreClient.store().getOrderById()
                .orderIdPath(orderId)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .log()
                .body()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("status", equalTo(PLACED.getValue()));
    }
}