package org.elastos.hive;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class InstanceTest {

    Vault vault;

    @Test
    public void testGetVaultInstance() {
        try {
            vault = UserFactory.createUser2().getVault();
            assertNotNull(vault);
        } catch (Exception e) {
            fail();
        }
    }
}
