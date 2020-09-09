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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private String testImage = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/ela.png";

    private static Client client;

    private static Files filesApi;

    private static final String folder = "cache/";
    private static String uploadUrl = "test.txt"; ///api/v1/files/uploader/test.txt

    private static String cacheFile = "test.txt";

    private static String src = "/src";

    private static String dst = "/dst";

    @Test
    public void testUploadBin() {
        try {
            filesApi.upload("ela.png", OutputStream.class ,new Callback<OutputStream>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(OutputStream result) {
                    try {
                        byte[] stream = readImage(testImage);
                        result.write(stream);
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

    @Test
    public void testDownloadBin() {
        try {
            filesApi.download("ela.png", InputStream.class, new Callback<InputStream>() {
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
    public void testUploadText() {
        try {
            filesApi.upload(uploadUrl, Writer.class, new Callback<Writer>() {
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
    public void testDownloadFile() {
        try {
            filesApi.download(cacheFile, Reader.class, new Callback<Reader>() {
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
            filesApi.list(cacheFile, new Callback<List<FileInfo>>() {
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
            filesApi.delete(cacheFile, new Callback<Boolean>() {
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
    public void testGetStatus() {
        try {
            filesApi.stat(cacheFile, new Callback<FileInfo>() {
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
            filesApi.hash(cacheFile, new Callback<String>() {
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
            String json = "{\"id\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX\",\"publicKey\":[{\"id\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX#primary\",\"type\":\"ECDSAsecp256r1\",\"controller\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX\",\"publicKeyBase58\":\"xNoB1aRBgZqG3fLMmNzK5wkuNwwDmXDYm44cu2n8siSz\"}],\"authentication\":[\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX#primary\"],\"expires\":\"2025-09-01T20:18:27Z\",\"proof\":{\"type\":\"ECDSAsecp256r1\",\"created\":\"2020-09-02T04:18:27Z\",\"creator\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX#primary\",\"signatureValue\":\"Gq6ookLCWlfsib3NttV5pR6zXZFk6AHSoauYil-RWTS1Z-4l_u_UFk7gn7TObdHS650dMwcqezHlzLsiFbVOOw\"}}";

            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAiLCAia2lkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSJ9.eyJpc3MiOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwic3ViIjoiRElEQXV0aENoYWxsZW5nZSIsImF1ZCI6ImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgiLCJub25jZSI6IjM5ZTYzYzZhLWYwYWMtMTFlYS05NjUwLTY0NWFlZGViMDc2MyIsImV4cCI6MTU5OTQ0MzQ4OH0.DPvQjDD49w2kLWeO4gbMEMo0VyHrgVdUnPPrYyJut8-EajXcmK64VXHQhfE32hHhkFZSW-5OLB5hyJZxvbcn-w"));
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
}
