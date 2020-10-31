package org.elastos.hive;

import org.elastos.hive.Vault;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class InstanceTest {

    @Test
    public void testGetVaultInstance() {
        try {
            Vault vault = TestFactory.createFactory().getVault();
            assertNotNull(vault);
        } catch (Exception e) {
            fail();
        }
    }

}
