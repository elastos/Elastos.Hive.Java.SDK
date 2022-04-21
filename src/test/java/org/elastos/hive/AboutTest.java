package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.endpoint.NodeInfo;
import org.elastos.hive.endpoint.NodeVersion;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AboutTest {
	private static VaultSubscription subscription;

	@BeforeAll public static void setup() {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
	}

	@Test @Order(1) void testGetVersion() {
		Assertions.assertDoesNotThrow(()->{
			NodeVersion version = subscription.getNodeVersion().get();
			Assertions.assertNotNull(version);
			Assertions.assertTrue(version.major() > 0);
		});
	}

	@Test @Order(2) void testGetCommitId() {
		Assertions.assertDoesNotThrow(()-> {
			String commitId = subscription.getLatestCommitId().get();
			Assertions.assertNotNull(commitId);
		});
	}

	@Test @Order(3) void testGetNodeInfo() {
		Assertions.assertDoesNotThrow(()-> {
			NodeInfo info = subscription.getNodeInfo().get();
			Assertions.assertNotNull(info);
			Assertions.assertNotNull(info.getName());
			Assertions.assertNotNull(info.getOwnershipPresentation());
			Assertions.assertNotNull(info.getDescription());
			Assertions.assertNotNull(info.getEmail());
			Assertions.assertNotNull(info.getOwnerDid());
			Assertions.assertNotNull(info.getLastCommitId());
			Assertions.assertNotNull(info.getVersion());
			Assertions.assertNotNull(info.getServiceDid());
		});
	}
}
