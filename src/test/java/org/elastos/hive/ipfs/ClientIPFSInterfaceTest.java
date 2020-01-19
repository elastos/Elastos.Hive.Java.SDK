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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

public class ClientIPFSInterfaceTest {
    private static Client client;
    private static final String STORE_PATH = System.getProperty("user.dir");
    private static IPFS ipfsAPIs;

    String cid = "QmaY6wjwnybJgd5F4FD6pPL6h9vjXrGv2BJbxxUC1ojUbQ";

    String filePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

    @Test
    public void testPutData() {

    }

    @Test
    public void testPutDataFrom() {
        byte[] buf = "aaa".getBytes();
        CompletableFuture<String> future = ipfsAPIs.put(buf);

        TestUtils.waitFinish(future);


        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutStringData() {

        CompletableFuture<String> future = ipfsAPIs.put("aaa");
        try {
            String cid = future.get();
            System.out.println(cid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutDataFromStream() {
        String path = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

        try {
            InputStream inputStream = new FileInputStream(path);
            CompletableFuture<String> future = ipfsAPIs.put(inputStream);

            System.out.println(future.get());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutDataFromReader() {
        try {
            Reader reader = new FileReader(filePath);
            CompletableFuture<String> future = ipfsAPIs.put(reader);
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFileSize() {
        CompletableFuture<Long> completableFuture = ipfsAPIs.size(cid);
        try {
            long length = completableFuture.get();
            System.out.println(length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBuffer() {
        CompletableFuture<byte[]> completableFuture = ipfsAPIs.getAsBuffer(cid);
        try {
            byte[] buffer = completableFuture.get();
            String result = new String(buffer);
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStringBuffer() {
        CompletableFuture<String> completableFuture = ipfsAPIs.getAsString(cid);
        try {
            String result = completableFuture.get();
            System.out.println("result="+result);
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetOutput() {
        OutputStream outputStream = new ByteArrayOutputStream();
        CompletableFuture<Long> completableFuture = ipfsAPIs.get(cid, outputStream);
        try {
            long length = completableFuture.get();
            System.out.println(length);
            System.out.println(outputStream.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            IPFSOptions.RpcNode node = new IPFSOptions.RpcNode("3.133.166.156", 5001);
            IPFSOptions.RpcNode node2 = new IPFSOptions.RpcNode("127.0.0.1", 5001);

            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(node)
                    .addRpcNode(node2)
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
        if (client != null) client.disconnect();
        client = null;
    }
}
