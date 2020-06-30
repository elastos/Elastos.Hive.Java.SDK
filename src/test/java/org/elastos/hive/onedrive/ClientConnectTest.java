package org.elastos.hive.onedrive;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.onedrive.OneDriveOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Desktop;
import java.io.FileReader;
import java.net.URI;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientConnectTest {
    private static final String CLIENTID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";
    private static final String REDIRECTURL = "http://localhost:12345";
    private static final String STORE_PATH = System.getProperty("user.dir");
    private static String authorizeHtl = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/Authorize.html";

    private static Client client;

    @Test
    public void testConnect() {
        try {
            assertFalse(client.isConnected());
            System.out.println(client.isConnected());


            client.connect();
            assertTrue(client.isConnected());
            System.out.println(client.isConnected());

            client.disconnect();
            assertFalse(client.isConnected());
            System.out.println(client.isConnected());

        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void runScript() {
//        try {
//            ScriptEngineManager manager = new ScriptEngineManager();
//            ScriptEngine engine = manager.getEngineByName("javascript");
//            FileReader fileReader=new FileReader(jsFile);//js路径
//            engine.eval(fileReader);
//            if (engine instanceof Invocable) {
//                Invocable invocable = (Invocable) engine;
////                Double c = (Double)invocable.invokeFunction("aa", 2, 3); //调用了js的aa方法
////                System.out.println(c);
//                invocable.invokeFunction("openBrowser", "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
//            Desktop.getDesktop().browse(new URI("https://xidaokun.github.io/Authorize.html"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
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
                            Desktop.getDesktop().browse(new URI("https://xidaokun.github.io/Authorize.html"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail();
                        }
                    })
                    .build();

            client = Client.createInstance(options);
        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }
}
