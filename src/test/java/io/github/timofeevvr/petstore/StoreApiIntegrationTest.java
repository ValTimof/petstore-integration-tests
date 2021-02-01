package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Tag("store")
class StoreApiIntegrationTest extends TestBase {

    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest(name = "[{index}] {displayName}")
    @MethodSource("io.github.timofeevvr.petstore.DataProvider#randomOrders")
    void placeOrderAndCheckStatusIsPlaced(Order order) {
        // given
        order.setStatus(Order.StatusEnum.PLACED);
        // when
        var placedOrder = petstoreSteps.placeOrder(order);
        // then
        petstoreSteps.getExistingOrder(placedOrder.getId())
                .assertThat()
                .body("status", equalTo(Order.StatusEnum.PLACED.getValue()));
    }
}