package org.elastos.hive.result;

public class Data extends Result {
    private final byte[] data;

    public Data(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
