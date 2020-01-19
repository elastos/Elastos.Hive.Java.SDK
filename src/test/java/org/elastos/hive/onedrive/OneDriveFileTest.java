package org.elastos.hive.onedrive;


import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.vendor.onedrive.OneDriveOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveFileTest {

    private static final String CLIENTID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";
    private static final String REDIRECTURL = "http://localhost:12345";
    private static final String STORE_PATH = System.getProperty("user.dir");
    private String testFilepath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";
    private String storeFilepath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/storetest.txt";

    private String testFileName = "testFile.txt";
    private final long EXPECT_FILE_LENGTH = 17;

    private String testBufferFileName = "testBuffer.txt";

    private String testBufferString = "this is test for buffer";


    private static Client hiveClient;

    private static Files onedriveFileApi ;

    //    @Test
//    public void test_00_Prepare() {
//        String[] result = null;
//        try {
//            FileList fileList = hiveConnect.listFile().get();
//            result = fileList.getList();
//            for (int i = 0 ; i<result.length ; i++){
//                LogUtil.d("file = "+result[i]);
//                assertNotNull(result[i]);
//            }
//            if (result==null || result.length<1) return;
//
//            for (String name :result){
//                hiveConnect.deleteFile(name).get();
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void test_01_PutFile() {
//        try {
//            hiveConnect.putFile(testFileName,testFilepath,false).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assertNull(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            assertNull(e);
//        }
//    }
//
//    @Test
//    public void test_11_PutFileAsync() {
//        CompletableFuture future = hiveConnect.putFile(testFileName, testFilepath, false, new Callback<Void>() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(Void body) {
//                assertNotNull(body);
//            }
//        });
//
//        TestUtils.waitFinish(future);
//    }
//
//    @Test
//    public void test_02_PutBuffer() {
//        try {
//            hiveConnect.putFileFromBuffer(testBufferFileName,testBufferString.getBytes() ,false).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assertNull(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            assertNull(e);
//        }
//    }
//
//    @Test
//    public void test_12_PutBufferAsync() {
//        CompletableFuture future = hiveConnect.putFileFromBuffer(testBufferFileName, testBufferString.getBytes(), false, new Callback<Void>() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(Void body) {
//                assertNotNull(body);
//            }
//        });
//
//        TestUtils.waitFinish(future);
//    }
//
//    @Test
//    public void test_03_FileLength(){
//        try {
//            Length length = hiveConnect.getFileLength(testFileName).get();
//            LogUtil.d("length = "+length.getLength());
//
//            assertEquals(EXPECT_FILE_LENGTH,length.getLength());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assertNull(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            assertNull(e);
//        }
//    }
//
//    @Test
//    public void test_13_FileLengthAsync(){
//        CompletableFuture future = hiveConnect.getFileLength(testFileName, new Callback<Length>() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(Length body) {
//                assertNotNull(body);
//                assertEquals(EXPECT_FILE_LENGTH,body.getLength());
//            }
//        });
//
//        TestUtils.waitFinish(future);
//    }
//
//    @Test
//    public void test_04_GetFile(){
//        File file = new File(storeFilepath);
//        if (file.exists()) file.delete();
//
//        try {
//            hiveConnect.getFile(testFileName,false,storeFilepath).get();
//
//            String expectMd5 = Md5CaculateUtil.getFileMD5(testFilepath);
//            String actualMd5 = Md5CaculateUtil.getFileMD5(storeFilepath);
//
//            LogUtil.d("expectMd5 = "+expectMd5);
//            LogUtil.d("actualMd5 = "+actualMd5);
//
//            assertEquals(expectMd5,actualMd5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assertNull(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            assertNull(e);
//        }
//    }
//
//    @Test
//    public void test_14_GetFileAsync(){
//        File file = new File(storeFilepath);
//        if (file.exists()) file.delete();
//
//        CompletableFuture future = hiveConnect.getFile(testFileName, false, storeFilepath, new Callback<Length>() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(Length body) {
//                assertNotNull(body);
//            }
//        });
//
//        String expectMd5 = Md5CaculateUtil.getFileMD5(testFilepath);
//        String actualMd5 = Md5CaculateUtil.getFileMD5(storeFilepath);
//
//        LogUtil.d("expectMd5 = "+expectMd5);
//        LogUtil.d("actualMd5 = "+actualMd5);
//
//        assertEquals(expectMd5,actualMd5);
//
//        TestUtils.waitFinish(future);
//    }
//
//
//    @Test
//    public void test_05_GetFileBuffer(){
//        try {
//            Value data = hiveConnect.getFileToBuffer(testBufferFileName,false).get();
//
//            byte[] expectBytes = testBufferString.getBytes() ;
//            byte[] actualBytes = data.getData();
//
//            Assert.assertArrayEquals(expectBytes,actualBytes);
//
//            String str = new String(data.getData());
//            LogUtil.d("result = "+str);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assertNull(e);
//        } catch (ExecutionException e) {
//            assertNull(e);
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void test_15_GetFileBufferAsync(){
//        CompletableFuture future = hiveConnect.getFileToBuffer(testBufferFileName, false, new Callback<Value>() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(Value body) {
//                assertNotNull(body);
//
//                byte[] expectBytes = testBufferString.getBytes() ;
//                byte[] actualBytes = body.getData();
//
//                Assert.assertArrayEquals(expectBytes,actualBytes);
//
//                String str = new String(body.getData());
//                LogUtil.d("result = "+str);
//            }
//        });
//
//        TestUtils.waitFinish(future);
//    }
//
//
//    @Test
//    public void test_06_ListFiles(){
//        try {
//            FileList fileList = hiveConnect.listFile().get();
//            String[] result = fileList.getList();
//            for (int i = 0 ; i<result.length ; i++){
//                LogUtil.d("file = "+result[i]);
//                assertNotNull(result[i]);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            assertNull(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            assertNull(e);
//        }
//    }
//
//    @Test
//    public void test_16_ListFilesAsync(){
//        CompletableFuture future = hiveConnect.listFile(new Callback<FileList>() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(FileList body) {
//                String[] result = body.getList();
//                for (int i = 0 ; i<result.length ; i++){
//                    LogUtil.d("file = "+result[i]);
//                    assertNotNull(result[i]);
//                }
//            }
//        });
//
//        TestUtils.waitFinish(future);
//    }
//
//
//    @Test
//    public void test_07_DeleteFile(){
//        try {
//            Void result = hiveConnect.deleteFile(testFileName).get();
//            assertNotNull(result);
//        } catch (InterruptedException e) {
//            assertNull(e);
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            assertNull(e);
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void test_17_DeleteFileAsync(){
//        CompletableFuture future = hiveConnect.deleteFile(testFileName, new Callback() {
//            @Override
//            public void onError(HiveException e) {
//                assertNull(e);
//            }
//
//            @Override
//            public void onSuccess(Result body) {
//                assertNotNull(body);
//            }
//        });
//        TestUtils.waitFinish(future);
//    }
//
    @BeforeClass
    public static void setUp() {
        try {
            Client.Options options = new OneDriveOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .setClientId(CLIENTID)
                    .setRedirectUrl(REDIRECTURL)
                    .setAuthenticator(requestUrl -> {
                        try {
                            Desktop.getDesktop().browse(new URI(requestUrl));
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail();
                        }
                    })
                    .build();

            hiveClient = Client.createInstance(options);

            hiveClient.connect();

            onedriveFileApi = hiveClient.getFiles();
        } catch (
                HiveException e) {
            fail(e.getMessage());
        }

    }

    @AfterClass
    public static void tearDown() {
        hiveClient.disconnect();
        hiveClient = null ;
    }

    @Test
    public void testPutBuffer() {
        try {
            onedriveFileApi.put("test buffer".getBytes(),"testbuffer.txt").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutString() {
        try {
            onedriveFileApi.put("test","test.txt").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutStream() {
        String path = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

        try {
            InputStream inputStream = new FileInputStream(path);
            CompletableFuture<Void> future = onedriveFileApi.put(inputStream,"test.txt");

            future.get();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutReader() {

        try {
            Reader reader = new StringReader("readertest");

            CompletableFuture<Void> future = onedriveFileApi.put(reader,"test.txt");

            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetString() {
        try {
            String result = onedriveFileApi.getAsString("testBuffer.txt").get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBuffer() {
        try {
            byte[] result = onedriveFileApi.getAsBuffer("testBuffer.txt").get();
            String resultStr = new String(result);
            System.out.println(resultStr);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetLength() {
        try {
            long size = onedriveFileApi.size("testBuffer.txt").get();
            System.out.println(size);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        try {
            onedriveFileApi.delete("testbuffer.txt").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testList() {
        try {
            ArrayList<String> files = onedriveFileApi.list().get();
            for (String fileName :files){
                System.out.println(fileName);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetOutput() {
        OutputStream outputStream = new ByteArrayOutputStream();
        CompletableFuture<Long> completableFuture = onedriveFileApi.get("testbuffer.txt", outputStream);
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

    @Test
    public void testGetWriter() {
        Writer writer = new StringWriter();
        CompletableFuture<Long> completableFuture = onedriveFileApi.get("testbuffer.txt", writer);
        try {
            long length = completableFuture.get();
            System.out.println(length);
            System.out.println(writer.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
