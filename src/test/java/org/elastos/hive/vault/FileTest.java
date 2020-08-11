package org.elastos.hive.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.vendor.vault.VaultOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;

import okio.Buffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {
    private static final String clientId = "1098324333865-q7he5l91a4pqnuq9s2pt5btj9kenebkl.apps.googleusercontent.com";
    private static final String clientSecret = "0Ekmgx8dPbSxnTxxF-fqxjnz";
    private static final String redirectUri = "http://localhost:12345";
    private static final String nodeUrl = "http://127.0.0.1:5000";

    private static final String authToken = "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAifQ.eyJpc3MiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJzdWIiOiAiRElEQXV0aENyZWRlbnRpYWwiLCAiYXVkIjogIkhpdmUiLCAiaWF0IjogMTU5Njc2NDk3NCwgImV4cCI6IDE1OTY3NzQ5NzQsICJuYmYiOiAxNTk2NzY0OTc0LCAidnAiOiB7InR5cGUiOiAiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsICJjcmVhdGVkIjogIjIwMjAtMDgtMDdUMDE6NDk6MzNaIiwgInZlcmlmaWFibGVDcmVkZW50aWFsIjogW3siaWQiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNkaWRhcHAiLCAidHlwZSI6IFsiIl0sICJpc3N1ZXIiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJpc3N1YW5jZURhdGUiOiAiMjAyMC0wOC0wN1QwMTo0OTozM1oiLCAiZXhwaXJhdGlvbkRhdGUiOiAiMjAyNC0xMi0yN1QwODo1MzoyN1oiLCAiY3JlZGVudGlhbFN1YmplY3QiOiB7ImlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAiYXBwRGlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAicHVycG9zZSI6ICJkaWQ6ZWxhc3RvczppZWFBNVZNV3lkUW1WSnRNNWRhVzVob1RRcGN1VjM4bUhNIiwgInNjb3BlIjogWyJyZWFkIiwgIndyaXRlIl0sICJ1c2VyRGlkIjogImRpZDplbGFzdG9zOmlXRkFVWWhUYTM1YzFmUGUzaUNKdmloWkh4NnF1dW1ueW0ifSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAic2lnbmF0dXJlIjogIlN4RlkxQW5GLXhsU2dCTDUzYW5YdDRFOHFWNEptd0NkYUNXQVo4QmFpdnFKSTkwV2xkQ3Q4XzdHejllSm0zSlRNQTMxQjBrem5sSmVEUkJ3LXcyUU53In19XSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAicmVhbG0iOiAidGVzdGFwcCIsICJub25jZSI6ICI4NzMxNzJmNTg3MDFhOWVlNjg2ZjA2MzAyMDRmZWU1OSIsICJzaWduYXR1cmUiOiAidDYxV3dFM1pqR21EdktfZmtJM3h0ZkRGczFpNUFxVXVjZFIteEVDSVlzLTB4dHpNWGE2RTlkS0RFanJ3V2xwRjRUWElsTHduZlJWZXgzRl9KN0F6cUEifX19.";

    private static final String storePath = System.getProperty("user.dir");

    private static Client client;

    private String testFile = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

    private String remoteBufferFileName = "testBuffer.txt";
    private String remoteStringFileName = "testString.txt";
    private String remoteInputFileName  = "testInput.txt";
    private String remoteReaderFileName = "testReader.txt";

    private byte[] testBuffer = "this is test for buffer".getBytes();
    private String testString = "this is test for String";

    private String stringReader = "this is test for reader";

    private static Files filesApi;

    @Test
    public void test_00_Prepare() {
        try {
            ArrayList<String> list = filesApi.list().get();
            if (list == null || list.size() < 1)
                return;
            for (String fileName : list) {
                filesApi.delete(fileName).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_101_PutString() {
        try {
            filesApi.put(testString, remoteStringFileName).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void test_102_PutBuffer() {
        try {
            filesApi.put(testBuffer, remoteBufferFileName).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_103_PutInputStream() {
        try {
            filesApi.put(new FileInputStream(testFile), remoteInputFileName).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_104_PutReader() {
        try {
            filesApi.put(new StringReader(stringReader), remoteReaderFileName).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_105_Size() {
        try {
            long size = filesApi.size(remoteBufferFileName).get();
            assertEquals(testBuffer.length, size);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_106_getAsString() {
        try {
            String result = filesApi.getAsString(remoteStringFileName).get();
            assertEquals(testString, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_107_getAsBuffer() {
        try {
            byte[] result = filesApi.getAsBuffer(remoteBufferFileName).get();
            assertArrayEquals(testBuffer, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_108_getOutput() {
        try {
            OutputStream output = new ByteArrayOutputStream();
            long size = filesApi.get(remoteInputFileName, output).get();

            File file = new File(testFile);
            assertEquals(file.length(), size);
            assertEquals(readFromInputStream(new FileInputStream(testFile)),output.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_109_getWriter() {
        try {
            Writer writer = new CharArrayWriter();
            long size = filesApi.get(remoteReaderFileName, writer).get();
            assertEquals(stringReader, writer.toString());
            assertEquals(stringReader.length(), size);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_110_list() {
        try {
            ArrayList<String> files = filesApi.list().get();
            assertEquals(4, files.size());
            for (String fileName : files) {
                assertTrue(checkListFileName(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_111_Delete() {
        try {
            filesApi.delete(remoteStringFileName).get();
            ArrayList<String> files = filesApi.list().get();
            assertEquals(3, files.size());

            for (String fileName : files) {
                assertTrue(checkListFileNameAfterDel(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void test_201_PutStringAsync() {
        try {
            filesApi.put(testString, remoteStringFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_202_PutBufferAsync() {
        try {
            filesApi.put(testBuffer, remoteBufferFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_203_PutInputStreamAsync() {
        try {
            filesApi.put(new FileInputStream(testFile), remoteInputFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void test_204_PutReaderAsync() {
        try {
            filesApi.put(new StringReader(stringReader), remoteReaderFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_205_SizeAsync() {
        try {
            filesApi.size(remoteBufferFileName, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Long result) {
                    assertEquals(testBuffer.length, result.longValue());
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_206_getAsStringAsync() {
        try {
            filesApi.getAsString(remoteStringFileName, new Callback<String>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(String result) {
                    assertEquals(testString, result);
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_207_getAsBufferAsync() {
        try {
            filesApi.getAsBuffer(remoteBufferFileName, new Callback<byte[]>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(byte[] result) {
                    assertArrayEquals(testBuffer, result);
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_208_getOutputAsync() {
        try {
            OutputStream ouput = new ByteArrayOutputStream();
            filesApi.get(remoteInputFileName, ouput, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Long result) {
                    try {
                        File file = new File(testFile);
                        assertEquals(file.length(), result.longValue());
                        assertEquals(readFromInputStream(new FileInputStream(testFile)), ouput.toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        fail();
                    }
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void test_209_getWriterAsync() {
        try {
            Writer writer = new CharArrayWriter();
            filesApi.get(remoteReaderFileName, writer, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Long result) {
                    assertEquals(stringReader, writer.toString());
                    assertEquals(stringReader.length(), result.longValue());
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_210_listAsync() {
        try {
            filesApi.list(new Callback<ArrayList<String>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(ArrayList<String> result) {
                    assertEquals(4, result.size());
                    for (String fileName : result) {
                        assertTrue(checkListFileName(fileName));
                    }
                }
            })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_211_DeleteAsync() {
        try {
            filesApi.delete(remoteStringFileName, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    try {
                        ArrayList<String> files = filesApi.list().get();
                        assertEquals(3, files.size());

                        for (String fileName : files) {
                            assertTrue(checkListFileNameAfterDel(fileName));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            Client.Options options = new VaultOptions
                    .Builder()
                    .setNodeUrl(nodeUrl)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectURL(redirectUri)
                    .setAuthToken(authToken)
                    .setStorePath(storePath)
                    .setAuthenticator(requestUrl -> {
                        try {
                            Desktop.getDesktop().browse(new URI(requestUrl));
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail();
                        }
                    })
                    .build();

            client = Client.createInstance(options);
            client.connect();
            filesApi = client.getFiles();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        client.disconnect();
        client = null;
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

    private boolean checkListFileName(String filePath) {
        return (filePath.equals(remoteBufferFileName) ||
                filePath.equals(remoteStringFileName) ||
                filePath.equals(remoteInputFileName) ||
                filePath.equals(remoteReaderFileName));
    }

    private boolean checkListFileNameAfterDel(String filePath) {
        return (filePath.equals(remoteBufferFileName) ||
                filePath.equals(remoteInputFileName) ||
                filePath.equals(remoteReaderFileName));
    }
}
