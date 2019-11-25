package org.elastos.hive.OneDrive;

import org.elastos.hive.Callback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.util.TestUtils;
import org.elastos.hive.vendors.onedrive.IHiveFile;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveKVTest {

    private static final String APPID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
    private static final String SCOPE = "User.Read Files.ReadWrite.All offline_access";//offline_access Files.ReadWrite
    private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316

    private static IHiveConnect hiveConnect ;
    private static HiveClient hiveClient ;
    private static IHiveFile hiveFile ;

    private String key = "KEY";
    private String[] values = {"value1","value2","value3"};
    private String newValue = "newValue";


    @Test
    public void test_00_Prepare() {
        try {
            hiveFile.deleteFile(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01_PutValue() {
        try {
            hiveFile.putValue(key,values[0].getBytes(),false).get();
            hiveFile.putValue(key,values[1].getBytes(),false).get();
            hiveFile.putValue(key,values[2].getBytes(),false).get();

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
        CompletableFuture future1 = hiveFile.putValue(key, values[0].getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });
        CompletableFuture future2 = hiveFile.putValue(key, values[1].getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });

        CompletableFuture future3 = hiveFile.putValue(key, values[2].getBytes(), false, new Callback<Void>() {
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

        assertFalse(future1.isCompletedExceptionally());
        assertFalse(future2.isCompletedExceptionally());
        assertFalse(future3.isCompletedExceptionally());

    }

    @Test
    public void test_02_GetValue() {
        try {
            ValueList valueList = hiveFile.getValue(key,false).get();
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
        CompletableFuture future = hiveFile.getValue(key, false, new Callback<ValueList>() {
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
        assertFalse(future.isCompletedExceptionally());
    }


    @Test
    public void test_03_SetValue() {
        try {
            hiveFile.setValue(key,newValue.getBytes(),false).get();
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
        hiveFile.setValue(key, newValue.getBytes(), false, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
                assertNull(e);
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });
    }

    @Test
    public void test_04_GetValue() {
        try {
            ValueList valueList = hiveFile.getValue(key,false).get();
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
        CompletableFuture future = hiveFile.getValue(key, false, new Callback<ValueList>() {
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
        assertFalse(future.isCompletedExceptionally());
    }

    @Test
    public void test_05_DeleteKey() {
        try {
            hiveFile.deleteValueFromKey(key).get();
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
        CompletableFuture future = hiveFile.deleteValueFromKey(key, new Callback<Void>() {
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
        assertFalse(future.isCompletedExceptionally());
    }

    @Test
    public void test_06_GetValue(){
        CompletableFuture future = hiveFile.getValue(key, false, new Callback<ValueList>() {
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
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void test_16_GetValue(){
        CompletableFuture future = hiveFile.getValue(key, false, new Callback<ValueList>() {
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
        assertTrue(future.isCompletedExceptionally());
    }



    @BeforeClass
    public static void setUp(){
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = HiveClient.createInstance(hiveOptions);

        HiveConnectOptions hiveConnectOptions = new OneDriveConnectOptions(APPID,SCOPE,REDIRECTURL, requestUrl -> {
            try {
                Desktop.getDesktop().browse(new URI(requestUrl));
            }
            catch (Exception e) {
                e.printStackTrace();
                fail("Authenticator failed");
            }
        });

        try {
            hiveConnect = hiveClient.connect(hiveConnectOptions);
        } catch (HiveException e) {
            e.printStackTrace();
        }
        hiveFile = hiveConnect.createHiveFile("/bar");
    }

    @AfterClass
    public static void tearDown(){
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }
}
