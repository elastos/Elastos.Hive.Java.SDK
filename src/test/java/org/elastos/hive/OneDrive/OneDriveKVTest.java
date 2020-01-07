package org.elastos.hive.OneDrive;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.ClientOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.ConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.util.TestUtils;
import org.elastos.hive.result.Void;
import org.elastos.hive.vendors.onedrive.OneDriveConnectOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveKVTest {

    private static final String APPID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
    private static final String SCOPE = "User.Read Files.ReadWrite.All offline_access";//offline_access Files.ReadWrite
    private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316
    private static final String STORE_PATH = System.getProperty("user.dir");

    private static HiveConnect hiveConnect ;
    private static Client hiveClient ;

    private String key = "KEY";
    private String[] values = {"value1","value2","value3"};
    private String newValue = "newValue";


    @Test
    public void test_00_Prepare() {
        try {
            hiveConnect.deleteFile(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01_PutValue() {
        try {
            hiveConnect.putValue(key,values[0].getBytes(),false).get();
            hiveConnect.putValue(key,values[1].getBytes(),false).get();
            hiveConnect.putValue(key,values[2].getBytes(),false).get();

        } catch (InterruptedException e) {
            assertNull(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void test_11_PutValueAsync() {
        CompletableFuture future1 = hiveConnect.putValue(key, values[0].getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });
        CompletableFuture future2 = hiveConnect.putValue(key, values[1].getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });

        CompletableFuture future3 = hiveConnect.putValue(key, values[2].getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });

        TestUtils.waitFinish(future1);
        TestUtils.waitFinish(future2);
        TestUtils.waitFinish(future3);
    }

    @Test
    public void test_02_GetValue() {
        try {
            ValueList valueList = hiveConnect.getValue(key,false).get();
            ArrayList<Data> arrayDatas = valueList.getList();
            assertEquals(3,arrayDatas.size());
            for (int i = 0 ; i < arrayDatas.size() ; i++){
                assertArrayEquals(values[i].getBytes(), arrayDatas.get(i).getData());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_12_GetValueAsync() {
        CompletableFuture future = hiveConnect.getValue(key, false, new Callback<ValueList>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(ValueList body) {
                ArrayList<Data> arrayDatas = body.getList();
                assertEquals(3,arrayDatas.size());
                for (int i = 0 ; i < arrayDatas.size() ; i++){
                    assertArrayEquals(values[i].getBytes(), arrayDatas.get(i).getData());
                }
            }
        });
        TestUtils.waitFinish(future);
    }


    @Test
    public void test_03_SetValue() {
        try {
            hiveConnect.setValue(key,newValue.getBytes(),false).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_13_SetValueAsync() {
        CompletableFuture future = hiveConnect.setValue(key, newValue.getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });
        TestUtils.waitFinish(future);
    }

    @Test
    public void test_04_GetValue() {
        try {
            ValueList valueList = hiveConnect.getValue(key,false).get();
            ArrayList<Data> arrayDatas = valueList.getList();
            assertEquals(1,arrayDatas.size());
            for (int i = 0 ; i < arrayDatas.size() ; i++){
                assertArrayEquals(newValue.getBytes(), arrayDatas.get(i).getData());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_14_GetValueAsync() {
        CompletableFuture future = hiveConnect.getValue(key, false, new Callback<ValueList>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(ValueList body) {
                ArrayList<Data> arrayDatas = body.getList();
                assertEquals(1,arrayDatas.size());
                for (int i = 0 ; i < arrayDatas.size() ; i++){
                    assertArrayEquals(newValue.getBytes(), arrayDatas.get(i).getData());
                }
            }
        });
        TestUtils.waitFinish(future);
    }

    @Test
    public void test_05_DeleteKey() {
        try {
            hiveConnect.deleteValueFromKey(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNull(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_15_DeleteKeyAsync() {
        CompletableFuture future = hiveConnect.deleteValueFromKey(key, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });

        TestUtils.waitFinish(future);
    }

    @Test
    public void test_06_GetValue(){
        CompletableFuture future = hiveConnect.getValue(key, false, new Callback<ValueList>() {
            @Override
            public void onError(HiveException e) {
                assertNotNull(e);
                assertEquals("Item not found.",e.getMessage());
            }

            @Override
            public void onSuccess(ValueList body) {
            }
        });
        TestUtils.waitFinish(future);
    }

    @Test
    public void test_16_GetValue(){
        CompletableFuture future = hiveConnect.getValue(key, false, new Callback<ValueList>() {
            @Override
            public void onError(HiveException e) {
                assertNotNull(e);
                assertEquals("Item not found.",e.getMessage());
            }

            @Override
            public void onSuccess(ValueList body) {
            }
        });
        TestUtils.waitFinish(future);
    }



    @BeforeClass
    public static void setUp(){
        ClientOptions hiveOptions = new ClientOptions.Builder().setStorePath(STORE_PATH).build();
        hiveClient = new Client(hiveOptions);

        ConnectOptions hiveConnectOptions =
                new OneDriveConnectOptions.Builder()
                        .setClientId(APPID)
                        .setRedirectUrl(REDIRECTURL)
                        .setAuthenticator(requestUrl -> {
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
    public static void tearDown(){
        hiveClient.disConnect(hiveConnect);
    }
}
