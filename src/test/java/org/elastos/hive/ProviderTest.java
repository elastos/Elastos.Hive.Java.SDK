package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.endpoint.*;
import org.elastos.hive.exception.NotFoundException;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProviderTest {
    private static Provider provider;
    private static VaultSubscription subscription;

    @BeforeAll
    public static void setUp() {
        trySubscribeVault();
        Assertions.assertDoesNotThrow(()->provider = TestData.getInstance().newProvider());
    }

    private static void trySubscribeVault() {
        Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
        try {
            subscription.subscribe();
        } catch (NotFoundException e) {}
    }

    @Test
    @Order(1) void testGetVaults() {
        Assertions.assertDoesNotThrow(()->{
            List<VaultDetail> vaults = provider.getVaults().get();
            Assertions.assertNotNull(vaults);
            Assertions.assertFalse(vaults.isEmpty());
        });
    }

    @Test
    @Order(2) void testGetBackups() {
        Assertions.assertDoesNotThrow(()->{
            List<BackupDetail> backups = provider.getBackups().handle((res, ex) -> {
                if (ex != null) {
                    Assertions.assertEquals(ex.getCause().getClass(), NotFoundException.class);
                    return new ArrayList<BackupDetail>();
                }
                return res;
            }).get();
            Assertions.assertNotNull(backups);
        });
    }

    @Test
    @Order(3) void testGetUsers() {
        Assertions.assertDoesNotThrow(()->{
            List<UserDetail> users = provider.getUsers().get();
            Assertions.assertNotNull(users);
            Assertions.assertFalse(users.isEmpty());
        });
    }

    @Test
    @Order(4) void testGetPayments() {
        Assertions.assertDoesNotThrow(()-> {
            List<PaymentDetail> backups = provider.getPayments().handle((res, ex) -> {
                if (ex != null) {
                    Assertions.assertEquals(ex.getCause().getClass(), NotFoundException.class);
                    return new ArrayList<PaymentDetail>();
                }
                return res;
            }).get();
            Assertions.assertNotNull(backups);
        });
    }

    @Test
    @Order(5) void testDeleteVaults() {
        Assertions.assertDoesNotThrow(()->{
            List<String> userDids = Arrays.asList("did:elastos:imedtHyjLS155Gedhv7vKP3FTWjpBUAUm5",
                    "did:elastos:imedtHyjLS155Gedhv7vKP3FTWjpBUAUm6");
            provider.deleteVaults(userDids).get();
        });
    }

    @Test
    @Order(6) void testDeleteBackups() {
        Assertions.assertDoesNotThrow(()->{
            List<String> userDids = Arrays.asList("did:elastos:imedtHyjLS155Gedhv7vKP3FTWjpBUAUm5",
                    "did:elastos:imedtHyjLS155Gedhv7vKP3FTWjpBUAUm6");
            provider.deleteBackups(userDids).get();
        });
    }

    @Test
    @Order(7) void testGetVaultApps() {
        Assertions.assertDoesNotThrow(()->{
            List<VaultAppDetail> apps = provider.getVaultApps().get();
            Assertions.assertNotNull(apps);
            Assertions.assertFalse(apps.isEmpty());
        });
    }

    @Test
    @Order(8) void testDeleteVaultApps() {
        Assertions.assertDoesNotThrow(()->{
            List<String> appDids = Arrays.asList("did:elastos:imedtHyjLS155Gedhv7vKP3FTWjpBUAUm5",
                    "did:elastos:imedtHyjLS155Gedhv7vKP3FTWjpBUAUm6");
            provider.deleteVaultApps(appDids).get();
        });
    }
}
