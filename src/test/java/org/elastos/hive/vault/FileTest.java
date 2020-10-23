package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.Files;
import org.elastos.hive.file.FileInfo;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

    private String textLocalPath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";
    private String imgLocalPath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/big.png";

    private String textLocalCachePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/cache/test.txt";
    private String imgLocalCachePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/cache/big.png";

    private static String remoteFolder = "hive";
    private static String remoteTextPath = remoteFolder + File.separator + "test.txt";
    private static String remoteImgPath = remoteFolder + File.separator + "big.png";

    private static String remoteTextBackupPath = "backup" + File.separator + "test.txt";
    private static String remoteImgBackupPath = "backup" + File.separator + "big.png";

    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";
    private static Client client;
    private static Files filesApi;

    @Test
    public void test00_clean() {
        try {
            filesApi.delete(remoteTextPath).get();
            filesApi.delete(remoteImgPath).get();
            filesApi.delete(remoteTextBackupPath).get();
            filesApi.delete(remoteImgBackupPath).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test01_uploadText() {
        FileReader fileReader = null;
        Writer writer = null;
        try {
             writer = filesApi.upload(remoteTextPath, Writer.class).get();
            fileReader = new FileReader(new File(textLocalPath));
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
    public void test02_uploadBin() {
        try {
            OutputStream outputStream = filesApi.upload(remoteImgPath, OutputStream.class).get();
            byte[] bigStream = Utils.readImage(imgLocalPath);
            outputStream.write(bigStream);
            outputStream.close();
            System.out.println("write success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test03_downloadText() {
        try {
            Reader reader = filesApi.download(remoteTextPath, Reader.class).get();
            Utils.cacheTextFile(reader, textLocalCachePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test04_downloadBin() {
        try {
            InputStream inputStream = filesApi.download(remoteImgPath, InputStream.class).get();
            Utils.cacheBinFile(inputStream, imgLocalCachePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test05_list() {
        try {
            List<FileInfo> result = filesApi.list(remoteFolder).get();
            assertNotNull(result);
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
    public void test06_hash() {
        try {
            String hash = filesApi.hash(remoteTextPath).get();
            assertNotNull(hash);
            System.out.println("hash=" + hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test07_copy() {
        try {
            boolean success = filesApi.copy(remoteTextPath, remoteTextBackupPath).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test08_move() {
        try {
            boolean success = filesApi.move(remoteImgPath, remoteImgBackupPath).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test09_deleteFile() {
        try {
            filesApi.delete(remoteTextBackupPath).get();
            filesApi.delete(remoteImgBackupPath).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @BeforeClass
    public static void setUp() {
        try {
            String json = TestData.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.setLocalPath(localDataPath);
            Client.setResolverURL("http://api.elastos.io:21606");
            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestData.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);

            client = Client.createInstance(options);
            client.setVaultProvider(TestData.OWNERDID, TestData.PROVIDER);
            filesApi = client.getVault(TestData.OWNERDID).get().getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
