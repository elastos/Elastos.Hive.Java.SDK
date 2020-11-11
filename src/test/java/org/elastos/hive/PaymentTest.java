package org.elastos.hive;

import org.elastos.hive.payment.PricingPlan;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentTest {

	private static Payment paymentApi;
	private static final String planName = "Free";
	private static final String priceName = "priceName";
	private static final String orderId = "orderId";

	@Test
	public void test00_userTrail() throws ExecutionException, InterruptedException {
		paymentApi.useTrial()
				.whenComplete((aBoolean, throwable) -> {
					assertNull(throwable);
					assertTrue(aBoolean);
				}).get();
	}

	@Test
	public void test01_getPricingPlans() throws ExecutionException, InterruptedException {
		paymentApi.getAllPricingPlans().whenComplete((pricingPlans, throwable) -> {
			assertNull(throwable);
			assertNotNull(pricingPlans);
			assertTrue(pricingPlans.size()>0);
		}).get();
	}

	public void test02_getPricingPlan() {
		try {
			paymentApi.getPricingPlan(planName).get();
		} catch (Exception e) {
			fail();
		}
	}

	public void test03_placeOrder() {
		try {
			paymentApi.placeOrder(priceName);
		} catch (Exception e) {
			fail();
		}
	}

	public void test04_payOrder() {
		try {
			List<String> txids = new ArrayList<>();
			txids.add("xxxxxxxxxxx");
			paymentApi.payOrder(orderId, txids);
		} catch (Exception e) {
			fail();
		}
	}

	public void test05_useTrial() {
		try {
			paymentApi.useTrial();
		} catch (Exception e) {
			fail();
		}
	}

	public void test06_getOrder() {
		try {
			paymentApi.getOrder(orderId);
		} catch (Exception e) {
			fail();
		}
	}

	public void test07_getAllOrders() {
		try {
			paymentApi.getAllOrders();
		} catch (Exception e) {
			fail();
		}
	}

	public void test08_getUsingPricePlan() {
		try {
			paymentApi.getUsingPricePlan();
		} catch (Exception e) {
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = UserFactory.createUser2().getVault();
		paymentApi = vault.getPayment();
	}
}
