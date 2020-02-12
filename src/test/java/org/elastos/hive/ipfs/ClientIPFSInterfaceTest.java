package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.util.TestUtils;
import org.elastos.hive.vendor.ipfs.IPFSOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

public class ClientIPFSInterfaceTest {
    private static final String STORE_PATH = System.getProperty("user.dir");
    private static final String[] IPADDRS = {"3.133.166.156", "127.0.0.1"};

    private static Client client;
    private static IPFS ipfsAPIs;

    private String data = "aaa";
    private String cid = "QmaY6wjwnybJgd5F4FD6pPL6h9vjXrGv2BJbxxUC1ojUbQ";
    private String path = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

    @Test
    public void testPutData() {
    }

    @Test
    public void testPutDataInByteArray() {
        try {
            CompletableFuture<String> future = ipfsAPIs.put(data.getBytes());
            TestUtils.waitFinish(future);

            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPutString() {
        try {
            CompletableFuture<String> future = ipfsAPIs.put(data);
            String cid = future.get();
            System.out.println(cid);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPutDataFromInputStream() {
        try {
            CompletableFuture<String> future = ipfsAPIs.put(new FileInputStream(path));

            System.out.println(future.get());
        } catch (FileNotFoundException | InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPutDataFromFileReader() {
        try {
            Reader reader = new FileReader(path);
            CompletableFuture<String> future = ipfsAPIs.put(reader);
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException | FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetFileSize() {
        try {
            CompletableFuture<Long> completableFuture = ipfsAPIs.size(cid);
            long length = completableFuture.get();
            System.out.println(length);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetBuffer() {
        try {
            CompletableFuture<byte[]> completableFuture = ipfsAPIs.getAsBuffer(cid);

            byte[] buffer = completableFuture.get();
            String result = new String(buffer);
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetStringBuffer() {
        try {
            CompletableFuture<String> completableFuture = ipfsAPIs.getAsString(cid);

            String result = completableFuture.get();
            System.out.println("result=" + result);
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetOutput() {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            CompletableFuture<Long> completableFuture = ipfsAPIs.get(cid, outputStream);

            long length = completableFuture.get();
            System.out.println(length);
            System.out.println(outputStream.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[0], 5001))
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[1], 5001))
                    .build();

            client = Client.createInstance(options);
            client.connect();

            ipfsAPIs = client.getIPFS();
        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        if (client != null) {
            if (client.isConnected())
                client.disconnect();
            client = null;
        }
    }
}
