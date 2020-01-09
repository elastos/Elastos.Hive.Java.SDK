package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.vendor.ipfs.IPFSOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ClientIPFSInterfaceTest {
    private static Client client ;
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testPutData() {
        IPFS ipfsAPIs = client.getIPFS();
        // TODO;
    }

    @Test
    public void testPutDataFrom() {
        IPFS ipfsAPIs = client.getIPFS();
        // TODO;
    }

    @Test
    public void testPutDataFromStream() {
        IPFS ipfsAPIs = client.getIPFS();
        // TODO;
    }

    @Test
    public void testPutDataFromReader() {
        IPFS ipfsAPIs = client.getIPFS();
        // TODO;
    }

    @Test
    public void testDelete() {
        IPFS ipfsAPIs = client.getIPFS();
        // TODO;
    }

    @BeforeClass
    public static void setUp() {
        try {
            IPFSOptions.RpcNode node = new IPFSOptions.RpcNode("3.133.166.156",5001);
            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(node)
                    .build();

            client = Client.createInstance(options);
            client.connect();
        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        client.disconnect();
        client = null;
    }
}
