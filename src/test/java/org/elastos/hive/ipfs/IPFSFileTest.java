package org.elastos.hive.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.ClientOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.ConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.result.CID;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.Length;

import org.elastos.hive.util.Md5CaculateUtil;
import org.elastos.hive.util.TestUtils;
import org.elastos.hive.utils.LogUtil;
import org.elastos.hive.vendors.ipfs.IPFSRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class IPFSFileTest {
    private static Client client ;
    private static HiveConnect connect ;
    private static IPFSRpcNode[] rpcNodes = new IPFSRpcNode[5];

    private static final CID EXPECTED_CID = new CID("QmaY6wjwnybJgd5F4FD6pPL6h9vjXrGv2BJbxxUC1ojUbQ");
    private static final CID TEST_CID = new CID("QmaY6wjwnybJgd5F4FD6pPL6h9vjXrGv2BJbxxUC1ojUbQ");

    private static final String TEST_FILE_PATH = System.getProperty("user.dir")+"/src/resources/org/elastos/hive/test.txt";

    private static final Length EXPECTED_LENGTH = new Length(17);
    private static final String EXPECTED_FILE_MD5 = "973131af48aa1d25bf187dacaa5ca7c0";

    private static final String EXPECTED_STR = "this is test file" ;

    private static final String STORE_FILE_PATH = System.getProperty("user.dir")+"/src/resources/org/elastos/hive/storetest.txt";

    private static final String STORE_PATH = System.getProperty("user.dir");
    @BeforeClass
    public static void setUp() {
        ClientOptions options = new ClientOptions
                .Builder()
                .setStorePath(STORE_PATH)
                .build();
        client = new Client(options);

        rpcNodes[0] = new IPFSRpcNode("127.0.0.1",5001);
        rpcNodes[1] = new IPFSRpcNode("3.133.166.156",5001);
        rpcNodes[2] = new IPFSRpcNode("13.59.79.222",5001);
        rpcNodes[3] = new IPFSRpcNode("3.133.71.168",5001);
        rpcNodes[4] = new IPFSRpcNode("107.191.44.124",5001);

        ConnectOptions connectOptions = new IPFSConnectOptions
                .Builder()
                .setRpcNodes(rpcNodes)
                .build();
        connect = client.connect(connectOptions);
    }

    @AfterClass
    public static void tearDown() {
        client.disConnect(connect);
    }

    @Test
    public void testPutFile() {
        if (connect != null){
            try {
                CID cid = connect.putIPFSFile(TEST_FILE_PATH,false).get();
                assertEquals(EXPECTED_CID.getCid() , cid.getCid());
            } catch (ExecutionException | InterruptedException e) {
                fail();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPutFileAsync() {
        if (connect !=null) {
            CompletableFuture future = connect.putIPFSFile(TEST_FILE_PATH, false, new Callback<CID>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(CID body) {
                    LogUtil.d("result == " + body.getCid());
                    assertEquals(EXPECTED_CID.getCid(), body.getCid());
                }
            });

            TestUtils.waitFinish(future);
            assertFalse(future.isCompletedExceptionally());
        }
    }

    @Test
    public void testPutBuffer() {
        if (connect!=null) {
            try {
                CID cid = connect.putIPFSFileFromBuffer(EXPECTED_STR.getBytes(), false).get();
                LogUtil.d("result == " + cid.getCid());
                assertEquals(EXPECTED_CID.getCid(), cid.getCid());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    @Test
    public void testPutBufferAsync() {
        if (connect!=null) {
            CompletableFuture future = connect.putIPFSFileFromBuffer(EXPECTED_STR.getBytes(), false, new Callback<CID>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(CID body) {
                    LogUtil.d("result == " + body.getCid());
                    assertEquals(EXPECTED_CID.getCid(), body.getCid());
                }
            });

            TestUtils.waitFinish(future);
            assertFalse(future.isCompletedExceptionally());
        }
    }

    @Test
    public void testGetFileLength() {
        if (connect !=null) {
            try {
                Length length = connect.getIPFSFileLength(TEST_CID).get();
                LogUtil.d("length=" + length.getLength());
                assertEquals(EXPECTED_LENGTH.getLength(), length.getLength());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    @Test
    public void testGetFileLengthAsync() {
        CompletableFuture future = connect.getIPFSFileLength(TEST_CID, new Callback<Length>() {
            @Override
            public void onError(HiveException e) {
                fail();
            }

            @Override
            public void onSuccess(Length body) {
                LogUtil.d("length = "+body.getLength());
                assertEquals(EXPECTED_LENGTH.getLength(),body.getLength());
            }
        });

        TestUtils.waitFinish(future);
        assertFalse(future.isCompletedExceptionally());
    }

    @Test
    public void testGetFile() {
        File file = new File(STORE_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        try {
            Length length = connect.getIPFSFile(TEST_CID,false,STORE_FILE_PATH).get();

            String actualMD5 = Md5CaculateUtil.getFileMD5(STORE_FILE_PATH) ;

            assertEquals(EXPECTED_LENGTH.getLength(),length.getLength());
            assertEquals(EXPECTED_FILE_MD5, actualMD5);

            LogUtil.d("actualMD5="+actualMD5);
            LogUtil.d("length="+length.getLength());
        } catch (InterruptedException | ExecutionException e) {
            assertNull(e);
            fail();
        }
    }

    @Test
    public void testGetFileAsync() {
        CompletableFuture future = connect.getIPFSFile(TEST_CID, false, STORE_FILE_PATH, new Callback<Length>() {
            @Override
            public void onError(HiveException e) {
                fail();
            }

            @Override
            public void onSuccess(Length body) {
                String actualMD5 = Md5CaculateUtil.getFileMD5(STORE_FILE_PATH);

                assertEquals(EXPECTED_LENGTH.getLength(), body.getLength());
                assertEquals(EXPECTED_FILE_MD5, actualMD5);

                LogUtil.d("actualMD5=" + actualMD5);
                LogUtil.d("length=" + body.getLength());
            }
        });

        TestUtils.waitFinish(future);
        assertFalse(future.isCompletedExceptionally());
    }

    @Test
    public void testGetBuffer() {
        try {
            Data data = connect.getIPFSFileToBuffer(TEST_CID,false).get();
            Assert.assertArrayEquals(EXPECTED_STR.getBytes(),data.getData());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void testGetBufferAsync() {
        CompletableFuture future = connect.getIPFSFileToBuffer(TEST_CID, false, new Callback<Data>() {
            @Override
            public void onError(HiveException e) {
                fail();
            }

            @Override
            public void onSuccess(Data body) {
                assertNotNull(body);
                Assert.assertArrayEquals(EXPECTED_STR.getBytes(),body.getData());
            }
        });

        TestUtils.waitFinish(future);
        assertFalse(future.isCompletedExceptionally());
    }
}
