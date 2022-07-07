package org.elastos.hive;

import org.elastos.did.DIDBackend;
import org.elastos.did.DefaultDIDAdapter;
import org.elastos.hive.config.TestData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppContextTest {
    @Disabled
    @Test
    @Order(1) void testGetProviderAddress() {
        Assertions.assertDoesNotThrow(()->{
            DIDBackend.initialize(new DefaultDIDAdapter("https://api.elastos.io/eid"));
            String providerAddress = TestData.getInstance().getAppContext().getProviderAddress().get();
            System.out.println("Provider address: " + providerAddress);
            Assertions.assertNotNull(providerAddress);
        });
    }
}
