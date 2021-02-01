package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.model.Order;
import io.github.timofeevvr.petstore.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Tag("store")
class StoreApiIntegrationTest extends TestBase {

    @ParameterizedTest(name = "[{index}] {displayName}")
    @MethodSource("io.github.timofeevvr.petstore.DataProvider#orderProvider")
    void givenExistingPet_whenPlaceOrderForThePet_thenOrderStatusIsPlaced(Order order) {
        // given
        var existingPets = petstoreSteps.findPetsByStatus(Pet.StatusEnum.AVAILABLE);
        assertThat(existingPets)
                .as("Check at least one pet is available")
                .isNotEmpty();
        var existingPetId = existingPets[0].getId();
        order.quantity(1)
                .petId(existingPetId)
                .setStatus(Order.StatusEnum.PLACED);
        // when
        var placedOrder = petstoreSteps.placeOrder(order);
        // then
        petstoreSteps.getExistingOrder(placedOrder.getId())
                .assertThat()
                .body("status", equalTo(Order.StatusEnum.PLACED.getValue()));
    }
}