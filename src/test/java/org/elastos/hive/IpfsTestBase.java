package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.elastos.hive.vendors.ipfs.IPFSParameter;

public class IpfsTestBase {
	static public Client login() {
		try {
			String uid = null;
			IPFSParameter parameter = new IPFSParameter(uid);
			Client client = Client.createInstance(parameter);

			assertNotNull(client);

			client.login(null);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ipfs Login failed");
		}

		return null;
    }
}
