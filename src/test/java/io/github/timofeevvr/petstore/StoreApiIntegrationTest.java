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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@Tag("store")
class StoreApiIntegrationTest extends TestBase {

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 5, 10})
    void givenExistingPet_whenPlaceOrderForThePet_thenOrderStatusIsPlaced(long orderId) {
        // given
        Long existingPetId = petstoreSteps.findPetsByStatus(Pet.StatusEnum.AVAILABLE)[0].getId();
        Order order = new Order()
                .id(orderId)
                .petId(existingPetId)
                .quantity(1)
                .status(Order.StatusEnum.PLACED);
        // when
        petstoreSteps.placeOrder(order);
        // then
        petstoreSteps.getExistingOrder(orderId)
                .assertThat()
                .body("status", equalTo(Order.StatusEnum.PLACED.getValue()));
    }
}