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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneDriveKVTest {

    private static final String CLIENTID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
    private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316
    private static final String STORE_PATH = System.getProperty("user.dir");

    private static Client client;
    private static KeyValues keyValues;

    private String stringValue = "test string value";
    private byte[] bufferValue = "test buffer value".getBytes();

    private String strKey = "strKey";
    private String bufferKey = "bufferKey";

    @Test
    public void test_00_prepare() {
        try {
            keyValues.deleteKey(strKey).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            keyValues.deleteKey(bufferKey).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_101_putStr() {
        try {
            keyValues.putValue(strKey, stringValue).get();
            keyValues.putValue(strKey, stringValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_102_putBuffer() {
        try {
            keyValues.putValue(bufferKey, bufferValue).get();
            keyValues.putValue(bufferKey, bufferValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_103_getStr() {
        try {
            ArrayList<byte[]> valueList = keyValues.getValues(strKey).get();
            assertEquals(2, valueList.size());
            for (byte[] value : valueList) {
                value.equals(stringValue.getBytes());
                assertArrayEquals(stringValue.getBytes(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_104_getBuffer() {
        try {
            ArrayList<byte[]> valueList = keyValues.getValues(bufferKey).get();
            assertEquals(2, valueList.size());
            for (byte[] value : valueList) {
                value.equals(bufferValue);
                assertArrayEquals(bufferValue, value);
                System.out.println(new String(bufferValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_105_setStr() {
        try {
            keyValues.setValue(strKey, stringValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_106_setBuffer() {
        try {
            keyValues.setValue(bufferKey, bufferValue).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_107_getStr() {
        try {
            ArrayList<byte[]> valueList = keyValues.getValues(strKey).get();
            assertEquals(1, valueList.size());
            for (byte[] value : valueList) {
                value.equals(stringValue.getBytes());
                assertArrayEquals(stringValue.getBytes(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_108_getBuffer() {
        try {
            ArrayList<byte[]> valueList = keyValues.getValues(bufferKey).get();
            assertEquals(1, valueList.size());
            for (byte[] value : valueList) {
                value.equals(bufferValue);
                assertArrayEquals(bufferValue, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_109_delKey() {
        try {
            keyValues.deleteKey(strKey).get();
            keyValues.deleteKey(bufferKey).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_110_getValue() {
        try {
            ArrayList<byte[]> strValues = keyValues.getValues(strKey).get();
            ArrayList<byte[]> bufferValues = keyValues.getValues(bufferKey).get();
            assertNull(strValues);
            assertNull(bufferValues);
        } catch (Exception e) {
            e.printStackTrace();
            assertNotNull(e);
        }
    }

    @Test
    public void test_201_putStrAsync() {
        try {


            keyValues.putValue(strKey, stringValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
            .get();

            keyValues.putValue(strKey, stringValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_202_putBufferAsync() {
        try {

            keyValues.putValue(bufferKey, bufferValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
            .get();

            keyValues.putValue(bufferKey, bufferValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_203_getStrAsync() {
        try {
            keyValues.getValues(strKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(2, result.size());
                    for (byte[] value : result) {
                        value.equals(stringValue.getBytes());
                        assertArrayEquals(stringValue.getBytes(), value);
                    }
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_204_getBufferAsync() {
        try {
            keyValues.getValues(bufferKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(2, result.size());
                    for (byte[] value : result) {
                        value.equals(bufferValue);
                        assertArrayEquals(bufferValue, value);
                    }
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_205_setStrAsync() {
        try {
            keyValues.setValue(strKey, stringValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_206_setBufferAsync() {
        try {
            keyValues.setValue(bufferKey, bufferValue, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Void result) {
                    assertTrue(true);
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_207_getStrAsync() {
        try {
            keyValues.getValues(strKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(1, result.size());
                    for (byte[] value : result) {
                        value.equals(stringValue.getBytes());
                        assertArrayEquals(stringValue.getBytes(), value);
                    }
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_208_getBufferAsync() {
        try {
            keyValues.getValues(bufferKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertEquals(1, result.size());
                    for (byte[] value : result) {
                        value.equals(bufferValue);
                        assertArrayEquals(bufferValue, value);
                    }
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_209_delKeyAsync() {
        try {
            keyValues.deleteKey(strKey, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            })
            .get();

            keyValues.deleteKey(bufferKey, new Callback<Void>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(Void result) {
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_210_getValueAsync() {
        try {
            keyValues.getValues(strKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertNull(result);
                }
            })
            .get();

            keyValues.getValues(bufferKey, new Callback<ArrayList<byte[]>>() {
                @Override
                public void onError(HiveException e) {
                }

                @Override
                public void onSuccess(ArrayList<byte[]> result) {
                    assertNull(result);
                }
            })
            .get();
        } catch (Exception e) {
            e.printStackTrace();
            assertNotNull(e);
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
                            Desktop.getDesktop().browse(new URI(requestUrl));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Assert.fail();
                        }
                    })
                    .build();

            client = Client.createInstance(options);
            client.connect();
            keyValues = client.getKeyValues();
        } catch (HiveException e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        client.disconnect();
        client = null;
    }
}
