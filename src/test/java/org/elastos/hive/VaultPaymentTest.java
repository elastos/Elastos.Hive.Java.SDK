package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VaultPaymentTest {
    private static final String PRICING_PLAN_NAME = "Rookie";

    private static PaymentService paymentService;

    @BeforeAll
    public static void setUp() {
        try {
            TestData testData = TestData.getInstance();
            paymentService = new VaultSubscription(testData.getAppContext(), testData.getOwnerDid(), testData.getProviderAddress());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testGetPricingPlanList() {
        try {
            List<PricingPlan> plans = paymentService.getPricingPlanList().exceptionally(e->{
                fail();
                return null;
            }).get();
            assertNotNull(plans);
            assertFalse(plans.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testGetPricingPlan() {
        try {
            PricingPlan plan = paymentService.getPricingPlan(PRICING_PLAN_NAME).exceptionally(e->{
                fail();
                return null;
            }).get();
            assertNotNull(plan);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testOrderProcess() {
        try {
            Order order = paymentService.placeOrder(PRICING_PLAN_NAME).exceptionally(e->{
                fail();
                return null;
            }).get();
            assertNotNull(order);
            order = paymentService.getOrder(order.getOrderId()).exceptionally(e->{
                fail();
                return null;
            }).get();
            assertNotNull(order);
            Receipt receipt = paymentService.payOrder(order.getOrderId(), Arrays.asList("abcd","efgh")).exceptionally(e->{
                fail();
                return null;
            }).get();
            assertNotNull(receipt);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
