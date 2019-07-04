package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.vendors.ipfs.IPFSParameter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class IpfsTestBase {
	private static final String[] rpcAddrs = {
			"52.83.119.110",
			"52.83.159.189",
			"3.16.202.140",
			"18.217.147.205",
			"18.219.53.133"
	};

	static public Client login() {
		try {
			String uid = null;
			IPFSParameter parameter = new IPFSParameter(new IPFSEntry(uid, rpcAddrs), System.getProperty("user.dir"));
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
