package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultPaymentTest {
    private static final String PRICING_PLAN_NAME = "Rookie";

    private static PaymentService paymentService;

    @BeforeAll
    public static void setUp() {
        Assertions.assertDoesNotThrow(()->{
            TestData testData = TestData.getInstance();
            paymentService = new VaultSubscription(
                    testData.getAppContext(),
                    testData.getOwnerDid(),
                    testData.getProviderAddress());
        });
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testGetPricingPlanList() {
        Assertions.assertDoesNotThrow(()->{
            List<PricingPlan> plans = paymentService.getPricingPlanList().get();
            Assertions.assertNotNull(plans);
            Assertions.assertFalse(plans.isEmpty());
        });
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetPricingPlan() {
        Assertions.assertDoesNotThrow(()->
                Assertions.assertNotNull(paymentService.getPricingPlan(PRICING_PLAN_NAME).get()));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testOrderProcess() {
        Assertions.assertDoesNotThrow(()->{
            Order order = paymentService.placeOrder(PRICING_PLAN_NAME).get();
            Assertions.assertNotNull(order);
            order = paymentService.getOrder(order.getOrderId()).get();
            Assertions.assertNotNull(order);
            Receipt receipt = paymentService.payOrder(order.getOrderId(), Collections.emptyList()).get();
            Assertions.assertNotNull(receipt);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testGetReceipt() {
        //TODO:
    }
}
