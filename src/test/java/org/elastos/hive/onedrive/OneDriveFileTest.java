package org.elastos.hive.onedrive;

/*
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.HiveException;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Result;
import org.elastos.hive.util.Md5CaculateUtil;
import org.elastos.hive.util.TestUtils;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.Void;
import org.elastos.hive.utils.LogUtil;
import org.elastos.hive.vendors.onedrive.OneDriveConnectOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveFileTest {

    private static final String APPID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
    private static final String SCOPE = "User.Read Files.ReadWrite.All offline_access";//offline_access Files.ReadWrite
    private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316
    private static final String STORE_PATH = System.getProperty("user.dir");
    private String testFilepath = System.getProperty("user.dir")+"/src/resources/org/elastos/hive/test.txt";
    private String storeFilepath = System.getProperty("user.dir")+"/src/resources/org/elastos/hive/storetest.txt";

    private String testFileName = "testFile.txt";
    private final long EXPECT_FILE_LENGTH = 17 ;

    private String testBufferFileName = "testBuffer.txt";

    private String testBufferString = "this is test for buffer";


    private static HiveConnect hiveConnect ;
    private static Client hiveClient ;

    @Test
    public void test_00_Prepare() {
        String[] result = null;
        try {
            FileList fileList = hiveConnect.listFile().get();
            result = fileList.getList();
            for (int i = 0 ; i<result.length ; i++){
                LogUtil.d("file = "+result[i]);
                assertNotNull(result[i]);
            }
            if (result==null || result.length<1) return;

            for (String name :result){
                hiveConnect.deleteFile(name).get();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01_PutFile() {
        try {
            hiveConnect.putFile(testFileName,testFilepath,false).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_11_PutFileAsync() {
        CompletableFuture future = hiveConnect.putFile(testFileName, testFilepath, false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });

        TestUtils.waitFinish(future);
    }

    @Test
    public void test_02_PutBuffer() {
        try {
            hiveConnect.putFileFromBuffer(testBufferFileName,testBufferString.getBytes() ,false).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_12_PutBufferAsync() {
        CompletableFuture future = hiveConnect.putFileFromBuffer(testBufferFileName, testBufferString.getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });

        TestUtils.waitFinish(future);
    }

    @Test
    public void test_03_FileLength(){
        try {
            Length length = hiveConnect.getFileLength(testFileName).get();
            LogUtil.d("length = "+length.getLength());

            assertEquals(EXPECT_FILE_LENGTH,length.getLength());
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_13_FileLengthAsync(){
        CompletableFuture future = hiveConnect.getFileLength(testFileName, new Callback<Length>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Length body) {
                assertNotNull(body);
                assertEquals(EXPECT_FILE_LENGTH,body.getLength());
            }
        });

        TestUtils.waitFinish(future);
    }

    @Test
    public void test_04_GetFile(){
        File file = new File(storeFilepath);
        if (file.exists()) file.delete();

        try {
            hiveConnect.getFile(testFileName,false,storeFilepath).get();

            String expectMd5 = Md5CaculateUtil.getFileMD5(testFilepath);
            String actualMd5 = Md5CaculateUtil.getFileMD5(storeFilepath);

            LogUtil.d("expectMd5 = "+expectMd5);
            LogUtil.d("actualMd5 = "+actualMd5);

            assertEquals(expectMd5,actualMd5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_14_GetFileAsync(){
        File file = new File(storeFilepath);
        if (file.exists()) file.delete();

        CompletableFuture future = hiveConnect.getFile(testFileName, false, storeFilepath, new Callback<Length>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Length body) {
                assertNotNull(body);
            }
        });

        String expectMd5 = Md5CaculateUtil.getFileMD5(testFilepath);
        String actualMd5 = Md5CaculateUtil.getFileMD5(storeFilepath);

        LogUtil.d("expectMd5 = "+expectMd5);
        LogUtil.d("actualMd5 = "+actualMd5);

        assertEquals(expectMd5,actualMd5);

        TestUtils.waitFinish(future);
    }


    @Test
    public void test_05_GetFileBuffer(){
        try {
            Value data = hiveConnect.getFileToBuffer(testBufferFileName,false).get();

            byte[] expectBytes = testBufferString.getBytes() ;
            byte[] actualBytes = data.getData();

            Assert.assertArrayEquals(expectBytes,actualBytes);

            String str = new String(data.getData());
            LogUtil.d("result = "+str);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void test_15_GetFileBufferAsync(){
        CompletableFuture future = hiveConnect.getFileToBuffer(testBufferFileName, false, new Callback<Value>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Value body) {
                assertNotNull(body);

                byte[] expectBytes = testBufferString.getBytes() ;
                byte[] actualBytes = body.getData();

                Assert.assertArrayEquals(expectBytes,actualBytes);

                String str = new String(body.getData());
                LogUtil.d("result = "+str);
            }
        });

        TestUtils.waitFinish(future);
    }


    @Test
    public void test_06_ListFiles(){
        try {
            FileList fileList = hiveConnect.listFile().get();
            String[] result = fileList.getList();
            for (int i = 0 ; i<result.length ; i++){
                LogUtil.d("file = "+result[i]);
                assertNotNull(result[i]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_16_ListFilesAsync(){
        CompletableFuture future = hiveConnect.listFile(new Callback<FileList>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(FileList body) {
                String[] result = body.getList();
                for (int i = 0 ; i<result.length ; i++){
                    LogUtil.d("file = "+result[i]);
                    assertNotNull(result[i]);
                }
            }
        });

        TestUtils.waitFinish(future);
    }


    @Test
    public void test_07_DeleteFile(){
        try {
            Void result = hiveConnect.deleteFile(testFileName).get();
            assertNotNull(result);
        } catch (InterruptedException e) {
            assertNull(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void test_17_DeleteFileAsync(){
        CompletableFuture future = hiveConnect.deleteFile(testFileName, new Callback() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Result body) {
                assertNotNull(body);
            }
        });
        TestUtils.waitFinish(future);
    }

    @BeforeClass
    public static void setUp() {
        ClientOptions hiveOptions = new ClientOptions.Builder().setStorePath(STORE_PATH).build();

        hiveClient = new Client(hiveOptions);

        ConnectOptions hiveConnectOptions =
                new OneDriveConnectOptions.Builder()
                        .setClientId(APPID)
                        .setRedirectUrl(REDIRECTURL)
                        .setAuthenticator(requestUrl -> {
                            try {
                                Desktop.getDesktop().browse(new URI(requestUrl));
                            } catch (Exception e) {
                                e.printStackTrace();
                                fail("Authenticator failed");
                            }
                        })
                        .build();

        hiveConnect = hiveClient.connect(hiveConnectOptions);
    }



    @AfterClass
    public static void tearDown() {
        hiveClient.disConnect(hiveConnect);
    }
}
*/