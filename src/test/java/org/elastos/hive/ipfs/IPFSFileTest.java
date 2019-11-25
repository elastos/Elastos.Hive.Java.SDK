package org.elastos.hive.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.result.CID;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.Length;

import org.elastos.hive.result.Result;
import org.elastos.hive.util.Md5CaculateUtil;
import org.elastos.hive.util.TestUtils;
import org.elastos.hive.utils.LogUtil;
import org.elastos.hive.vendors.HiveRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.elastos.hive.vendors.ipfs.IPFSFile;
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

public class IPFSFileTest {
    private static IHiveConnect hiveConnect ;
    private static HiveClient hiveClient ;
    private static HiveRpcNode[] hiveRpcNodes = new HiveRpcNode[5];
    private static IPFSFile hiveFile ;

    private static final CID EXPECTED_CID = new CID("QmaY6wjwnybJgd5F4FD6pPL6h9vjXrGv2BJbxxUC1ojUbQ");
    private static final CID TEST_CID = new CID("QmaY6wjwnybJgd5F4FD6pPL6h9vjXrGv2BJbxxUC1ojUbQ");

    private static final String TEST_FILE_PATH = System.getProperty("user.dir")+"/src/resources/org/elastos/hive/test.txt";

    private static final Length EXPECTED_LENGTH = new Length(17);
    private static final String EXPECTED_FILE_MD5 = "973131af48aa1d25bf187dacaa5ca7c0";

    private static final String EXPECTED_STR = "this is test file" ;

    private static final String STORE_FILE_PATH = System.getProperty("user.dir")+"/src/resources/org/elastos/hive/storetest.txt";


    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = HiveClient.createInstance(hiveOptions);

        hiveRpcNodes[0] = new HiveRpcNode("127.0.0.1",5001);
        hiveRpcNodes[1] = new HiveRpcNode("3.133.166.156",5001);
        hiveRpcNodes[2] = new HiveRpcNode("13.59.79.222",5001);
        hiveRpcNodes[3] = new HiveRpcNode("3.133.71.168",5001);
        hiveRpcNodes[4] = new HiveRpcNode("107.191.44.124",5001);

        HiveConnectOptions hiveConnectOptions = new IPFSConnectOptions(hiveRpcNodes);
        try {
            hiveConnect = hiveClient.connect(hiveConnectOptions);
        } catch (HiveException e) {
            e.printStackTrace();
        }

        if (hiveConnect!=null){
            hiveFile = hiveConnect.createHiveFile();
        }
    }

    @AfterClass
    public static void tearDown() {
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }

    @Test
    public void testGetInstance() {
        assertNotNull(hiveFile);
    }

    @Test
    public void testPutFile() {
        if (hiveFile!=null){
            try {
                CID cid = hiveFile.putFile(TEST_FILE_PATH,false).get();
                assertEquals(EXPECTED_CID.getCid() , cid.getCid());
            } catch (InterruptedException e) {
                assertNull(e);
                e.printStackTrace();
            } catch (ExecutionException e) {
                assertNull(e);
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPutFileAsync() {
        if (hiveFile!=null) {
            CompletableFuture future = hiveFile.putFile(TEST_FILE_PATH, false, new Callback<CID>() {
                @Override
                public void onError(HiveException e) {

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
        if (hiveFile!=null) {
            try {
                CID cid = hiveFile.putFileFromBuffer(EXPECTED_STR.getBytes(), false).get();
                LogUtil.d("result == " + cid.getCid());
                assertEquals(EXPECTED_CID.getCid(), cid.getCid());
            } catch (InterruptedException e) {
                e.printStackTrace();
                assertNull(e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                assertNull(e);
            }
        }
    }

    @Test
    public void testPutBufferAsync() {
        if (hiveFile!=null) {
            CompletableFuture future = hiveFile.putFileFromBuffer(EXPECTED_STR.getBytes(), false, new Callback<CID>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
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
        if (hiveFile!=null) {
            try {
                Length length = hiveFile.getFileLength(TEST_CID).get();
                LogUtil.d("length=" + length.getLength());
                assertEquals(EXPECTED_LENGTH.getLength(), length.getLength());
            } catch (InterruptedException e) {
                e.printStackTrace();
                assertNull(e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                assertNull(e);
            }
        }
    }

    @Test
    public void testGetFileLengthAsync() {
        CompletableFuture future = hiveFile.getFileLength(TEST_CID, new Callback<Length>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
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
        if (file.exists()) file.delete();

        try {
            Length length = hiveFile.getFile(TEST_CID,false,STORE_FILE_PATH).get();

            String actualMD5 = Md5CaculateUtil.getFileMD5(STORE_FILE_PATH) ;

            assertEquals(EXPECTED_LENGTH.getLength(),length.getLength());
            assertEquals(EXPECTED_FILE_MD5, actualMD5);

            LogUtil.d("actualMD5="+actualMD5);
            LogUtil.d("length="+length.getLength());
        } catch (InterruptedException e) {
            assertNull(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFileAsync() {
        CompletableFuture future = hiveFile.getFile(TEST_CID, false, STORE_FILE_PATH, new Callback<Length>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
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
            Data data = hiveFile.getFileToBuffer(TEST_CID,false).get();
            Assert.assertArrayEquals(EXPECTED_STR.getBytes(),data.getData());
        } catch (InterruptedException e) {
            assertNull(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }


    @Test
    public void testGetBufferAsync() {
        CompletableFuture future = hiveFile.getFileToBuffer(TEST_CID, false, new Callback<Data>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
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
