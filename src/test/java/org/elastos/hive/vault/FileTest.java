package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Files;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.file.FileInfo;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private String testTextFilePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";
    private String testSmallImagePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/small.png";
    private String testBigImagePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/big.png";

    private String testCacheTextFilePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/cache/test.txt";
    private String testCacheSmallImagePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/cache/small.png";
    private String testCacheBigImagePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/cache/big.png";


    private static Client client;

    private static Files filesApi;

    private static String rootPath = "hive";
    private static String dstPath = "backup";

    private static String remoteText = rootPath + File.separator + "test.txt";

    private static String remoteSmallBin = rootPath + File.separator + "hive/small.png";

    private static String remoteBigBin = rootPath + File.separator + "big.png";

    private static String src = remoteText;

    private static String dst = dstPath + File.separator + "test.txt";

    @Test
    public void testUploadTextNoCallback() {
        FileReader fileReader = null;
        Writer writer = null;
        try {
             writer = filesApi.upload(remoteText, Writer.class).get();
            fileReader = new FileReader(new File(testTextFilePath));

            char[] buffer = new char[1];
            while (fileReader.read(buffer) != -1) {
                writer.write(buffer);
            }
            System.out.println("write success");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=fileReader) fileReader.close();
                if(null!=writer) writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testUploadTextWithCallback() {
        try {
            filesApi.upload(remoteText, Writer.class, new Callback<Writer>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Writer result) {
                    try {
                        result.write("test remote file435fwjfpwjfwpfjwfjwfjwjfwpfjp");
                        result.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadBin() {
        try {
            OutputStream outputStream = filesApi.upload(remoteBigBin, OutputStream.class).get();
            byte[] bigStream = readImage(testBigImagePath);
            outputStream.write(bigStream);
            outputStream.close();
            System.out.println("write success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadFileWNoCallback() {
        try {
            Reader reader = filesApi.download(remoteText, Reader.class).get();
            cacheTextFile(reader, testCacheTextFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadFileWithCallback() {
        try {
            filesApi.download(remoteText, Reader.class, new Callback<Reader>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Reader result) {
                    cacheTextFile(result, testCacheTextFilePath);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadBin() {
        try {
            InputStream inputStream = filesApi.download(remoteBigBin, InputStream.class).get();
            cacheBinFile(inputStream, testCacheBigImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListFilesNoCallback() {
        try {
            List<FileInfo> result = filesApi.list(rootPath).get();
            if(null == result) return;
            assertTrue(result.size()>0);
            System.out.println("list size=" + result.size());
            for(FileInfo fileInfo : result) {
                System.out.println("type=" + fileInfo.getType());
                System.out.println("name=" + fileInfo.getName());
                System.out.println("fileSize=" + fileInfo.getSize());
                System.out.println("lastModify=" + fileInfo.getLastModify());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListFilesWithCallback() {
        try {
            filesApi.list(remoteText, new Callback<List<FileInfo>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(List<FileInfo> result) {
                    System.out.println("size=" + result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCopyFile() {
        try {
            filesApi.copy(src, dst, new Callback<Boolean>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Boolean result) {
                    assertTrue(result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMoveFile() {
        try {
            filesApi.move(src, dst, new Callback<Boolean>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Boolean result) {

                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteFile() {
        try {
            filesApi.delete(dst).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteFileWithCallback() {
        try {
            filesApi.delete(dst, new Callback<Boolean>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Boolean result) {
                    assertTrue(result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStatus() {
        try {
            filesApi.stat(dst, new Callback<FileInfo>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(FileInfo result) {
                    assertNotNull(result);
                    System.out.println("name=" + result.getName());
                    System.out.println("type=" + result.getType());
                    System.out.println("size=" + result.getSize());
                    System.out.println("date=" + result.getLastModify());
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetHash() {
        try {
            filesApi.hash(dst, new Callback<String>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(String result) {
                    assertNotNull(result);
                    System.out.println("hash=" + result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            String json = TestConstance.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestConstance.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestConstance.OWNERDID, TestConstance.PROVIDER);
            client = Client.createInstance(options);
            filesApi = client.getVault(TestConstance.OWNERDID).get().getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] readImage(String path) {
        try {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(path);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                return buffer;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cacheTextFile(Reader reader, String storePath) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(storePath));
            char[] buffer = new char[1];
            while (reader.read(buffer) != -1) {
                fileWriter.write(buffer);
            }
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cacheBinFile(InputStream inputStream, String storePath) {
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=inputStream.read(buffer)) != -1 ) {
                outStream.write(buffer, 0, len);
            }

            byte[] data = outStream.toByteArray();
            File imageFile = new File(storePath);
            FileOutputStream fileOutStream = new FileOutputStream(imageFile);
            fileOutStream .write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
