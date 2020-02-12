package org.elastos.hive.onedrive;


import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.vendor.onedrive.OneDriveOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveKVTest {

    private static final String CLIENTID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
    private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316
    private static final String STORE_PATH = System.getProperty("user.dir");

    private static Client hiveClient;
    private static KeyValues onedriveKeyValueApi;

    private String stringValue = "test string value";
    private byte[] bufferValue = "test buffer value".getBytes();

    private String strKey = "strKey";
    private String bufferKey = "bufferKey";

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
                            Desktop.getDesktop().browse(new URI(requestUrl));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Assert.fail();
                        }
                    })
                    .build();

            hiveClient = Client.createInstance(options);

            hiveClient.connect();

            onedriveKeyValueApi = hiveClient.getKeyValues();
        } catch (
                HiveException e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        hiveClient.disconnect();
        hiveClient = null;
    }

    @Test
    public void test_00_prepare() {
        try {
            onedriveKeyValueApi.deleteKey(strKey).get();
            onedriveKeyValueApi.deleteKey(bufferKey).get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_101_putStr() {
        try {
            onedriveKeyValueApi.putValue(strKey, stringValue).get();
            onedriveKeyValueApi.putValue(strKey, stringValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_102_putBuffer() {
        try {
            onedriveKeyValueApi.putValue(bufferKey, bufferValue).get();
            onedriveKeyValueApi.putValue(bufferKey, bufferValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_103_getStr() {
        try {
            ArrayList<byte[]> valueList = onedriveKeyValueApi.getValues(strKey).get();
            assertEquals(2, valueList.size());
            for (byte[] value : valueList) {
                value.equals(stringValue.getBytes());
                assertArrayEquals(stringValue.getBytes(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_104_getBuffer() {
        try {
            ArrayList<byte[]> valueList = onedriveKeyValueApi.getValues(bufferKey).get();
            assertEquals(2, valueList.size());
            for (byte[] value : valueList) {
                value.equals(bufferValue);
                assertArrayEquals(bufferValue, value);
                System.out.println(new String(bufferValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_105_setStr() {
        try {
            onedriveKeyValueApi.setValue(strKey, stringValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_106_setBuffer() {
        try {
            onedriveKeyValueApi.setValue(bufferKey, bufferValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_107_getStr() {
        try {
            ArrayList<byte[]> valueList = onedriveKeyValueApi.getValues(strKey).get();
            assertEquals(1, valueList.size());
            for (byte[] value : valueList) {
                value.equals(stringValue.getBytes());
                assertArrayEquals(stringValue.getBytes(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_108_getBuffer() {
        try {
            ArrayList<byte[]> valueList = onedriveKeyValueApi.getValues(bufferKey).get();
            assertEquals(1, valueList.size());
            for (byte[] value : valueList) {
                value.equals(bufferValue);
                assertArrayEquals(bufferValue, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_109_delKey() {
        try {
            onedriveKeyValueApi.deleteKey(strKey).get();
            onedriveKeyValueApi.deleteKey(bufferKey).get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_110_getValue() {
        try {
            ArrayList<byte[]> strValues = onedriveKeyValueApi.getValues(strKey).get();
            ArrayList<byte[]> bufferValues = onedriveKeyValueApi.getValues(bufferKey).get();
            assertNull(strValues);
            assertNull(bufferValues);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_201_putStrAsync() {
        try {


            CompletableFuture future = onedriveKeyValueApi.putValue(strKey, stringValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
            future.get();

            CompletableFuture future1 = onedriveKeyValueApi.putValue(strKey, stringValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
            future1.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_202_putBufferAsync() {
        try {

            CompletableFuture future = onedriveKeyValueApi.putValue(bufferKey, bufferValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
            future.get();

            CompletableFuture future1 = onedriveKeyValueApi.putValue(bufferKey, bufferValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
            future1.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_203_getStrAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.getValues(strKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(2, result.size());
                    for (byte[] value : result) {
                        value.equals(stringValue.getBytes());
                        assertArrayEquals(stringValue.getBytes(), value);
                    }
                }
            });

            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_204_getBufferAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.getValues(bufferKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(2, result.size());
                    for (byte[] value : result) {
                        value.equals(bufferValue);
                        assertArrayEquals(bufferValue, value);
                        System.out.println(new String(bufferValue));
                    }
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_205_setStrAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.setValue(strKey, stringValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });

            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_206_setBufferAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.setValue(bufferKey, bufferValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });

            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_207_getStrAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.getValues(strKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(1, result.size());
                    for (byte[] value : result) {
                        value.equals(stringValue.getBytes());
                        assertArrayEquals(stringValue.getBytes(), value);
                    }
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_208_getBufferAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.getValues(bufferKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(1, result.size());
                    for (byte[] value : result) {
                        value.equals(bufferValue);
                        assertArrayEquals(bufferValue, value);
                    }
                }
            });
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_209_delKeyAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.deleteKey(strKey, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
            CompletableFuture future1 = onedriveKeyValueApi.deleteKey(bufferKey, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
            future.get();
            future1.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void test_210_getValueAsync() {
        try {
            CompletableFuture future = onedriveKeyValueApi.getValues(strKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertNull(result);
                }
            });
            CompletableFuture future1 = onedriveKeyValueApi.getValues(bufferKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertNull(result);
                }
            });

            future.get();
            future1.get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }
}
