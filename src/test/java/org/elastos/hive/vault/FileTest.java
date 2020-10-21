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

import static org.junit.Assert.assertTrue;

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
    public void testUploadBin() {
        try {
            OutputStream outputStream = filesApi.upload(remoteBigBin, OutputStream.class).get();
            byte[] bigStream = Utils.readImage(testBigImagePath);
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
            Utils.cacheTextFile(reader, testCacheTextFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadBin() {
        try {
            InputStream inputStream = filesApi.download(remoteBigBin, InputStream.class).get();
            Utils.cacheBinFile(inputStream, testCacheBigImagePath);
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
    public void testDeleteFile() {
        try {
            filesApi.delete(dst).get();
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

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestData.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestData.OWNERDID, TestData.PROVIDER);
            client = Client.createInstance(options);
            filesApi = client.getVault(TestData.OWNERDID).get().getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
