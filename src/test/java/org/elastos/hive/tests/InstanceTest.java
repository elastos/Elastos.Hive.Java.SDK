package org.elastos.hive.tests;

import org.elastos.hive.Vault;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Ignore
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
