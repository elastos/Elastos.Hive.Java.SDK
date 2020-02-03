package org.elastos.hive.onedrive;


import org.elastos.hive.Callback;
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
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import okio.Buffer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveFileTest {

    private static final String CLIENTID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";
    private static final String REDIRECTURL = "http://localhost:12345";
    private static final String STORE_PATH = System.getProperty("user.dir");
    private String testFilepath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

    private String remoteBufferFileName = "testBuffer.txt";
    private String remoteStringFileName = "testString.txt";
    private String remoteInputFileName = "testInput.txt";
    private String remoteReaderFileName = "testReader.txt";

    private byte[] testBuffer = "this is test for buffer".getBytes();
    private String testString = "this is test for String";

    private String stringReader = "this is test for reader";

    private static Client hiveClient;
    private static Files onedriveFileApi;

    @Test
    public void test_00_Prepare() {
        try {
            ArrayList<String> list = onedriveFileApi.list().get();
            if (list == null || list.size() < 1)
                return;
            for (String fileName : list) {
                onedriveFileApi.delete(fileName).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_101_PutString() {
        try {
            onedriveFileApi.put(testString, remoteStringFileName).get();
            assertTrue(checkFileExist(remoteStringFileName));
            checkFileContent(testString, remoteStringFileName);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }


    @Test
    public void test_102_PutBuffer() {
        try {
            onedriveFileApi.put(testBuffer, remoteBufferFileName).get();
            assertTrue(checkFileExist(remoteBufferFileName));
            checkFileContent(new String(testBuffer), remoteBufferFileName);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_103_PutInputStream() {
        try {
            onedriveFileApi.put(createInputStream(testFilepath), remoteInputFileName).get();
            assertTrue(checkFileExist(remoteInputFileName));
            checkFileContent(readFromInputStream(createInputStream(testFilepath)), remoteInputFileName);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_104_PutReader() {
        try {
            onedriveFileApi.put(createReader(stringReader), remoteReaderFileName).get();
            assertTrue(checkFileExist(remoteReaderFileName));
            checkFileContent(stringReader, remoteReaderFileName);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_105_Size() {
        try {
            long size = onedriveFileApi.size(remoteBufferFileName).get();
            assertEquals(testBuffer.length, size);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_106_getAsString() {
        try {
            String result = onedriveFileApi.getAsString(remoteStringFileName).get();
            assertEquals(testString, result);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_107_getAsBuffer() {
        try {
            byte[] result = onedriveFileApi.getAsBuffer(remoteBufferFileName).get();
            assertArrayEquals(testBuffer, result);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_108_getOutput() {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            long size = onedriveFileApi.get(remoteStringFileName, outputStream).get();
            assertEquals(testString, outputStream.toString());
            assertEquals(testString.length(), size);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_109_getWriter() {
        try {
            Writer writer = new CharArrayWriter();
            long size = onedriveFileApi.get(remoteStringFileName, writer).get();
            assertEquals(testString, writer.toString());
            assertEquals(testString.length(), size);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_110_list() {
        try {
            ArrayList<String> files = onedriveFileApi.list().get();
            assertEquals(4, files.size());
            for (String fileName : files) {
                assertTrue(checkListFileName(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_111_Delete() {
        try {
            onedriveFileApi.delete(remoteStringFileName).get();
            ArrayList<String> files = onedriveFileApi.list().get();
            assertEquals(3, files.size());

            for (String fileName : files) {
                assertTrue(checkListFileNameAfterDel(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }


    @Test
    public void test_201_PutStringAsync() {
        try {
            CompletableFuture future = onedriveFileApi.put(testString, remoteStringFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(checkFileExist(remoteStringFileName));
                    checkFileContent(testString, remoteStringFileName);
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_202_PutBufferAsync() {
        try {
            CompletableFuture future = onedriveFileApi.put(testBuffer, remoteBufferFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(checkFileExist(remoteBufferFileName));
                    checkFileContent(new String(testBuffer), remoteBufferFileName);
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_203_PutInputStreamAsync() {
        try {
            CompletableFuture future = onedriveFileApi.put(createInputStream(testFilepath), remoteInputFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(checkFileExist(remoteInputFileName));
                    try {
                        checkFileContent(readFromInputStream(createInputStream(testFilepath)), remoteInputFileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }


    @Test
    public void test_204_PutReaderAsync() {
        try {
            CompletableFuture future = onedriveFileApi.put(createReader(stringReader), remoteReaderFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(checkFileExist(remoteReaderFileName));
                    checkFileContent(stringReader, remoteReaderFileName);
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_205_SizeAsync() {
        try {
            CompletableFuture future = onedriveFileApi.size(remoteBufferFileName, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Long result) {
                    assertEquals(testBuffer.length, result.longValue());
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_206_getAsStringAsync() {
        try {
            CompletableFuture future = onedriveFileApi.getAsString(remoteStringFileName, new Callback<String>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(String result) {
                    assertEquals(testString, result);
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_207_getAsBufferAsync() {
        try {
            CompletableFuture future = onedriveFileApi.getAsBuffer(remoteBufferFileName, new Callback<byte[]>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(byte[] result) {
                    assertArrayEquals(testBuffer, result);
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_208_getOutputAsync() {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            CompletableFuture future = onedriveFileApi.get(remoteStringFileName, outputStream, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Long result) {
                    assertEquals(testString, outputStream.toString());
                    assertEquals(testString.length(), result.longValue());
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }


    @Test
    public void test_209_getWriterAsync() {
        try {
            Writer writer = new CharArrayWriter();
            CompletableFuture future = onedriveFileApi.get(remoteStringFileName, writer, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Long result) {
                    assertEquals(testString, writer.toString());
                    assertEquals(testString.length(), result.longValue());
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_210_listAsync() {
        try {
            CompletableFuture future = onedriveFileApi.list(new Callback<ArrayList<String>>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(ArrayList<String> result) {
                    assertEquals(4, result.size());
                    for (String fileName : result) {
                        assertTrue(checkListFileName(fileName));
                    }
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_211_DeleteAsync() {
        try {
            onedriveFileApi.delete(remoteStringFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    assertNull(e);
                }

                @Override
                public void onSuccess(Void result) {
                    try {
                        ArrayList<String> files = onedriveFileApi.list().get();
                        assertEquals(3, files.size());

                        for (String fileName : files) {
                            assertTrue(checkListFileNameAfterDel(fileName));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        assertNull(e);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

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
        } catch (HiveException e) {
            fail(e.getMessage());
            assertNull(e);
        }

    }

    @AfterClass
    public static void tearDown() {
        hiveClient.disconnect();
        hiveClient = null;
    }

    private boolean checkFileExist(String remoteFile) {
        try {
            ArrayList<String> fileList = onedriveFileApi.list().get();
            for (String fileName : fileList) {
                if (fileName.equals(remoteFile)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
        return false;
    }

    private void checkFileContent(String expected, String remoteFile) {
        try {
            String actual = onedriveFileApi.getAsString(remoteFile).get();
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        try {
            Buffer buffer = new Buffer();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                buffer.write(bytes, 0, len);
            }
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
            assertNull(e);
        }
        return "";
    }

    private InputStream createInputStream(String filepath) throws FileNotFoundException {
        return new FileInputStream(filepath);
    }

    private Reader createReader(String str) {
        return new StringReader(str);
    }

    private boolean checkListFileName(String filePath) {
        if (filePath.equals(remoteBufferFileName) ||
                filePath.equals(remoteStringFileName) ||
                filePath.equals(remoteInputFileName) ||
                filePath.equals(remoteReaderFileName)) {
            return true;
        }
        return false;
    }

    private boolean checkListFileNameAfterDel(String filePath) {
        if (filePath.equals(remoteBufferFileName) ||
                filePath.equals(remoteInputFileName) ||
                filePath.equals(remoteReaderFileName)) {
            return true;
        }
        return false;
    }
}
