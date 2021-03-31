package org.elastos.hive;

import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
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
        try {
            TestData testData = TestData.getInstance();
            paymentService = new VaultSubscription(testData.getAppContext(), testData.getOwnerDid(), testData.getProviderAddress());
        } catch (HiveException | DIDException e) {
            Assertions.fail(Throwables.getStackTraceAsString(e));
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testGetPricingPlanList() {
        try {
            List<PricingPlan> plans = paymentService.getPricingPlanList().get();
            Assertions.assertNotNull(plans);
            Assertions.assertFalse(plans.isEmpty());
        } catch (Exception e) {
            Assertions.fail(Throwables.getStackTraceAsString(e));
        }
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetPricingPlan() {
        try {
            PricingPlan plan = paymentService.getPricingPlan(PRICING_PLAN_NAME).get();
            Assertions.assertNotNull(plan);
        } catch (Exception e) {
            Assertions.fail(Throwables.getStackTraceAsString(e));
        }
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testOrderProcess() {
        try {
            Order order = paymentService.placeOrder(PRICING_PLAN_NAME).get();
            Assertions.assertNotNull(order);
            order = paymentService.getOrder(order.getOrderId()).get();
            Assertions.assertNotNull(order);
            Receipt receipt = paymentService.payOrder(order.getOrderId(), Collections.emptyList()).get();
            Assertions.assertNotNull(receipt);
        } catch (Exception e) {
            Assertions.fail(Throwables.getStackTraceAsString(e));
        }
    }
}
