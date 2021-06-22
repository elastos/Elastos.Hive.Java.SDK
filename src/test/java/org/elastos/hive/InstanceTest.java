package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.jupiter.api.Test;

public class InstanceTest {

    Vault vault;

    @Test
    public void testGetVaultInstance() {
        try {
            vault = AppInstanceFactory.configSelector().getVault();
            assertNotNull(vault);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
