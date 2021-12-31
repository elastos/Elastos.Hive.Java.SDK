package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.provider.BackupDetail;
import org.elastos.hive.provider.FilledOrderDetail;
import org.elastos.hive.provider.VaultDetail;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

@Disabled
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
    @Order(3) void testGetFilledOrders() {
        Assertions.assertDoesNotThrow(()-> {
            List<FilledOrderDetail> backups = provider.getFilledOrders().handle((res, ex) -> {
                if (ex != null) {
                    Assertions.assertEquals(ex.getCause().getClass(), NotFoundException.class);
                    return new ArrayList<FilledOrderDetail>();
                }
                return res;
            }).get();
            Assertions.assertNotNull(backups);
        });
    }
}
