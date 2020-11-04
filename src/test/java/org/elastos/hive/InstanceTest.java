package org.elastos.hive;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class InstanceTest {

    @Test
    public void testGetVaultInstance() {
        try {
            Vault vault = UserFactory.createUser1().getVault();
            assertNotNull(vault);
        } catch (Exception e) {
            fail();
        }
    }



}
