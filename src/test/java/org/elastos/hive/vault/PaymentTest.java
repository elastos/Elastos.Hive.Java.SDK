package org.elastos.hive.vault;

import org.elastos.hive.Payment;
import org.elastos.hive.Vault;
import org.elastos.hive.payment.PricingPlan;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentTest {

	private static Payment paymentApi;
	private static final String planName = "planName";
	private static final String priceName = "priceName";
	private static final String orderId = "orderId";

	public void test00_getPricingPlans() {
		try {
			List<PricingPlan> pricingPlans = paymentApi.getAllPricingPlans().get();
			assertNotNull(pricingPlans);
		} catch (Exception e) {
			fail();
		}
	}

	public void test01_getPricingPlan() {
		try {
			paymentApi.getPricingPlan(planName).get();
		} catch (Exception e) {
			fail();
		}
	}

	public void test02_placeOrder() {
		try {
			paymentApi.placeOrder(planName, priceName);
		} catch (Exception e) {
			fail();
		}
	}

	public void test03_payOrder() {
		try {
			List<String> txids = new ArrayList<>();
			txids.add("xxxxxxxxxxx");
			paymentApi.payOrder(orderId, txids);
		} catch (Exception e) {
			fail();
		}
	}

	public void test04_useTrial() {
		try {
			paymentApi.useTrial();
		} catch (Exception e) {
			fail();
		}
	}

	public void test05_getOrder() {
		try {
			paymentApi.getOrder(orderId);
		} catch (Exception e) {
			fail();
		}
	}

	public void test06_getAllOrders() {
		try {
			paymentApi.getAllOrders();
		} catch (Exception e) {
			fail();
		}
	}

	public void test07_getUsingPricePlan() {
		try {
			paymentApi.getUsingPricePlan();
		} catch (Exception e) {
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = TestFactory.createFactory().getVault();
		paymentApi = vault.getPayment();
	}
}
