package org.elastos.hive.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.VaultFiles;
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
import static org.junit.Assert.assertFalse;
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
    private static final String remoteFile = "test.txt";
    private String stringReader = "this is test for reader";

    private static Client client;

    private String testFile = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

    private static VaultFiles filesApi;

    private String uploadUrl;

    @Test
    public void test_create_file() {
        try {
            uploadUrl = filesApi.createFile(remoteFile).get();
            assertFalse(uploadUrl == null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_upload_file() {
        try {
            filesApi.upload(uploadUrl, stringReader.getBytes(), stringReader);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_download_file01() {

        try {
            OutputStream output = new ByteArrayOutputStream();
            long size = filesApi.downloader(remoteFile, output).get();

            File file = new File(testFile);
            assertEquals(file.length(), size);
            assertEquals(readFromInputStream(new FileInputStream(testFile)),output.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_download_file02() {

        try {
            Writer writer = new CharArrayWriter();
            long size = filesApi.downloader(remoteFile, writer).get();
            assertEquals(stringReader, writer.toString());
            assertEquals(stringReader.length(), size);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_list_file() {

    }

    @Test
    public void test_delete_file() {
        try {
            filesApi.deleteFile(remoteFile);
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
            filesApi = client.getVaultFiles();
        } catch (Exception e) {
            fail(e.getMessage());
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

}
