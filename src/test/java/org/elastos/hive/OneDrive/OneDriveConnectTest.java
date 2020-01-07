package org.elastos.hive.OneDrive;

import org.elastos.hive.HiveClient;
import org.elastos.hive.ClientOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.ConnectOptions;
import org.elastos.hive.vendors.onedrive.OneDriveConnectOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Desktop;
import java.net.URI;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;

public class OneDriveConnectTest {
    private static HiveConnect hiveConnect ;
    private static HiveClient hiveClient ;

    private static final String APPID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
    private static final String SCOPE = "User.Read Files.ReadWrite.All offline_access";//offline_access Files.ReadWrite
    private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testGetInstance() {
        assertNotNull(hiveConnect);
    }

    @BeforeClass
    public static void setUp(){
        ClientOptions hiveOptions = new ClientOptions.Builder().setStorePath(STORE_PATH).build();
        hiveClient = new HiveClient(hiveOptions);
    }

    @Test
    public void testConnect() {
        ConnectOptions hiveConnectOptions =
                new OneDriveConnectOptions.Builder()
                        .clientId(APPID)
                        .scope(SCOPE)
                        .redirectUrl(REDIRECTURL)
                        .authenticator(requestUrl -> {
                            try {
                                Desktop.getDesktop().browse(new URI(requestUrl));
                            } catch (Exception e) {
                                e.printStackTrace();
                                fail("Authenticator failed");
                            }
                        }).build();
        hiveConnect = hiveClient.connect(hiveConnectOptions);
    }

    @AfterClass
    public static void tearDown() {
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }
}
