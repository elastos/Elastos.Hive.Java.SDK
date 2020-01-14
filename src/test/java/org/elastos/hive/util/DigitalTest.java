package org.elastos.hive.util;

import org.elastos.hive.utils.DigitalUtil;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DigitalTest {
    byte[] dataBytes = {0, 1, -122, -96};
    int dataInt = 100000;

    @Test
    public void testBytesToInt() {
        int num = DigitalUtil.byteArrayToInt(dataBytes);
        assertEquals(dataInt, num);
    }

    @Test
    public void testIntToBytes() {
        byte[] bytes = DigitalUtil.intToByteArray(100000);
        assertArrayEquals(dataBytes, bytes);
    }
}
