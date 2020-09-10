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

import java.io.File;
import java.io.FileInputStream;
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

    private String testSmallImage = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/small.png";
    private String testBigImage = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/big.png";

    private static Client client;

    private static Files filesApi;

    private static String uploadUrl = "test.txt";

    private static String src = "/src";

    private static String dst = "/dst";

    private static String txtTest = "test.txt";

    private static String smallBinTest = "small.png";

    private static String bigBinTest = "big.png";

    @Test
    public void testUploadText() {
        try {
            Writer writer = filesApi.upload(txtTest, Writer.class).get();
            writer.write("fasjfosjfoajfsdoafjsofjdsaoifjsofjdsofjsdofjdsofjsooifj");
            writer.close();
            System.out.println("write success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadTextWithCallback() {
        try {
            filesApi.upload(txtTest, Writer.class, new Callback<Writer>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Writer result) {
                    try {
                        result.write("test remote file435fwjfpwjfwpfjwfjwfjwjfwpfjp");
                        result.flush();
                        result.close();
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
            OutputStream outputStream = filesApi.upload(bigBinTest, OutputStream.class).get();
            byte[] stream = readImage(testBigImage);
            outputStream.write(stream);
            outputStream.flush();
            outputStream.close();
            System.out.println("write success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadBinWithCallback() {
        try {
            filesApi.upload(smallBinTest, OutputStream.class ,new Callback<OutputStream>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(OutputStream result) {
                    try {
                        byte[] stream = readImage(testBigImage);
                        result.write(stream);
                        result.close();
                        assertNotNull(result);
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
    public void testDownloadBin() {
        try {
            filesApi.download(smallBinTest, InputStream.class, new Callback<InputStream>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(InputStream result) {
                    assertNotNull(result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadFile() {
        try {
            filesApi.download(txtTest, Reader.class, new Callback<Reader>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Reader result) {
                    try {
                        char[] buffer = new char[1];
                        StringBuilder sb = new StringBuilder();
                        while (result.read(buffer) != -1) {
                            sb.append(buffer);
                        }
                        System.out.println("content:" + sb.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != result) result.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListFiles() {
        try {
            filesApi.list(txtTest, new Callback<List<FileInfo>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(List<FileInfo> result) {
                    assertNotNull(result);
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
            filesApi.delete(uploadUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteFileWithCallback() {
        try {
            filesApi.delete(uploadUrl, new Callback<Boolean>() {
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
            filesApi.stat(uploadUrl, new Callback<FileInfo>() {
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
            filesApi.hash(txtTest, new Callback<String>() {
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
            String json = "{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"publicKey\":[{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"type\":\"ECDSAsecp256r1\",\"controller\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"publicKeyBase58\":\"tgmQDrEGg8QKNjy7hgm2675QFh7qUkfd4nDZ2AgZxYy5\"}],\"authentication\":[\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\"],\"verifiableCredential\":[{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#email\",\"type\":[\"BasicProfileCredential\",\"EmailCredential\",\"InternetAccountCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"email\":\"john@gmail.com\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"qEAxxPzAeSS7umKKKL-T0bMD7iUgUMnoHRsROupMjnXojLZdPF6KGmU80f7iy1nLDyuRx-dQLyIqBi0a1-vHaQ\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#passport\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"nation\":\"Singapore\",\"passport\":\"S653258Z07\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"qbb8YXBp5DiOMsBur5iwOW0eJtnnEi2P_EznGG0rSg5daR6hvuSXKjywgBi-GShTCK1QOQMiBC2LINn-XyjXJg\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#profile\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"email\":\"john@example.com\",\"language\":\"English\",\"name\":\"John\",\"nation\":\"Singapore\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"OOtRiXrGMnrmAu8D_2nwPkRhO6Qj8Hkp9qKbRiKTxSLA4wzbRtXesLav1n1FR3jFzddSSbsBGDXBzVD88B5tnw\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#twitter\",\"type\":[\"InternetAccountCredential\",\"TwitterCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"twitter\":\"@john\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"PE4NlCm1gk_dGRxJBb2XWVkYisuwsXmC_06oS7vBAnVOGpA_qYX1JWar7xTS6_oCzLSLus3IVfEXdG3xVK8gow\"}}],\"expires\":\"2024-12-27T08:53:27Z\",\"proof\":{\"type\":\"ECDSAsecp256r1\",\"created\":\"2019-12-27T08:53:27Z\",\"creator\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signatureValue\":\"2p-wukVhrDfu0N-xe2ANqMDUbAfZ4ntLcTVvL4IXkB5jD7ZJhrnyqtAsF9kT6kVkHBSKFgcxPavo7Nws7x4JMQ\"}}";

            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAiLCAia2lkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSJ9.eyJpc3MiOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwic3ViIjoiRElEQXV0aFJlc3BvbnNlIiwiYXVkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlhdCI6MTU5OTYyMjM5MiwiZXhwIjoxNjAwMjIyMzkyLCJuYmYiOjE1OTk2MjIzOTIsInByZXNlbnRhdGlvbiI6eyJ0eXBlIjoiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsImNyZWF0ZWQiOiIyMDIwLTA5LTA5VDAzOjMzOjEyWiIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbeyJpZCI6ImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jZGlkYXBwIiwidHlwZSI6WyJBcHBJZENyZWRlbnRpYWwiXSwiaXNzdWVyIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlzc3VhbmNlRGF0ZSI6IjIwMjAtMDktMDlUMDM6MzM6MTJaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDI0LTEyLTI3VDA4OjUzOjI3WiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImFwcERpZCI6ImFwcElkIn0sInByb29mIjp7InR5cGUiOiJFQ0RTQXNlY3AyNTZyMSIsInZlcmlmaWNhdGlvbk1ldGhvZCI6ImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSIsInNpZ25hdHVyZSI6IlFEbE01b0E4ZWRwMXlHUmhoN2l2N1pfUTh6cFpRMVFUbXVDckxkZ3JxVFUweFdrdF85TU9Ca0F1TldMcmhoNFpSOWNveTRDbXlGTmhtOEZrM0M1LTF3In19XSwicHJvb2YiOnsidHlwZSI6IkVDRFNBc2VjcDI1NnIxIiwidmVyaWZpY2F0aW9uTWV0aG9kIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNwcmltYXJ5IiwicmVhbG0iOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwibm9uY2UiOiIyY2YxN2Y1NC1mMjRkLTExZWEtYjJjOC02NDVhZWRlYjA3NjMiLCJzaWduYXR1cmUiOiJ0ZjI3TVRjWUxZMWlZZlFBNEdkcDRNNV83Q1MtdUdKdk5oaWZpUW5tdzVCTGp1SXdTUUpDcTVRNVViekVoZkNucDhlWkZjcGUtSF9NRWtkbnJoSjZnUSJ9fX0.ONm3xYuN1L___ktjgyHkIdmeckln81EHFvp-KOn1H56bXoo_f-v1n99ia0VJewO88L4Em4Qve1-veC5NSvgrYA"));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX", "http://localhost:5000");
            client = Client.createInstance(options);
            filesApi = client.getVault("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX").get().getFiles();
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
}
